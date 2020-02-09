package co.cashme.cashme

import android.app.Application
import co.cashme.cashme.di.modules.AppModule
import co.cashme.cashme.di.modules.NetworkModule
import timber.log.Timber
import toothpick.Toothpick

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initAppScope()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initAppScope() {
        Toothpick.openScope(SCOPE_NAME).installModules(
            AppModule(applicationContext),
            NetworkModule()
        )
    }

    companion object {
        const val SCOPE_NAME = "APP_SCOPE"
    }
}