package module.christian.ru.dating.activity

import android.app.Activity
import android.app.Fragment
import module.christian.ru.dating.R

/**
 *   Created by dakishin@gmail.com
 */
open  class BaseActivity : Activity() {


    protected fun setFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment)
        //        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

}