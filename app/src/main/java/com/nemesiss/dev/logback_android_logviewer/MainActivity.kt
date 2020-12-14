package com.nemesiss.dev.logback_android_logviewer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.nemesiss.dev.logback_android_logviewer.activity.LogFileExplorerActivity
import org.slf4j.getLogger
import java.util.*

class MainActivity : AppCompatActivity() {

    private val log = getLogger<MainActivity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    private fun initComponents() {
        log.warn("MainActivity is started at: {})", Date())
    }

    fun startLogViewerActivity(view: View) {
        simpleStartActivity<LogFileExplorerActivity>()
    }
}

inline fun <reified T : Activity> Activity.simpleStartActivity() {
    startActivity(Intent(this, T::class.java))
}
