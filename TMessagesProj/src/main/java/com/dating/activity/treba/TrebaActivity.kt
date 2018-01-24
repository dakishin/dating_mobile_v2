package com.dating.activity.treba

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TrebaCreateFragmentStep3.REQUEST_CODE_BY_ITEM && data != null && resultCode == Activity.RESULT_OK) {
            val trebaCreateFragmentStep3: TrebaCreateFragmentStep3 = fragmentManager.findFragmentById(R.id.fragment_container) as TrebaCreateFragmentStep3
            trebaCreateFragmentStep3.startCreateTreba(data)
        }
    }

}