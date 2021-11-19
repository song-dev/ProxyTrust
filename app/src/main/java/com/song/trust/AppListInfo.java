package com.song.trust;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static List<ApplicationBean> getAppListInfo(Context context) {
        Set<String> set = getPreferencesKeySet(context);
        List<ApplicationBean> list = new ArrayList<>();
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> applications = packageManager.getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : applications) {
            ApplicationBean bean = new ApplicationBean();
            bean.setName(applicationInfo.loadLabel(packageManager).toString());
            bean.setPackageName(applicationInfo.packageName);
            bean.setIcon(applicationInfo.loadIcon(packageManager));
            if ((ApplicationInfo.FLAG_SYSTEM & applicationInfo.flags) == 0) {
                bean.setConfigured(set.contains(bean.getPackageName()));
                list.add(bean);
            }
        }
        Collections.sort(list);
        if (!set.isEmpty()) {
            list.add(0, new ApplicationBean().setTitle("已配置"));
            list.add(set.size() + 1, new ApplicationBean().setTitle("已安装"));
        } else {
            list.add(0, new ApplicationBean().setTitle("已安装"));
        }
        return list;
    }

    private static Set<String> getPreferencesKeySet(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> all = preferences.getAll();
        return all.keySet();
    }

}
