package com.dating.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.Display
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.*
import java.nio.charset.Charset
import java.util.*


/**
 * Created by dakishin@gmail.com
 * 17.08.2014.
 */
object Utils {
    private val DAY_FORMAT = "dd.MM.yyyy EE"
    private val EXTRA_PUSH = "EXTRA_PUSH"
    private val PERMISSION = "com.club.pif.permission.RECEIVER"


    @JvmStatic
    fun getLanguage() = Locale.getDefault().language


    fun isNotEmpty(collection: Collection<*>?): Boolean {
        return collection != null && !collection.isEmpty()
    }

    @Throws(IOException::class)
    fun writeObjectToFile(context: Context, fileName: String, objectToWrite: Serializable) {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(objectToWrite)
        oos.close()
        val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fos.write(baos.toByteArray())
        fos.close()

    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun readObjectFromFile(context: Context, fileName: String): Serializable {

        val fos = context.openFileInput(fileName)
        val oos = ObjectInputStream(fos)
        val serializable = oos.readObject() as Serializable
        fos.close()
        oos.close()
        return serializable


    }


    private fun isEqualDates(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }


    fun showKeyboard(context: Context?, view: View?) {
        if (context == null || view == null) {
            return
        }
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyBoard(context: Context?, editText: View?) {
        if (context == null || editText == null) {
            return
        }
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }


    fun getDisplay(context: Activity): Display {
        return context.windowManager.defaultDisplay
    }


    fun extractEmail(email: String?): String? {
        if (email == null) {
            return null
        }
        val values = email.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (values.size > 2) {
            values[2]
        } else null
    }

    fun createFullAccountName(name: String, type: String): String {
        return type + "_" + name

    }


    fun encodeBase64(text: String): String {
        return Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)
    }

    fun decodeBase64(text: String?): String? {
        if (text == null) {
            return null
        }
        try {
            return String(Base64.decode(text.toByteArray(), Base64.DEFAULT), Charset.forName("utf8"))
        } catch (e: UnsupportedEncodingException) {
            return null
        }

    }


    val geoPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun isGeoPermissionGranted(activity: Activity): Boolean {
        geoPermission.forEach {
            if (ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun requestGeoPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, geoPermission, requestCode)
    }


    fun isPermissionGranted(permission: String, activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }


    fun sendBroadCast(context: Context, action: String, param: Serializable) {
        val intent = Intent(action)
        intent.putExtra(EXTRA_PUSH, param)
        context.sendOrderedBroadcast(intent, PERMISSION)
    }


    fun isNotBlank(string: String?): Boolean {
        return string != null && string.trim { it <= ' ' } != ""
    }

    fun isBlank(headerCurrentUser: String?): Boolean {
        return headerCurrentUser == null || headerCurrentUser.trim { it <= ' ' }.equals("", ignoreCase = true)
    }

    fun startUpdate(context: Activity) {
        val appPackageName = "com.pif.club"
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
        }


    }
}
