package com.livetl.android.util

import android.util.SparseArray
import androidx.core.util.forEach

fun <T> SparseArray<T>.toList(): List<T> {
    val list = ArrayList<T>()
    forEach { _, value ->
        list.add(value)
    }
    return list
}