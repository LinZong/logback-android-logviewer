package com.nemesiss.dev.logback_android_logviewer.utils

import java.io.DataOutputStream
import java.lang.Exception

object Utils {
    fun tryApplyRootPermission(path: String): Boolean {
        val cmd = "chmod -R 777 $path"
        var proc: Process? = null
        var os: DataOutputStream? = null
        try {
            proc = Runtime.getRuntime().exec("su")
            os = DataOutputStream(proc.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()
            proc.waitFor()
            return true
        } catch (e: Exception) {
            return false
        } finally {
            try {
                os?.close()
                proc?.destroy()
            } catch (ignore: Throwable) {
            }
        }
    }
}