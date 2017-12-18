package module.christian.ru.dating.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import module.christian.ru.dating.R

/**
 *   Created by dakishin@gmail.com
 */
class NearMeListFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun create(): NearMeListFragment = NearMeListFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_near_me_list,container,false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbarWithBackButton(R.string.who_is_near)
    }
}