package com.song.trust.applist

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.song.trust.utils.ThreadPoolUtils

/**
 * Created by chensongsong on 2021/12/8.
 */
class AppListViewModel : ViewModel() {

    val applicationList: MutableLiveData<List<ApplicationBean>> = MutableLiveData()

    fun refreshData(view: View) {
        ThreadPoolUtils.instance?.execute {
            val list = AppListInfo.getAppListInfo(view.context)
            applicationList.postValue(list)
        }
    }
}