package modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import dagger.Module
import dagger.Provides
import module.christian.ru.dating.api.Api
import module.christian.ru.dating.util.DaggerAppComponent
import javax.inject.Singleton


/**
 *   Created by dakishin@gmail.com
 */


object AppComponentInstance {
    private var appComponent: AppComponent? = null

    @JvmStatic
    fun getAppComponent(context: Context): AppComponent {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                .androidModule(AndroidModule(context)).build()
        }
        return appComponent!!
    }
}

@Singleton
@Component(modules = arrayOf(AndroidModule::class))
interface AppComponent {
    fun getPreferences(): PreferencesModule
    fun getApi(): Api
    fun getSyncWithDating(): RegisterModule
}

@Module
class AndroidModule(val context: Context) {
    private val PREFS = "settings"

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }


}

