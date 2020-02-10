package co.cashme.cashme.utils

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject

class ResourceDelegate @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}