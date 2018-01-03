package com.dating

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import module.christian.ru.dating.model.NearUser
import module.christian.ru.dating.util.Utils
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.support.widget.DividerItemDecoration
import org.telegram.messenger.support.widget.LinearLayoutManager
import org.telegram.messenger.support.widget.RecyclerView
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.ActionBar.ThemeDescription
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.BackupImageView
import java.util.*

/**
 *   Created by dakishin@gmail.com
 */
class NearMeActivity : BaseFragment() {

    private lateinit var mUserAdapter: UserAdapter
    private val TAG = NearMeActivity::javaClass.name


    override fun createView(context: Context): View? {

        actionBar.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle(context.getString(module.christian.ru.dating.R.string.who_near_menu))
        actionBar.setActionBarMenuOnItemClick(object : ActionBar.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        fragmentView = inflater.inflate(R.layout.dating_near_me, null)


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val nearMeList = fragmentView.findViewById(R.id.nearMeList) as RecyclerView
        nearMeList.layoutManager = layoutManager
        val decotator = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        decotator.setDrawable(context.resources.getDrawable(R.drawable.shape_rectangle))
        nearMeList.addItemDecoration(decotator)
        mUserAdapter = UserAdapter()
        nearMeList.adapter = mUserAdapter
        nearMeList.setHasFixedSize(true)

        mUserAdapter.clear()
        loadUsers()
        return fragmentView
    }


    override fun getThemeDescriptions(): Array<ThemeDescription>? {
        return arrayOf(
            //            new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextColorCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite),
            ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray),

            ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
            //            new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault),
            ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector))
    }

    internal inner class UserHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {

        val nameView: TextView
        val avatarDrawable: AvatarDrawable
        val avatarImage: BackupImageView


        init {
            nameView = rootView.findViewById(R.id.nearUserItemName) as TextView
            val photoHolder = rootView.findViewById(R.id.nearUserItemPhoto) as FrameLayout
            avatarDrawable = AvatarDrawable()
            avatarImage = BackupImageView(parentActivity)
            avatarImage.setRoundRadius(AndroidUtilities.dp(82f))
            avatarImage.pivotX = 0f
            avatarImage.pivotY = 0f
            photoHolder.addView(avatarImage)
        }
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var userLeftHolder: UserHolder
        var userRightHolder: UserHolder
        var distanceHeader: TextView
        var distanceHeaderHolder: View
        var headerForEmptyCity: View
        var showMoreView: View

        init {
            distanceHeader = view.findViewById(R.id.distance_header) as TextView
            distanceHeaderHolder = view.findViewById(R.id.distance_header_holder)
            headerForEmptyCity = view.findViewById(R.id.header_for_empty_city)
            showMoreView = view.findViewById(R.id.show_more)

            userLeftHolder = UserHolder(view.findViewById(R.id.user_left))
            userRightHolder = UserHolder(view.findViewById(R.id.user_right))
        }
    }


    internal inner class NearListItem {
        var user1: NearUser? = null
        var user2: NearUser? = null
        var header: String? = null
        var isByDistance = true
    }

    internal inner class UserAdapter : RecyclerView.Adapter<ViewHolder>() {

        private val items = ArrayList<NearListItem>()
        private val distanceGroups = arrayListOf(5, 10)


        val isEmpty: Boolean?
            get() = items.isEmpty()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_who_near_me, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val listItem = items[position]

            initUserView(holder.userLeftHolder, listItem.user1)
            initUserView(holder.userRightHolder, listItem.user2)
            holder.distanceHeader.text = listItem.header

            holder.headerForEmptyCity.visibility = View.GONE

            if (Utils.isBlank(listItem.header)) {
                if (position == 0) {
                    holder.headerForEmptyCity.visibility = View.VISIBLE
                }
                holder.distanceHeaderHolder.visibility = View.GONE
            } else {
                if (position == 0 || listItem.header != items[position - 1].header) {
                    holder.distanceHeaderHolder.visibility = View.VISIBLE
                } else {
                    holder.distanceHeaderHolder.visibility = View.GONE
                }
            }


//            if (position == items.size - 1) {
//                if (hasMoreByDistance || hasMoreByCity) {
//                    holder.showMoreView.visibility = View.VISIBLE
//
//                    holder.showMoreView.setOnClickListener { loadUsers() }
//                } else {
//                    holder.showMoreView.visibility = View.INVISIBLE
//                }
//            } else {
//                holder.showMoreView.visibility = View.GONE
//            }

        }

        private fun initUserView(holder: UserHolder, user: NearUser?) {
            if (user == null) {
                holder.itemView.visibility = View.GONE
                return
            }

            holder.itemView.visibility = View.VISIBLE

            val remoteUser = getUser(user.telegramId)

            holder.nameView.text = remoteUser?.first_name
            holder.avatarDrawable.setInfo(remoteUser)
            val photo = remoteUser?.photo?.photo_small
            holder.avatarImage.setImage(photo, "50_50", holder.avatarDrawable)


            holder.itemView.setOnClickListener {
                val param = Bundle()
                param.putInt("user_id", Integer.valueOf(user.telegramId)!!)
                presentFragment(ChatActivity(param))
            }


        }


        override fun getItemCount(): Int {
            return items.size
        }


        fun clear() {
            items.clear()
            notifyDataSetChanged()
        }


        fun add(users: List<NearUser>) {
            var index = 0

            while (index < users.size) {
                var item: NearListItem
                val user = users[index]
                val isByDistance: Boolean = (user.distance <= 10)

                if (items.isEmpty()) {
                    item = NearListItem()
                    item.isByDistance = isByDistance
                    items.add(item)
                } else {
                    item = items[items.size - 1]
                }

                if (item.user1 != null && item.user2 != null) {
                    item = NearListItem()
                    item.isByDistance = isByDistance
                    items.add(item)
                }

                val headerCurrentUser = getHeader(user, isByDistance)
                if (item.user1 == null) {
                    item.user1 = user
                    item.header = headerCurrentUser
                    index++
                    continue
                }

                val headerUser1 = getHeader(item.user1, isByDistance)
                if ((Utils.isBlank(headerCurrentUser) || headerUser1.equals(headerCurrentUser, ignoreCase = true)) && item.isByDistance == isByDistance) {
                    item.user2 = user
                    index++
                    continue
                }

                item = NearListItem()
                item.isByDistance = isByDistance!!
                items.add(item)
                item.user1 = user
                item.header = headerCurrentUser

                index++
            }
            notifyDataSetChanged()
        }

        private fun getHeader(user: NearUser?, isByDistance: Boolean?): String {
            return if (isByDistance!!) {
                parentActivity!!.getString(R.string.near_user_header_distance, getGroup(user!!.distance))
            } else {
                user!!.city
            }
        }

        private fun getGroup(distance: Int): Int {
            for (group in distanceGroups) {
                if (group > distance) {
                    return group
                }
            }


            return distanceGroups[distanceGroups.size - 1]
        }


    }

    private fun loadUsers() {
        val req = TLRPC.TL_channels_getParticipants()
        val chat_id = parentActivity.resources.getInteger(module.christian.ru.dating.R.integer.living_room_id)
        req.channel = MessagesController.getInputChannel(chat_id)
        req.filter = TLRPC.TL_channelParticipantsRecent()
        req.offset = 0
        req.limit = 300
        val reqId = ConnectionsManager.getInstance().sendRequest(req) { response, error ->
            if (error != null) {
                Log.e(TAG, error.text)
                return@sendRequest
            }
            val res = response as TLRPC.TL_channels_channelParticipants
            MessagesController.getInstance().putUsers(res.users, false)
            appComponent.getApi()
                .getNearMeUsers()
                .doOnError { exception -> }
                .subscribe({ users ->
                    mUserAdapter.add(users)
                })

        }
        ConnectionsManager.getInstance().bindRequestToGuid(reqId, classGuid)
    }


    private fun getUser(telegramId: Int): TLRPC.User? {
        return MessagesController.getInstance().getUser(telegramId)
    }


}
