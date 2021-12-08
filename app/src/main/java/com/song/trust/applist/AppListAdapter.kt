package com.song.trust.applist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.song.trust.BR
import com.song.trust.R
import com.song.trust.SettingsActivity
import com.song.trust.databinding.ItemAdapterAppListBinding
import com.song.trust.databinding.ItemAdapterAppListTitleBinding
import java.util.*

/**
 * Created by chensongsong on 2021/11/19.
 */
class AppListAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: MutableList<ApplicationBean> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list: List<ApplicationBean>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (TextUtils.isEmpty(data[position].title)) {
            1
        } else {
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val itemAdapterAppListBinding = DataBindingUtil.inflate<ItemAdapterAppListBinding>(
                LayoutInflater.from(context),
                R.layout.item_adapter_applist,
                parent,
                false
            )
            HomeHolder(itemAdapterAppListBinding)
        } else {
            val itemAdapterAppListTitleBinding =
                DataBindingUtil.inflate<ItemAdapterAppListTitleBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_adapter_applist_title,
                    parent,
                    false
                )
            TitleHolder(itemAdapterAppListTitleBinding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = data[position]
        if (TextUtils.isEmpty(bean.title)) {
            val homeHolder = holder as HomeHolder<*>
            homeHolder.binding.setVariable(BR.viewModel, bean)
            homeHolder.binding.executePendingBindings()
            homeHolder.binding.root.setOnClickListener {
                val intent = Intent()
                intent.setClass(context, SettingsActivity::class.java)
                intent.putExtra("app", bean)
                (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                context.finish()
            }
        } else {
            val titleHolder = holder as TitleHolder<*>
            titleHolder.binding.setVariable(BR.viewModel, bean)
            titleHolder.binding.executePendingBindings()
        }
    }

    internal class HomeHolder<T : ViewDataBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)

    internal class TitleHolder<T : ViewDataBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)
}