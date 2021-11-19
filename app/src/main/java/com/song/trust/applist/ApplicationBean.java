package com.song.trust.applist;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

/**
 * Created by chensongsong on 2021/11/19.
 */
public class ApplicationBean implements Parcelable, Comparable<ApplicationBean> {

    private String name;
    private String packageName;
    private Drawable icon;
    private boolean configured;
    private String title;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }

    public String getTitle() {
        return title;
    }

    public ApplicationBean setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int compareTo(ApplicationBean applicationBean) {
        if (this.isConfigured() == applicationBean.isConfigured()) {
            return 0;
        } else if (this.isConfigured()) {
            return -1;
        } else {
            return 1;
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "ApplicationBean{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", configured=" + configured +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.packageName);
        dest.writeByte(this.configured ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
    }

    public ApplicationBean() {
    }

    protected ApplicationBean(Parcel in) {
        this.name = in.readString();
        this.packageName = in.readString();
        this.configured = in.readByte() != 0;
        this.title = in.readString();
    }

    public static final Creator<ApplicationBean> CREATOR = new Creator<ApplicationBean>() {
        @Override
        public ApplicationBean createFromParcel(Parcel source) {
            return new ApplicationBean(source);
        }

        @Override
        public ApplicationBean[] newArray(int size) {
            return new ApplicationBean[size];
        }
    };
}
