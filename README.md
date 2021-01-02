## Logback Android Version with LogViewer 用户接入指南

### 简介

为了保障端侧稳定性，保留端侧记录的部分日志，降低排查问题的难度，所以把服务端常用的日志组件`slf4j+logback`的组合移植到了Android端。以下是简单的接入方式

### 快速接入

1. 项目根`build.gradle`添加私服依赖

   ```groovy
   buildscript {
       ext.kotlin_version = '1.3.61'
       repositories {
           maven { url 'http://47.107.119.93:8090/repository/maven-public/' }
       }
   }
   
   allprojects {
       repositories {
           maven { url 'http://47.107.119.93:8090/repository/maven-public/' }
       }
   }
   ```
   
2. `app`模块内的`build.gradle`添加以下依赖

   ```groovy
       // add viewer support
       implementation 'com.github.tony19:logback-android:2.0.2-SNAPSHOT'
       // log viewer
       implementation('com.nemesiss.dev:logback-android-logviewer:1.0.0-SNAPSHOT') {
           exclude group: 'com.android.support.constraint', module: 'constraint-layout'
       }
   ```

3. 同步一次gradle

4. 设置系统属性

   在继承的`Application`类中，如果是Kotlin，要在`companion object`块中添加`init`块，写以下配置

   ```kotlin
       companion object {
           init {
               System.setProperty("project.name", "App名字")
               System.setProperty("log.platform", "ANDROID")
               System.setProperty("log.debug", "false")
           }
       }
   ```

   如果是Java，则写`static`块

   ```java
   static {
       System.setProperty("project.name", "App名字")
       System.setProperty("log.platform", "ANDROID")
       System.setProperty("log.debug", "false")
   }
   ```

5. 复制一份`logback.xml`到`assets`文件夹下，下面给个例子作参考

   ```xml
   <configuration>
       <property name="ANDROID_DIR" value="${DATA_DIR}/logs"/>
       <property name="PC_DIR" value="${user.home}/${project.name}/logs"/>
       <property name="LOGVIEWER_SEARCH_DIR" value="${ANDROID_DIR}"
                 scope="system"/>
   
       <if condition='"${log.debug}" === "false" || "${log.debug}" === "log.debug_IS_UNDEFINED"'>
           <then>
               <statusListener class="ch.qos.logback.core.status.NopStatusListener"/>
           </then>
       </if>
   
       <if condition='"${log.platform}" === "ANDROID"'>
           <then>
               <appender name="console" class="ch.qos.logback.classic.android.LogcatAppender">
                   <tagEncoder>
                       <pattern>%logger{12}</pattern>
                   </tagEncoder>
                   <encoder>
                       <pattern>[%thread] %msg</pattern>
                   </encoder>
               </appender>
           </then>
           <else>
               <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
                   <tagEncoder>
                       <pattern>%logger{12}</pattern>
                   </tagEncoder>
                   <encoder>
                       <pattern>[%date{yyyy-M-d HH:mm:ss.SSS}] [%level] [%thread] %msg%n</pattern>
                   </encoder>
               </appender>
           </else>
       </if>
   
       <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
           <file>${${log.platform}_DIR}/applog.log</file>
           <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
               <fileNamePattern>applog-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
               <maxFileSize>50MB</maxFileSize>
               <maxHistory>7</maxHistory>
               <totalSizeCap>350MB</totalSizeCap>
           </rollingPolicy>
           <tagEncoder>
               <pattern>%logger{12}</pattern>
           </tagEncoder>
           <encoder>
               <pattern>[%date{yyyy-M-d HH:mm:ss.SSS}] [%level] [%thread] %msg%n</pattern>
           </encoder>
       </appender>
   
       <appender name="crash" class="ch.qos.logback.core.rolling.RollingFileAppender">
           <file>${${log.platform}_DIR}/crash.log</file>
           <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
               <fileNamePattern>crash-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
               <maxFileSize>50MB</maxFileSize>
               <maxHistory>7</maxHistory>
               <totalSizeCap>350MB</totalSizeCap>
           </rollingPolicy>
           <tagEncoder>
               <pattern>%logger{12}</pattern>
           </tagEncoder>
           <encoder>
               <pattern>[%date{yyyy-M-d HH:mm:ss.SSS}] [%level] [%thread] %msg%n</pattern>
           </encoder>
       </appender>
   
       <root level="INFO">
           <appender-ref ref="console"/>
           <appender-ref ref="file"/>
       </root>
   
       <logger name="crash" level="INFO" additivity="false">
           <appender-ref ref="crash"/>
       </logger>
   </configuration>
   ```

6. 在需要的地方使用

   ```java
   private static Logger log = LoggerFactory.getLogger("LoggerName");
   ```

   获取Logger。
   
7. 如果想在端侧启动日志文件查看器，可以

   ```kotlin
   startActivity(Intent(this, LogFileExplorerActivity::class.java))
   ```