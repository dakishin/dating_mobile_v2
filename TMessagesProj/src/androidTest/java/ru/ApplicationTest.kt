package ru

import com.dating.modules.AndroidModule
import com.dating.modules.AppComponentInstance
import com.dating.modules.DaggerAppComponent
import org.telegram.messenger.ApplicationLoader

/**
 *   Created by dakishin@gmail.com
 */
class ApplicationTest : ApplicationLoader() {

    override fun onCreate() {
        super.onCreate()

        val app = DaggerAppComponent.builder()
            .androidModule(AndroidModule(this)).build()
        AppComponentInstance.setAppComponent(app)

    }

}