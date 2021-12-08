package com.song.trust.applist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.song.trust.databinding.FragmentAppListBinding

/**
 * Created by chensongsong on 2021/11/19.
 */
class AppListFragment : Fragment() {
    private lateinit var dataBinding: FragmentAppListBinding
    private val appListViewModel: AppListViewModel by activityViewModels()
    private lateinit var adapter: AppListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAppListBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = appListViewModel
        val recyclerView: RecyclerView = dataBinding.recyclerView
        swipeRefreshLayout = dataBinding.swipeRefreshLayout
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
        appListViewModel.applicationList.observe(viewLifecycleOwner) {
            mainHandler.post {
                swipeRefreshLayout.isRefreshing = false
                adapter.updateData(it)
            }
        }
        setSwipeRefreshLayout()
        return dataBinding.root
    }

    private fun setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light,
            android.R.color.holo_red_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            appListViewModel.refreshData(swipeRefreshLayout)
        }
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            appListViewModel.refreshData(swipeRefreshLayout)
        }
    }

    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}