package com.song.trust

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.json.JSONObject

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
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
                val jsonObject = JSONObject()
                jsonObject.put("targetPackageName", targetPackageName?.trim())
                jsonObject.put("certificate", certificate)
                jsonObject.put("protocol", protocol)
                jsonObject.put("okhttp", okhttp)
                jsonObject.put("webView", webView)
                jsonObject.put("webDebug", webDebug)
                jsonObject.put("jsAlert", jsAlert)
                jsonObject.put("json", json)
                edit.putString("target", jsonObject.toString())
                edit.apply()
//                if (key == "package_select") {
//                    val preference: Preference? =
//                        this.findPreference("package_select")
//                    preference?.summary = "test"
//                }


            }

        }
    }
}