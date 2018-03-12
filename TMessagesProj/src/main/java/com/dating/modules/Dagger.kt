package com.dating.modules

import android.content.Context
import android.location.LocationManager
import com.dating.api.DatingApi
import com.dating.api.TelegramApi
import com.dating.interactors.*
import com.dating.ui.near.NearMeComponent
import com.dating.ui.near.NearMeModule
import com.dating.ui.registration.RegistrationComponent
import com.dating.ui.registration.RegistrationModule
import com.dating.ui.treba.TrebaComponent
import com.dating.ui.treba.TrebaModule
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
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
    fun getRegisterModule(): RegisterInteractor
    fun getGeoModule(): SaveLocationInteractor
    fun getTelegramApi(): TelegramApi
    fun getDatingApi(): DatingApi


    fun nearMeComponent(nearMeModule: NearMeModule): NearMeComponent
    fun trebaComponent(trebaModule: TrebaModule): TrebaComponent
    fun registrationComponent(trebaModule: RegistrationModule): RegistrationComponent
}

@Module
class AndroidModule(val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideLocationInteractor(context: Context, permissionInteractor: PermissionInteractor): LocationInteractor =
        LocationInteractor(context, Schedulers.computation(),
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager, permissionInteractor)


    @Provides
    @Singleton
    fun permissionInteractor(context: Context): PermissionInteractor = PermissionInteractor(context)


    @Provides
    @Singleton
    fun registerModule(datingApi: DatingApi, profilePreferences: ProfilePreferences, geoDataSender: SaveLocationInteractor): RegisterInteractor =
        RegisterInteractor(datingApi, profilePreferences, geoDataSender)

    @Provides
    @Singleton
    fun profilePreferences(context: Context): ProfilePreferences = ProfilePreferences(context)

    @Provides
    @Singleton
    fun geoPreferences(context: Context): GeoPreferences = GeoPreferences(context)


    @Provides
    @Singleton
    fun datingApi(profilePreferences: ProfilePreferences): DatingApi = DatingApi(profilePreferences)

    @Provides
    @Singleton
    fun geoDataSender(geoPreferences: GeoPreferences,
                      api: DatingApi, locationInteractor: LocationInteractor,
                      profilePreferences: ProfilePreferences): SaveLocationInteractor =
        SaveLocationInteractor(geoPreferences, api, locationInteractor, profilePreferences, Schedulers.computation(), PublishSubject.create())
}

