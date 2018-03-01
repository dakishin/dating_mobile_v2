package com.dating.ui.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dating.ui.base.BaseActivity
import com.dating.ui.registration.view.EnterMainDataFragment
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */

class RegistrationActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setFragment(EnterMainDataFragment.create())
    }


}