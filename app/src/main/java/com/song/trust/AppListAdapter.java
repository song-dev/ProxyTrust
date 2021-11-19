package com.song.trust;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by chensongsong on 2021/11/19.
 */
public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ApplicationBean> data = new ArrayList<>();

    public AppListAdapter(Context context) {
        this.context = context;
    }

    public void updateData(List<ApplicationBean> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(data.get(position).getTitle())) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View root = LayoutInflater.from(context).inflate(R.layout.item_adapter_applist, parent, false);
            return new HomeHolder(root);
        } else {
            View root = LayoutInflater.from(context).inflate(R.layout.item_adapter_applist_title, parent, false);
            return new TitleHolder(root);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ApplicationBean bean = data.get(position);
        if (TextUtils.isEmpty(bean.getTitle())) {
            HomeHolder homeHolder = (HomeHolder) holder;
            homeHolder.icon.setImageDrawable(bean.getIcon());
            homeHolder.nameTv.setText(bean.getName());
            homeHolder.packageNameTv.setText(bean.getPackageName());
            homeHolder.root.setOnClickListener((view) -> {
                Intent intent = new Intent();
                intent.setClass(context, SettingsActivity.class);
                intent.putExtra("app", bean);
                ((AppCompatActivity) context).setResult(RESULT_OK, intent);
                ((AppCompatActivity) context).finish();
            });
        } else {
            TitleHolder titleHolder = (TitleHolder) holder;
            titleHolder.titleTv.setText(bean.getTitle());
        }
    }

    static class HomeHolder extends RecyclerView.ViewHolder {

        View root;
        @BindView(R.id.tv_name)
        TextView nameTv;
        @BindView(R.id.tv_package_name)
        TextView packageNameTv;
        @BindView(R.id.imageView)
        ImageView icon;

        HomeHolder(View view) {
            super(view);
            root = view;
            ButterKnife.bind(this, root);
        }
    }

    static class TitleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView titleTv;

        TitleHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
