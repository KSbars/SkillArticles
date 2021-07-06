package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup

fun View.setMarginOptionally(
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null,
    left: Int? = null
) {
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    top?.let { lp.topMargin = it }
    right?.let { lp.rightMargin = it }
    bottom?.let { lp.bottomMargin = it }
    left?.let { lp.leftMargin = it }
    layoutParams = lp
}