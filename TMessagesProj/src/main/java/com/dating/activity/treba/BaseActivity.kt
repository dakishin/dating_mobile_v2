package com.dating.activity.treba

import android.app.Activity
import android.app.Fragment
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
abstract class BaseActivity : Activity() {


    fun setFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        // Replace whatever is in the fragment_container changeView with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment)
        //        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

}