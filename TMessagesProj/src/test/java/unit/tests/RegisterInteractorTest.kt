package unit.tests

import com.dating.api.DatingApi
import com.dating.api.PifResponse
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.RegisterInteractor
import com.dating.interactors.SaveLocationInteractor
import com.dating.model.TelegramUser
import io.reactivex.Observable
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import unit.base.BaseRobotoTest

/**
 *   Created by dakishin@gmail.com
 */
class RegisterInteractorTest : BaseRobotoTest() {

    @Mock
    lateinit var api: DatingApi

    @Mock
    lateinit var profilePreferences: ProfilePreferences

    @Mock
    lateinit var geoDataSender: SaveLocationInteractor


    @Test
    fun should_return_current_registered() {
        Mockito.`when`(api.registerTelegramUser(anyInt(), anyString(), anyString()))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(geoDataSender.saveLocation()).thenReturn(Observable.just(Unit))


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(1, "name", "lastName")
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(profilePreferences).saveTelegramId(anyInt())
        verify(profilePreferences).saveFistName(anyString())
        verify(profilePreferences).saveLastName(anyString())
        verify(geoDataSender).saveLocation()
        verify(api, never()).registerTelegramUser(anyInt(), anyString(), anyString())
    }



    @Test
    fun should_return_register() {
        Mockito.`when`(api.registerTelegramUser(anyInt(), anyString(), anyString()))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))
        Mockito.`when`(profilePreferences.getTelegramId()).thenReturn(1)
        Mockito.`when`(geoDataSender.saveLocation()).thenReturn(Observable.just(Unit))


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(2, "name", "lastName")
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(profilePreferences).saveTelegramId(anyInt())
        verify(profilePreferences).saveFistName(anyString())
        verify(profilePreferences).saveLastName(anyString())
        verify(geoDataSender).saveLocation()


        verify(api).registerTelegramUser(anyInt(), anyString(), anyString())
    }

}