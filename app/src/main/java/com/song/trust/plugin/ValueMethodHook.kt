package com.song.trust.plugin

import de.robv.android.xposed.XC_MethodHook

class ValueMethodHook(private val value: Any?) : XC_MethodHook() {

    @Throws(Throwable::class)
    public override fun afterHookedMethod(param: MethodHookParam) {
        super.beforeHookedMethod(param)
        if (value is Throwable) {
            param.throwable = value
        } else {
            param.result = value
        }
    }

}