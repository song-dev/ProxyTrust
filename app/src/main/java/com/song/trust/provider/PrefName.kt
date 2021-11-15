package com.song.trust.provider

/**
 * Created by chensongsong on 2021/11/15.
 */
class PrefName private constructor(val prefName: String, val isMatch: Boolean) {

    private constructor(prefName: String) : this(prefName, false)

    companion object {
        fun parse(prefNames: Array<String>): Array<PrefName?> {
            val prefNameArray = arrayOfNulls<PrefName>(prefNames.size)
            for (i in prefNames.indices) {
                prefNameArray[i] = PrefName(prefNames[i])
            }
            return prefNameArray
        }
    }
}