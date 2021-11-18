package com.song.trust.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

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

}