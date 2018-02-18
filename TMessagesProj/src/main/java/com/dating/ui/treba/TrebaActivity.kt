package com.dating.ui.treba

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dating.ui.treba.view.TrebaCreateFragmentStep1
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */

class TrebaActivity : BaseActivity() {

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, TrebaActivity::class.java)
            context.startActivity(intent)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treba)
        setFragment(TrebaCreateFragmentStep1.create())
    }


}