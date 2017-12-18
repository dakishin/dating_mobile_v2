package module.christian.ru.dating.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import module.christian.ru.dating.R
import module.christian.ru.dating.fragment.NearMeListFragment

/**
 *   Created by dakishin@gmail.com
 */

class NearMeActivity: BaseActivity() {

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, NearMeActivity::class.java)
            context.startActivity(intent)
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_me)
        setFragment(NearMeListFragment.create())
    }



}