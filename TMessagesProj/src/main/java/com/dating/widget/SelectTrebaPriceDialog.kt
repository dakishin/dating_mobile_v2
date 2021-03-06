package com.dating.widget

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import io.reactivex.functions.Consumer
import org.telegram.messenger.R


/**
 *   Created by dakishin@gmail.com
 */
class SelectTrebaPriceDialog : DialogFragment() {
    var prices: List<String> = ArrayList<String>()
    var clickItemListener: Consumer<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_treba_price, null);
        val listView:ListView = view.findViewById(R.id.dialog_treba_price_list) as ListView
        listView.adapter = ArrayAdapter<String>(activity, R.layout.dialog_select_treba_price_item,
            android.R.id.text1, prices)

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            clickItemListener?.accept(position)
            dismissAllowingStateLoss()
        }

        val builder = AlertDialog.Builder(activity)
        builder
            .setTitle(R.string.dialog_select_treba_price_title)
            .setView(view)

        return builder.create()
    }


}