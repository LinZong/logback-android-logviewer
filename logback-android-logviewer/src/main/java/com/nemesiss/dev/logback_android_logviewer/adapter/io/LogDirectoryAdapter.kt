package com.nemesiss.dev.logback_android_logviewer.adapter.io

import java.io.File

interface LogDirectoryAdapter {
    fun getLogDirectoriesAbsStr(): List<String>
    fun getLogDirectories(): List<File>
}