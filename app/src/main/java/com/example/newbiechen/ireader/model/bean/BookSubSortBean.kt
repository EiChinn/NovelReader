package com.example.newbiechen.ireader.model.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by newbiechen on 17-5-3.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class BookSubSortBean( val major: String, var mins: MutableList<String>): Parcelable