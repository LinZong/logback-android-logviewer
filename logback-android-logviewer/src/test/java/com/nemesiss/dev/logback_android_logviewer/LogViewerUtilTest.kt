package com.nemesiss.dev.logback_android_logviewer

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LogViewerUtilTest {

    @Test
    fun testLastModifiedDateFormatPattern() {
        val pattern = "MMM dd | HH:mm"
        val dfChina = SimpleDateFormat(pattern, Locale.CHINA)
        val dfUs = SimpleDateFormat(pattern, Locale.US)
        assertEquals("十二月 14 | 16:43", dfChina.format(1607935382220L))
        assertEquals("Dec 14 | 16:43", dfUs.format(1607935382220L))
    }
}
