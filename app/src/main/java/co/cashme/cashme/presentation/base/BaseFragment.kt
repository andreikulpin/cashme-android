package co.cashme.cashme.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import co.cashme.cashme.App
import co.cashme.cashme.utils.extensions.castTo
import toothpick.Toothpick

internal open class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {
    protected val scopeName = "${javaClass.simpleName}_${hashCode()}"
    protected open val parentScopeName =
        parentFragment?.castTo<BaseFragment>()?.scopeName
            ?: App.SCOPE_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.openScopes(parentScopeName, scopeName).inject(this)
        super.onCreate(savedInstanceState)
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @CallSuper
    protected open fun initView() {
        setupSystemUiVisibility()
    }

    protected open fun setupSystemUiVisibility() {
        requireView().systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving || requireActivity().isFinishing)
            Toothpick.closeScope(scopeName)
    }
}