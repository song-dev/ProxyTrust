package com.song.trust.provider

import com.song.trust.utils.Constants

/**
 * Created by chensongsong on 2021/11/15.
 */
class XposedPreferencesProvider :
    PreferencesProvider(Constants.AUTHORITIES, arrayOf(Constants.PREFName))