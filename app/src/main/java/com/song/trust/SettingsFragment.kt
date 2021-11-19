package com.song.trust

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.json.JSONObject

/**
 * Created by chensongsong on 2021/11/19.
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var requestDataLauncher: ActivityResultLauncher<Intent>? = null
    private var preference: Preference? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestDataLauncher =
            activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data?.getParcelableExtra<ApplicationBean>("app")
                    if (data != null) {
                        preference?.summary = data.packageName
                    }
                }
            }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preference = findPreference<Preference>("package_select")
        preference?.setOnPreferenceClickListener {
            val intent = Intent(this.context, AppListActivity::class.java)
            requestDataLauncher?.launch(intent)
            return@setOnPreferenceClickListener true
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
            jsonObject.put("targetPackageName", targetPackageName?.trim())
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