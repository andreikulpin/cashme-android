package co.cashme.cashme.presentation.feature.map.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.cashme.cashme.App
import toothpick.Toothpick
import javax.inject.Inject

class ViewModelProviderFactory @Inject constructor() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return Toothpick.openScope(App.SCOPE_NAME).getInstance(modelClass)
    }
}