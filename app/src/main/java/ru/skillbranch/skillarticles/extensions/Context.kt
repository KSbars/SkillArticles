package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics

    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

fun Context.attrValue(colorSecondary: Int): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(colorSecondary, tv, true)) tv.data
    else throw Resources.NotFoundException("Resource with id $colorSecondary not found")
}