package com.song.trust.utils

import android.content.Context
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream

/**
 * Created by chensongsong on 2021/11/18.
 */
object CommonUtils {

    fun isMessyCode(strName: String): Boolean {
        try {
            val p: Pattern = Pattern.compile("\\s*|\t*|\r*|\n*")
            val m: Matcher = p.matcher(strName)
            val after: String = m.replaceAll("")
            val temp = after.replace("\\p{P}".toRegex(), "")
            val ch = temp.trim { it <= ' ' }.toCharArray()
            val length = ch.size
            for (i in 0 until length) {
                val c = ch[i]
                if (!Character.isLetterOrDigit(c)) {
                    val str = "" + ch[i]
                    val regex = Regex.fromLiteral("[\u4e00-\u9fa5]+")
                    if (!str.matches(regex)) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun unzip(content: ByteArray): String? {
        try {
            val outputStream = ByteArrayOutputStream()
            val inputStream = ByteArrayInputStream(content)
            val unGzip = GZIPInputStream(inputStream)
            val buffer = ByteArray(512)
            var n: Int
            while (unGzip.read(buffer).also { n = it } >= 0) {
                outputStream.write(buffer, 0, n)
            }
            return String(outputStream.toByteArray(), Charset.defaultCharset())
        } catch (e: Exception) {
            XposedLogger.log("CommonUtils.unzip: ${e.message}")
        }
        return null
    }

    fun isPkgInstalled(context: Context?, name: String?): Boolean {
        return try {
            context?.packageManager?.getPackageInfo(name!!, 0)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}