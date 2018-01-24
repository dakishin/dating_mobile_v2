package com.dating.activity.treba

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import com.android.vending.billing.IInAppBillingService
import com.dating.model.GoogleOrder
import com.dating.model.PRIEST_EVGEN_UUID
import com.dating.model.PRIEST_NOKOLAY_UUID
import com.dating.model.TrebaType
import com.dating.util.PifException
import com.dating.widget.SelectTrebaPriceDialog
import com.dating.widget.TabsWidget
import com.google.gson.Gson
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_create_treba_step3.*
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */


class TrebaCreateFragmentStep3 : BaseFragment() {


    private var createTrebaTask: CreateTrebaTask? = null
    private var consumePurchaseTask: ConsumePurchaseTask? = null

    lateinit var selectedTrebaType: TrebaType
    lateinit var selectedNames: List<String>


    var selectedPriestUuid: String? = null


    companion object {
        val TAG = TrebaCreateFragmentStep3::javaClass.name
        val REQUEST_CODE_BY_ITEM = 1001

        @JvmStatic
        fun create(): TrebaCreateFragmentStep3 = TrebaCreateFragmentStep3()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_treba_step3, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbarWithBackButton(R.string.treba_menu)
        tabs.initTabs(R.string.trebi_title_new, R.string.trebi_title_info, R.string.trebi_title_history)

        tabs.onTabSelected = object : TabsWidget.OnTabSelected {
            override fun onSelect(tab: TabsWidget.ActiveTab) {
                when (tab) {
                    TabsWidget.ActiveTab.RIGHT -> {
                        (activity as TrebaActivity).setFragment(TrebaInfoFragment.create())
                    }
                    TabsWidget.ActiveTab.CENTER -> {
                        (activity as TrebaActivity).setFragment(TrebaHistoryFragment.create())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.LEFT)

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.`package` = "com.android.vending"
        activity.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)

        cancel_treba_button.setOnClickListener({ cancelCreateTreba() })
        yevgen_layout.setOnClickListener({ chooseYevgen() })
        nikolay_layout.setOnClickListener({ chooseNikolay() })
        create_treba_button.setOnClickListener({ createTreba() })

    }


    fun createTreba() {
        if (selectedPriestUuid == null) {
            Toast.makeText(activity, getString(R.string.choose_church), Toast.LENGTH_SHORT).show()
            return
        }
        val debug = false
        if (debug) {
            createTrebaTask = CreateTrebaTask(activity)
            createTrebaTask?.execute()
        } else {
            showBuyDialog()
        }


    }

    private fun showBuyDialog() {
        SelectTrebaPriceDialog()
            .apply {
                prices = this@TrebaCreateFragmentStep3.activity.resources.getStringArray(selectedTrebaType.prices).toList()
                clickItemListener = Consumer<Int> { position ->
                    val sku = activity.resources.getStringArray(selectedTrebaType.skus)[position]
                    consumePurchaseTask = ConsumePurchaseTask(subscriptionService!!, activity, sku)
                    consumePurchaseTask?.execute()
                }
            }.show(fragmentManager, "SelectTrebaTypeDialog")
    }


    override fun onStop() {
//        cancelTask(createTrebaTask)
//        cancelTask(consumePurchaseTask)
        super.onStop()
    }


    private fun startBuyIntent(sku: String, type: String) {
        val testSku = "test.moleben"
        try {
            val buyIntentBundle = subscriptionService?.getBuyIntent(GOOGLE_INAPP_API_VERSION, activity.packageName,
                sku, type, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ")
            val pendingIntent: PendingIntent? = buyIntentBundle?.getParcelable("BUY_INTENT")
            pendingIntent ?: return
            activity.startIntentSenderForResult(pendingIntent.intentSender, REQUEST_CODE_BY_ITEM, Intent(), 0, 0, 0, null)
        } catch (e: Exception) {
//            StatisticUtil.logError(e.message, e)
            Log.e(TAG, e.message, e)
        }

    }


    var subscriptionService: IInAppBillingService? = null

    internal var mServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            subscriptionService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            subscriptionService = IInAppBillingService.Stub.asInterface(service)

        }
    }

    override fun onDestroy() {
        if (subscriptionService != null) {
            activity.unbindService(mServiceConn)
        }
        super.onDestroy()
    }

    private inner class ConsumePurchaseTask(val subscriptionService: IInAppBillingService,
                                            activity: Activity,
                                            val sku: String
    ) : DatingAsynkTask<Any>(activity, true) {
        @Throws(PifException::class)
        override fun doInBackgroundJob(): Any {
            val orders = ArrayList<GoogleOrder>()
            val purchases = subscriptionService.getPurchases(GOOGLE_INAPP_API_VERSION, activity!!.packageName, "inapp", null)
            purchases?.getStringArrayList("INAPP_PURCHASE_DATA_LIST")?.forEach {
                try {
                    orders.add(Gson().fromJson(it, GoogleOrder::class.java))
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                }
            }

            orders.forEach {
                if (it.productId == sku) {
                    val code = subscriptionService.consumePurchase(GOOGLE_INAPP_API_VERSION, activity.packageName, it.purchaseToken)
                }
            }

            return Any()
        }

        override fun onSuccess(v: Any) {
            startBuyIntent(sku, PRODUCT_TYPE)
        }

    }

    private inner class CreateTrebaTask(activity: Activity) : DatingAsynkTask<Any>(activity, true) {


        @Throws(PifException::class)
        override fun doInBackgroundJob(): Any {
            return getApi().createTreba(getPreferences().getProfile()!!, selectedTrebaType, selectedNames, selectedPriestUuid!!)
        }

        override fun onSuccess(v: Any) {
            tabs.selectTab(TabsWidget.ActiveTab.CENTER)
        }
    }


    fun cancelCreateTreba() {
        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
    }

    fun chooseYevgen() {
        selectedPriestUuid = PRIEST_EVGEN_UUID
        (yevgenTextView as CheckedTextView).isChecked = true
        (nikolayTextView as CheckedTextView).isChecked = false

    }


    fun chooseNikolay() {
        selectedPriestUuid = PRIEST_NOKOLAY_UUID
        (nikolayTextView as CheckedTextView).isChecked = true
        (yevgenTextView as CheckedTextView).isChecked = false
    }


    fun startCreateTreba(data: Intent) {
        try {
            val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")
            val order: GoogleOrder = Gson().fromJson(purchaseData, GoogleOrder::class.java)
            createTrebaTask = CreateTrebaTask(activity)
            createTrebaTask?.execute()
        } catch (e: Exception) {
            Toast.makeText(activity, "Во время покупки произошла ошибка. Обратитесь к администратору.", Toast.LENGTH_SHORT).show()
//                StatisticUtil.logError("ошибка обработки ответа покупки требы", e)
            Log.e(TAG, e.message, e)
        }
    }

}