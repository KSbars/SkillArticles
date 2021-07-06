package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewAnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.ui.custom.behaviors.BottombarBehavior
import kotlin.math.hypot

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    val binding: LayoutBottombarBinding
    var isSearchMode = false

    init {
        binding = LayoutBottombarBinding.inflate(LayoutInflater.from(context), this)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    fun setSearchState(isSearch: Boolean) {
        if (isSearch == isSearchMode || !isAttachedToWindow) return
        isSearchMode = isSearch
        if (isSearchMode) animateShowSearch()
        else animateHideSearch()
    }

    fun setSearchInfo(searchCount: Int = 0, position: Int = 0) {
        with(binding) {
            btnResultUp.isEnabled = searchCount > 0
            btnResultDown.isEnabled = searchCount > 0

            tvSearchResult.text = if (searchCount == 0) "Not found" else "${position.inc()} of $searchCount"

            when(position) {
                0 -> btnResultUp.isEnabled = false
                searchCount.dec() -> btnResultDown.isEnabled = false
            }
        }
    }

    private fun animateShowSearch() {
        binding.reveal.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())

        val animation = ViewAnimationUtils.createCircularReveal(
            binding.reveal,
            width,
            height / 2,
            0f,
            endRadius.toFloat()
        )
        animation.doOnEnd {
            binding.bottomGroup.isVisible = false
        }
        animation.start()
    }

    private fun animateHideSearch() {
        binding.bottomGroup.isVisible = true
        val endRadius = hypot(width.toDouble(), height / 2.toDouble())

        val animation = ViewAnimationUtils.createCircularReveal(
            binding.reveal,
            width,
            height / 2,
            endRadius.toFloat(),
            0f
        )
        animation.doOnEnd {
            binding.reveal.isVisible = false
        }
        animation.start()
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = BottombarBehavior()
}