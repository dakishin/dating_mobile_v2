package module.christian.ru.dating.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import module.christian.ru.dating.R

/**
 *   Created by dakishin@gmail.com
 */
object DialogUtils {

    fun showDialog(activity: Activity?, messageId: Int) {
        showDialog(activity, -1, messageId,
            View.OnClickListener { }, null)
    }


    @JvmOverloads
    fun showDialog(activity: Activity?,
                   titleId: Int, messageId: Int,
                   okButtonListener: View.OnClickListener? = View.OnClickListener { },
                   cancelButtonClickListener: View.OnClickListener? = null,
                   resOkButton: Int = R.string.ok,
                   resCancesButton: Int = R.string.cancel) {

        if (activity == null || activity.isFinishing) {
            return
        }

        val builder = AlertDialog.Builder(activity)

        if (okButtonListener != null) {
            builder.setPositiveButton(resOkButton) { dialog, id -> okButtonListener.onClick(null) }

        }

        if (cancelButtonClickListener != null) {
            builder.setNegativeButton(resCancesButton) { dialog, id -> cancelButtonClickListener.onClick(null) }

        }


        if (titleId != -1) {
            builder.setTitle(titleId)
        }

        builder.setMessage(messageId)
        val dialog = builder.create()
        dialog.show()
    }
}
