package com.song.trust.applist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.song.trust.R
import com.song.trust.SettingsActivity
import java.util.*

/**
 * Created by chensongsong on 2021/11/19.
 */
class AppListAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<ApplicationBean> = ArrayList()
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
            val root =
                LayoutInflater.from(context).inflate(R.layout.item_adapter_applist, parent, false)
            HomeHolder(root)
        } else {
            val root =
                LayoutInflater.from(context)
                    .inflate(R.layout.item_adapter_applist_title, parent, false)
            TitleHolder(root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = data[position]
        if (TextUtils.isEmpty(bean.title)) {
            val homeHolder = holder as HomeHolder
            homeHolder.icon?.setImageDrawable(bean.icon)
            homeHolder.nameTv?.text = bean.name
            homeHolder.packageNameTv?.text = bean.packageName
            homeHolder.root.setOnClickListener {
                val intent = Intent()
                intent.setClass(context, SettingsActivity::class.java)
                intent.putExtra("app", bean)
                (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                context.finish()
            }
        } else {
            val titleHolder = holder as TitleHolder
            titleHolder.titleTv?.text = bean.title
        }
    }

    internal class HomeHolder(var root: View) : RecyclerView.ViewHolder(root) {
        @JvmField
        @BindView(R.id.tv_name)
        var nameTv: TextView? = null

        @JvmField
        @BindView(R.id.tv_package_name)
        var packageNameTv: TextView? = null

        @JvmField
        @BindView(R.id.imageView)
        var icon: ImageView? = null

        init {
            ButterKnife.bind(this, root)
        }
    }

    internal class TitleHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        @BindView(R.id.tv_title)
        var titleTv: TextView? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}