package com.dating.modules

import android.content.Context
import com.dating.activity.near.NearMeComponent
import com.dating.activity.near.NearMeModule
import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import dagger.Component
import dagger.Module
import dagger.Provides
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
    fun getRegisterModule(): RegisterModule
    fun getGeoModule(): GeoModule
    fun getTelegramApi(): TelegramApi
    fun getDatingApi(): DatingApi
    fun nearMeComponent(nearMeModule: NearMeModule): NearMeComponent
}

@Module
class AndroidModule(val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

}

