package com.song.trust

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by chensongsong on 2021/11/19.
 */
class AppListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.app_list, AppListFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun finish() {
        super.finish()
    }
}