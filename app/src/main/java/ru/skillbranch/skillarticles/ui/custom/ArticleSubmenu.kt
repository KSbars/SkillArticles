package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.getDimension
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.databinding.LayoutArticleSubmenuBinding
import ru.skillbranch.skillarticles.ui.custom.behaviors.SubmenuBehavior
import kotlin.math.hypot

class ArticleSubmenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    var isOpen: Boolean = false
    private var rightBottomX: Float = context.getDimension(R.dimen.submenu_width)
    private var rightBottomY: Float = context.getDimension(R.dimen.submenu_height)

    val binding: LayoutArticleSubmenuBinding

    init {
        binding = LayoutArticleSubmenuBinding.inflate(LayoutInflater.from(context), this, true)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    fun open() {
        if (isOpen || !isAttachedToWindow) return
        isOpen = true
        animateShow()
    }

    fun close() {
        if (!isOpen || !isAttachedToWindow) return
        isOpen = false
        animateHide()
    }

    private fun animateShow() {
        val endRadius = hypot(rightBottomX, rightBottomY).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            rightBottomX.toInt(),
            rightBottomY.toInt(),
            0f,
            endRadius.toFloat()
        )
        anim.doOnStart {
            visibility = View.VISIBLE
        }
        anim.start()
    }

    private fun animateHide() {
        val endRadius = hypot(rightBottomX, rightBottomY).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            rightBottomX.toInt(),
            rightBottomY.toInt(),
            endRadius.toFloat(),
            0f
        )
        anim.doOnEnd {
            visibility = View.GONE
        }
        anim.start()
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isOpen = isOpen
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            isOpen = state.isOpen
            visibility = if (isOpen) View.VISIBLE else View.GONE
        }
    }

    private class SavedState : BaseSavedState, Parcelable {

        var isOpen: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            isOpen = src.readInt() == 1
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (isOpen) 1 else 0)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(source: Parcel): SavedState = SavedState(source)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = SubmenuBehavior()
}