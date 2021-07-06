package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return dpToPx(dp).toInt()
}

fun Context.getIntDimension(@DimenRes dimensionId: Int): Int {
    return getDimension(dimensionId).toInt()
}

fun Context.getDimension(@DimenRes dimensionId: Int): Float {
    return resources.getDimension(dimensionId)
}

fun Context.attrValue(@AttrRes resId: Int): Int {
    val tv = TypedValue()
    if (theme.resolveAttribute(resId, tv, true)) return tv.data
    else throw Resources.NotFoundException("Resource with id $resId not found")
}