package com.song.trust.applist

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by chensongsong on 2021/11/19.
 */
open class ApplicationBean : Parcelable, Comparable<ApplicationBean> {
    var name: String? = null
    var packageName: String? = null
    var icon: Drawable? = null
    var isConfigured = false
    var title: String? = null
        private set

    fun setTitle(title: String?): ApplicationBean {
        this.title = title
        return this
    }

    override fun compareTo(other: ApplicationBean): Int {
        return when {
            isConfigured == other.isConfigured -> {
                0
            }
            isConfigured -> {
                -1
            }
            else -> {
                1
            }
        }
    }

    override fun toString(): String {
        return "ApplicationBean{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", configured=" + isConfigured +
                ", title='" + title + '\'' +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(packageName)
        dest.writeByte(if (isConfigured) 1.toByte() else 0.toByte())
        dest.writeString(title)
    }

    constructor()
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        packageName = `in`.readString()
        isConfigured = `in`.readByte().toInt() != 0
        title = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ApplicationBean?> =
            object : Parcelable.Creator<ApplicationBean?> {
                override fun createFromParcel(source: Parcel): ApplicationBean {
                    return ApplicationBean(source)
                }

                override fun newArray(size: Int): Array<ApplicationBean?> {
                    return arrayOfNulls(size)
                }
            }
    }
}