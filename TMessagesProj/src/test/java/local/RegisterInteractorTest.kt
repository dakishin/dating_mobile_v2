package local

import com.dating.api.DatingApi
import com.dating.api.PifResponse
import com.dating.interactors.GeoDataSender
import com.dating.interactors.RegisterInteractor
import com.dating.model.TelegramUser
import com.dating.interactors.ProfilePreferences
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*

/**
 *   Created by dakishin@gmail.com
 */
class RegisterInteractorTest : BaseRobotoTest() {

    @Mock
    lateinit var api: DatingApi

    @Mock
    lateinit var profilePreferences: ProfilePreferences

    @Mock
    lateinit var geoDataSender: GeoDataSender


    @Test
    fun should_return_current_registered() {
        Mockito.`when`(api.registerTelegramUser(anyInt(), anyString(), anyString()))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(geoDataSender.sendGeoData()).thenReturn(Completable.complete())


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(1, "name", "lastName")
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(profilePreferences).saveTelegramId(anyInt())
        verify(profilePreferences).saveFistName(anyString())
        verify(profilePreferences).saveLastName(anyString())
        verify(geoDataSender).sendGeoData()
        verify(api, never()).registerTelegramUser(anyInt(), anyString(), anyString())
    }



    @Test
    fun should_return_register() {
        Mockito.`when`(api.registerTelegramUser(anyInt(), anyString(), anyString()))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(geoDataSender.sendGeoData()).thenReturn(Completable.complete())


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(2, "name", "lastName")
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(profilePreferences).saveTelegramId(anyInt())
        verify(profilePreferences).saveFistName(anyString())
        verify(profilePreferences).saveLastName(anyString())
        verify(geoDataSender).sendGeoData()


        verify(api).registerTelegramUser(anyInt(), anyString(), anyString())
    }

}