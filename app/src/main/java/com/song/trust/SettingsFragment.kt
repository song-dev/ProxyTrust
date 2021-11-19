package com.song.trust

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.song.trust.applist.AppListActivity
import com.song.trust.applist.ApplicationBean
import com.song.trust.utils.CommonUtils
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/19.
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var requestDataLauncher: ActivityResultLauncher<Intent>? = null
    private var preference: Preference? = null

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestDataLauncher =
            activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getParcelableExtra<ApplicationBean>("app")
                    if (data != null) {
                        preference?.summary = "${data.name}(${data.packageName})"
                        val defaultSharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context)
                        val edit = defaultSharedPreferences.edit()
                        edit.putString("package_select", data.packageName)
                        edit.apply()
                        onSharedPreferenceChanged(defaultSharedPreferences, "package_select")
                    }
                }
            }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preference = findPreference("package_select")
        preference?.setOnPreferenceClickListener {
            val intent = Intent(this.context, AppListActivity::class.java)
            requestDataLauncher?.launch(intent)
            return@setOnPreferenceClickListener true
        }
        val name =
            PreferenceManager.getDefaultSharedPreferences(context).getString("package_select", null)
        if (name?.isNotBlank() == true) {
            if (CommonUtils.isPkgInstalled(context, name)) {
                val label = context?.packageManager?.getApplicationInfo(name, 0)
                    ?.loadLabel(context?.packageManager!!)
                preference?.summary = "$label($name)"
            } else {
                preference?.summary = "$name: 已被卸载，请重新选择"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null) {
            val edit = sharedPreferences.edit()
            val packageSelected = sharedPreferences.getString("package_select", null)
            val targetPackageName = sharedPreferences.getString("package_name", null)
            val certificate = sharedPreferences.getBoolean("certificate", false)
            val protocol = sharedPreferences.getBoolean("protocol", false)
            val okhttp = sharedPreferences.getBoolean("okhttp", false)
            val webView = sharedPreferences.getBoolean("webView", false)
            val webDebug = sharedPreferences.getBoolean("web_debug", false)
            val jsAlert = sharedPreferences.getBoolean("js_alert", false)
            val json = sharedPreferences.getBoolean("json", false)
            val crypto = sharedPreferences.getBoolean("crypto", false)
            val jsonObject = JSONObject()
            // 目标包名 EditTextPreference 配置优先级最高
            if (targetPackageName?.isNotBlank() == true) {
                jsonObject.put("targetPackageName", targetPackageName.trim())
            } else if (packageSelected?.isNotBlank() == true
                && CommonUtils.isPkgInstalled(context, packageSelected)
            ) {
                jsonObject.put("targetPackageName", packageSelected.trim())
            }
            jsonObject.put("certificate", certificate)
            jsonObject.put("protocol", protocol)
            jsonObject.put("okhttp", okhttp)
            jsonObject.put("webView", webView)
            jsonObject.put("webDebug", webDebug)
            jsonObject.put("jsAlert", jsAlert)
            jsonObject.put("json", json)
            jsonObject.put("crypto", crypto)
            edit.putString("target", jsonObject.toString())
            edit.apply()
        }

    }
}