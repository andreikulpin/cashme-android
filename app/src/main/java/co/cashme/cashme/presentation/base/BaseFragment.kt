package co.cashme.cashme.presentation.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import co.cashme.cashme.App
import co.cashme.cashme.R
import co.cashme.cashme.utils.extensions.castTo
import kotlinx.android.synthetic.main.fragment_map.*
import toothpick.Toothpick

internal open class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {
    protected open val activityOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    protected val scopeName = "${javaClass.simpleName}_${hashCode()}"
    protected open val parentScopeName =
        parentFragment?.castTo<BaseFragment>()?.scopeName
            ?: App.SCOPE_NAME

    protected var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.openScopes(parentScopeName, scopeName).inject(this)
        super.onCreate(savedInstanceState)
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        initView()
    }

    @CallSuper
    protected open fun initView() {
        requireActivity().requestedOrientation = activityOrientation
        setupSystemUiVisibility()
        setupForInsets()
    }

    protected open fun setupSystemUiVisibility() {
        requireView().systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    protected open fun setupForInsets() {
        rootView?.setOnApplyWindowInsetsListener { root, insets ->
            root.castTo<ViewGroup>()?.forEach { view ->
                view.dispatchApplyWindowInsets(insets)
            }
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        rootView?.requestApplyInsets()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving || requireActivity().isFinishing)
            Toothpick.closeScope(scopeName)
    }
}