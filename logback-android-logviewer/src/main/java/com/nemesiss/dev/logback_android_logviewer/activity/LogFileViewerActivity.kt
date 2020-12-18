package com.nemesiss.dev.logback_android_logviewer.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.nemesiss.dev.logback_android_logviewer.R
import kotlinx.android.synthetic.main.activity_log_file_viewer.*
import java.io.File

class LogFileViewerActivity : AppCompatActivity() {

    companion object {

        private const val LOGFILE_ABS_PATH_INTENT_KEY = "LOGFILE_ABS_PATH"

        fun start(activity: Activity, fileAbsPath: String) {
            activity.startActivity(Intent(activity, LogFileViewerActivity::class.java).apply {
                putExtra(LOGFILE_ABS_PATH_INTENT_KEY, fileAbsPath)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_file_viewer)
        val logFilePath = intent.getStringExtra(LOGFILE_ABS_PATH_INTENT_KEY) ?: ""
        parseLogFile(logFilePath)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun parseLogFile(logFilePath: String) {
        Thread {
            try {
                val code = File(logFilePath).readText()
                runOnUiThread {
                    code_view.setCode(code)
                    title = logFilePath.substring(logFilePath.lastIndexOf("/") + 1)
                    switchLoadingPlaceholder()
                }
            } catch (e: Exception) {
                Log.e("LogFileViewerActivity", e.message, e)
                runOnUiThread {
                    handleParseFailed(logFilePath)
                }
            }
        }.start()
    }

    private fun switchLoadingPlaceholder() {
        code_view.visibility = View.VISIBLE
        code_loading_container.visibility = View.GONE
    }

    private fun handleParseFailed(logFilePath: String) {
        code_loading_icon.setImageDrawable(getDrawable(R.drawable.code_loadfailed))
        code_loading_hint.text = getString(R.string.code_loading_failed) + logFilePath
    }
}
