package com.dating.interactors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 *   Created by dakishin@gmail.com
 */
open class PermissionInteractor(val context: Context) {

    private val geoPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    open fun isGeoPermissionGranted(): Boolean {
        geoPermission.forEach {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

   open fun requestGeoPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, geoPermission, requestCode)
    }
}