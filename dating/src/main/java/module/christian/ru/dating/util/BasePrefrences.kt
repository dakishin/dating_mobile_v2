package module.christian.ru.dating.util

import android.content.SharedPreferences
import javax.inject.Inject

/**
 *   Created by dakishin@gmail.com
 */
class BasePreferences @Inject constructor() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun getBoolean(property: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(property, defaultValue)
    }

    fun getInt(property: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(property, defaultValue)
    }

    fun getString(property: String, defaultValue: String?): String? {
        return sharedPreferences.getString(property, defaultValue)
    }


    fun setBoolean(property: String, isEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(property, isEnabled)
        editor.apply()
    }

    fun setInt(property: String, isEnabled: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(property, isEnabled)
        editor.apply()
    }

    fun setString(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}


