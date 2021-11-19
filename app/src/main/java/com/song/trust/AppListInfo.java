package com.song.trust;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chensongsong on 2021/11/19.
 */
public class AppListInfo {

    /**
     * 获取应用列表
     *
     * @param context
     * @return
     */
    @SuppressLint("QueryPermissionsNeeded")
    public static List<ApplicationBean> getAppListInfo(Context context) {
        String packageName = getSelectedPackageName(context);
        List<ApplicationBean> list = new ArrayList<>();
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : applications) {
            ApplicationBean bean = new ApplicationBean();
            bean.setName(applicationInfo.loadLabel(packageManager).toString());
            bean.setPackageName(applicationInfo.packageName);
            bean.setIcon(applicationInfo.loadIcon(packageManager));
            if ((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0) {
                bean.setConfigured(bean.getPackageName().equals(packageName));
                list.add(bean);
            }
        }
        Collections.sort(list);
        if (!TextUtils.isEmpty(packageName)) {
            list.add(0, new ApplicationBean().setTitle("已选应用"));
            list.add(2, new ApplicationBean().setTitle("待选应用"));
        } else {
            list.add(0, new ApplicationBean().setTitle("待选应用"));
        }
        return list;
    }

    private static String getSelectedPackageName(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("package_select", null);
    }

}
