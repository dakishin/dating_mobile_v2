package ru.dating.unittest.tests

import com.dating.api.DatingApi
import com.dating.api.PifResponse
import com.dating.interactors.ProfilePreferences
import com.dating.interactors.RegisterInteractor
import com.dating.interactors.SaveLocationInteractor
import com.dating.model.TelegramUser
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import ru.dating.unittest.base.BaseRobotoTest

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
    fun shouldReturnCurrentRegistered() {
       whenever(api.registerTelegramUser(1, "", ""))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))
        whenever(profilePreferences.getTelegramId()).thenReturn(1)
        whenever(geoDataSender.saveLocation()).thenReturn(Observable.just(Unit))


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(1, "", "")
            .test()
            .assertNoErrors()
            .assertComplete()


        verify(profilePreferences).saveTelegramId(1)
        verify(profilePreferences).saveFistName("")
        verify(profilePreferences).saveLastName("")
        verify(geoDataSender).saveLocation()
        verify(api, never()).registerTelegramUser(1, "", "")
    }



    @Test
    fun shouldReturnRegister() {
        whenever(api.registerTelegramUser(1, "", ""))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))


        whenever(api.registerTelegramUser(2, "", ""))
            .thenReturn(Observable.just(mock(PifResponse::class.java) as PifResponse<TelegramUser>))

        whenever(profilePreferences.getTelegramId()).thenReturn(1)
        whenever(geoDataSender.saveLocation()).thenReturn(Observable.just(Unit))


        RegisterInteractor(api, profilePreferences, geoDataSender)
            .registerTelegramUser(2, "", "")
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