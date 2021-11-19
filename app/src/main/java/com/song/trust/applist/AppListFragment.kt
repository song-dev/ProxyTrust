package com.song.trust.applist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.song.trust.R
import com.song.trust.applist.AppListInfo.getAppListInfo
import com.song.trust.utils.ThreadPoolUtils

/**
 * Created by chensongsong on 2021/11/19.
 */
class AppListFragment : Fragment() {
    private lateinit var adapter: AppListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        adapter = AppListAdapter(requireActivity())
        recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.adapter = adapter
        setSwipeRefreshLayout()
        return root
    }

    private fun setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light,
            android.R.color.holo_red_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light
        )
        swipeRefreshLayout.setOnRefreshListener { refreshData() }
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            refreshData()
        }
    }

    private fun refreshData() {
        ThreadPoolUtils.instance?.execute {
            val list = getAppListInfo(requireContext())
            mainHandler.post {
                swipeRefreshLayout.isRefreshing = false
                adapter.updateData(list)
            }
        }
    }

    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}