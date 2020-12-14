package com.nemesiss.dev.logback_android_logviewer.adapter.io

import android.content.Context
import android.text.TextUtils
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.util.FileUtil
import ch.qos.logback.core.util.OptionHelper
import java.io.File

class LogbackConfigurationPropertyLogDirectoryAdapter constructor(private val context: Context) :
    LogDirectoryAdapter {

    companion object {
        const val LOG_SEARCH_DIR_KEY = CoreConstants.LOGVIEWER_SEARCH_DIR_KEY

        private const val TAG = "LogbackConfigPropertyLDA"
    }

    override fun getLogDirectoriesAbsStr(): List<String> {
        val logDirs = OptionHelper.getSystemProperty(LOG_SEARCH_DIR_KEY, "")
        if (TextUtils.isEmpty(logDirs)) {
            return emptyList()
        }
        val dirs = logDirs.split(";")
        val prefix = context.filesDir.absolutePath
        return dirs.map { dir -> FileUtil.prefixRelativePath(prefix, dir) }
    }

    override fun getLogDirectories(): List<File> = getLogDirectoriesAbsStr().map { dir -> File(dir) }
}