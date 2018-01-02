package modules

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import module.christian.ru.dating.api.Api
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
    fun getProfilePreferences(): ProfilePreferences
    fun getApi(): Api
    fun getSyncWithDating(): RegisterModule
    fun getGeoModule():GeoModule
}

@Module
class AndroidModule(val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

}

