package com.song.trust.applist

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.text.TextUtils
import androidx.preference.PreferenceManager
import com.song.trust.utils.CommonUtils
import java.util.*

/**
 * Created by chensongsong on 2021/11/19.
 */
object AppListInfo {
    /**
     * 获取应用列表
     *
     * @param context
     * @return
     */
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun getAppListInfo(context: Context): List<ApplicationBean> {
        val packageName = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("package_select", null)
        val list: MutableList<ApplicationBean> = ArrayList()
        try {
            val packageManager = context.applicationContext.packageManager
            val applications = packageManager.getInstalledApplications(0)
            if (applications.isNotEmpty()) {
                for (applicationInfo in applications) {
                    val bean = ApplicationBean()
                    bean.name = applicationInfo.loadLabel(packageManager).toString()
                    bean.packageName = applicationInfo.packageName
                    bean.icon = applicationInfo.loadIcon(packageManager)
                    if (ApplicationInfo.FLAG_SYSTEM and applicationInfo.flags == 0) {
                        bean.isConfigured = bean.packageName == packageName
                        list.add(bean)
                    }
                }
                list.sort()
                if (!TextUtils.isEmpty(packageName)
                    && CommonUtils.isPkgInstalled(context, packageName)
                ) {
                    list.add(0, ApplicationBean().setTitle("已选应用"))
                    list.add(2, ApplicationBean().setTitle("待选应用"))
                } else {
                    list.add(0, ApplicationBean().setTitle("待选应用"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

}