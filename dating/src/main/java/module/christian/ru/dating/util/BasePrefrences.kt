package module.christian.ru.dating.util

import android.content.Context
import android.content.SharedPreferences

/**
 *   Created by dakishin@gmail.com
 */
class BasePreferences constructor(val context: Context, name: String) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)


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

    fun setInt(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setLong(key: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun setString(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setFloat(key: String, value: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(property: String): Float? {
        val value = sharedPreferences.getFloat(property, (-1).toFloat())
        if (value == (-1).toFloat()) {
            return null
        }
        return value
    }
}


