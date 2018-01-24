package com.dating.activity.treba

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.dating.model.*
import com.dating.util.PifException
import com.dating.widget.TabsWidget
import kotlinx.android.synthetic.main.fragment_treba_history.*
import org.telegram.messenger.R
import java.text.SimpleDateFormat


/**
 *   Created by dakishin@gmail.com
 */
class TrebaHistoryFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_treba_history, container, false)
    }


    private var downlaodTrebaTask: DownloadTrebaTask? = null

    private var adapter: TrebaHistoryAdapter? = null


    companion object {
        @JvmStatic
        private val priestNames = mapOf(Pair(PRIEST_NOKOLAY_UUID, "отцу Николаю"), Pair(PRIEST_EVGEN_UUID, "отцу Евгению"))

        @JvmStatic
        fun create(): TrebaHistoryFragment = TrebaHistoryFragment()
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
                    TabsWidget.ActiveTab.LEFT -> {
                        (activity as TrebaActivity).setFragment(TrebaCreateFragmentStep1.create())
                    }

                }
            }
        }
        tabs.selectTab(TabsWidget.ActiveTab.CENTER)

        adapter = TrebaHistoryAdapter(activity)
        val layoutManager = LinearLayoutManager(activity)

        trebaHistoryRecyclerView.layoutManager = layoutManager
        trebaHistoryRecyclerView.adapter = adapter

        trebaHistoryRecyclerView.addItemDecoration(object : DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL) {

            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.set(0, 0, 0, activity.resources.getDimensionPixelOffset(R.dimen.treba_history_list_item_divider))
            }
        })


        downlaodTrebaTask = DownloadTrebaTask()
        downlaodTrebaTask?.execute()
    }


    private inner class DownloadTrebaTask : DatingAsynkTask<List<Treba>?>(activity, true) {


        @Throws(PifException::class)
        override fun doInBackgroundJob(): List<Treba>? {
            val profile: TelegramUser = getPreferences().getProfile() ?: return ArrayList<Treba>()
            return getApi().getUserTrebs(profile.uuid)
        }

        override fun onSuccess(trebas: List<Treba>?) {
            if (trebas == null || trebas.isEmpty()) {
                (activity as TrebaActivity).setFragment(TrebaEmptyHistoryFragment.create())
            } else {
                adapter?.add(trebas)
            }

        }
    }

    override fun onStop() {
//        cancelTask(downlaodTrebaTask)
        super.onStop()
    }

    class TrebaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val trebaHistoryItemDate: TextView
        val trebaType: TextView
        val names: TextView
        val status: TextView
        val firstItemPadding: RelativeLayout

        init {
            trebaHistoryItemDate = itemView.findViewById(R.id.trebaHistoryItemDate) as TextView;
            trebaType = itemView.findViewById(R.id.trebaHistoryItemType) as TextView
            names = itemView.findViewById(R.id.treba_names) as TextView
            status = itemView.findViewById(R.id.treba_status) as TextView
            firstItemPadding = itemView.findViewById(R.id.first_item_padding) as RelativeLayout
        }

    }

    class TrebaHistoryAdapter(val context: Context) : RecyclerView.Adapter<TrebaHolder>() {
        val trebas: MutableList<Treba> = mutableListOf<Treba>()
        private val dateFormatter = SimpleDateFormat("d MMMM yyyy")


        override fun onBindViewHolder(holder: TrebaHolder, position: Int) {
            val treba = trebas[position]
            holder.trebaType.text = context.getText(treba.type.nameRes)
//            holder.date.text = dateFormatter.format(Date(treba.createDate))
            var namesFormated = ""
            treba.names?.forEachIndexed { index, value ->
                namesFormated += value
                if (index != treba.names.lastIndex) {
                    namesFormated += ", "
                }
            }
            holder.names.text = namesFormated


            if (treba.status == TrebaStatus.WAIT) {
                val priestNameWait = priestNames[treba.priestUuid] ?: ""
                holder.status.text = context.resources.getString(R.string.treba_status_wait, priestNameWait)
                holder.status.setTextColor(context.resources.getColor(R.color.treba_status_in_active_text))
                holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.treba_priest_in_active, 0, 0, 0);
            } else {
                val priestNameTaken = priestNames[treba.priestUuid] ?: "священнику"
                holder.status.text = context.resources.getString(R.string.treba_status_taken, priestNameTaken)
                holder.status.setTextColor(context.resources.getColor(R.color.textColorPrimary))
                holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.treba_priest_active, 0, 0, 0);
            }

            if (position == 0) {
                holder.firstItemPadding.visibility = View.VISIBLE
            } else {
                holder.firstItemPadding.visibility = View.GONE
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrebaHolder {
            val view = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.fragment_treba_history_item, parent, false)
            val trebaHolder = TrebaHolder(view)
            return trebaHolder
        }


        override fun getItemCount(): Int {
            return trebas.size
        }

        fun add(trebas: List<Treba>) {
            this.trebas.addAll(trebas)
            notifyDataSetChanged()
        }


    }


}