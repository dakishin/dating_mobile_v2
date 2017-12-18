package module.christian.ru.dating

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 *   Created by dakishin@gmail.com
 */
class DatingUtils {
    companion object {

        @JvmStatic
        fun syncTelegramId(context: Context, clientUserId: Int) {
            if (clientUserId == 0) {
                return
            }
            val intent = Intent("com.pif.club.APPLY_MY_TELEGRAM_ID")
            intent.putExtra("telegram_id", clientUserId)
            context.sendBroadcast(intent)
        }

        @JvmStatic
        fun startDatingSearch(activity: Activity) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("christian_dating://search"))
                activity.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pif.club")))
                } catch (e: Exception) {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pif.club")))
                }

            }

        }

        @JvmStatic
        fun startDatingTreba(activity: Activity) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("christian_dating://treba"))
                activity.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pif.club")))
                } catch (e: Exception) {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pif.club")))
                }

            }

        }

    }
}