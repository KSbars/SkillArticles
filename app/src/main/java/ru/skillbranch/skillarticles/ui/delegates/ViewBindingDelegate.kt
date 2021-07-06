package ru.skillbranch.skillarticles.ui.delegates

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingDelegate<T : ViewBinding>(
    private val activity: AppCompatActivity,
    private val initializer: (LayoutInflater) -> T
) : ReadOnlyProperty<AppCompatActivity, T>, LifecycleObserver {

    private var value: T? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        initValueIfNeeded()
        activity.setContentView(value!!.root)
        activity.lifecycle.removeObserver(this)
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        initValueIfNeeded()
        return value!!
    }

    private fun initValueIfNeeded() {
        if (value == null) {
            value = initializer(activity.layoutInflater)
        }
    }
}

inline fun <reified T : ViewBinding> AppCompatActivity.viewBinding(noinline initializer: (LayoutInflater) -> T) =
    ViewBindingDelegate(this, initializer)