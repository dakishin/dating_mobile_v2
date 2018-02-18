package com.dating.ui.treba

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.annotation.IdRes
import android.util.Log
import android.view.View
import com.dating.util.DialogUtils
import com.dating.util.PifException
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
abstract class DatingAsynkTask<T> : AsyncTask<Void, Void, T> {
    protected val activity: Activity?
    private var exception: Exception? = null
    private var progressDialog: ProgressDialog? = null
    private var showProgress = true
    private val errorView: View? = null
    private val TAG = DatingAsynkTask::class.java.name

    @IdRes
    private val errorMessageId = R.id.error_text_view


    constructor(activity: Activity, showProgress: Boolean) : super() {
        this.activity = activity
        this.showProgress = showProgress
    }


    @Throws(PifException::class)
    abstract fun doInBackgroundJob(): T

    override fun doInBackground(vararg params: Void): T? {
        try {
            exception = null
            return doInBackgroundJob()
        } catch (e: PifException) {
            Log.e(TAG, e.error.toString())
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            exception = e
        }

        return null
    }

    protected abstract fun onSuccess(result: T)

    override fun onPostExecute(result: T) {
        //super.onPostExecute(result);  пустой метод
        if (activity == null || activity.isFinishing) {
            //           если активити уже финишировали дальнейшая обработка ошибки не нужна.
            return
        }
        hideProgressViews()

        if (exception == null) {
            onSuccess(result)
        } else {
            onError(exception)
        }
    }

    private fun hideProgressViews() {
        try {
            progressDialog?.dismiss()
        } catch (e: Exception) {

        }

    }

    protected fun onError(e: Exception?) {
        Log.e(TAG, e?.message, e)
        DialogUtils.showDialog(activity, R.string.unexpected_error)
    }


    override fun onPreExecute() {
        if (activity == null || activity.isFinishing) {
            //           если активити уже финишировали дальнейшая обработка ошибки не нужна.
            return
        }

        if (showProgress) {
            progressDialog = ProgressDialog.show(activity, null, activity.getString(R.string.temp_wait), true)
        }
    }



    override fun onCancelled() {
        hideProgressViews()
    }


    override fun onCancelled(t: T) {
        hideProgressViews()
    }



}
