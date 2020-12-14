package com.nemesiss.dev.logback_android_logviewer

import android.app.Application

class LogViewerDemoApplication : Application() {

    companion object {
        init {
            System.setProperty("project.name", "LogViewerDemoApplication")
            System.setProperty("log.platform", "ANDROID")
            System.setProperty("log.debug", "false")
        }
    }
}