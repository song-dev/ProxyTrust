package com.song.trust

import android.graphics.drawable.Drawable
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qmuiteam.qmui.widget.QMUIRadiusImageView2
import com.song.trust.applist.AppListViewModel

@BindingAdapter("onRefreshListener")
fun onRefreshListener(swipeRefreshLayout: SwipeRefreshLayout, appListViewModel: AppListViewModel) {
    appListViewModel.refreshData(swipeRefreshLayout)
}

@BindingAdapter("icon")
fun icon(imageView: QMUIRadiusImageView2, drawable: Drawable) {
    imageView.setImageDrawable(drawable)
}
