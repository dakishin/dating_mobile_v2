package com.dating.ui.near.view

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.dating.model.CompoundUser
import com.dating.ui.near.*
import com.dating.util.Utils
import com.dating.util.updateVisibility
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.messenger.support.widget.DividerItemDecoration
import org.telegram.messenger.support.widget.LinearLayoutManager
import org.telegram.messenger.support.widget.RecyclerView
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.ActionBar
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.ActionBar.ThemeDescription
import org.telegram.ui.ChatActivity
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.BackupImageView
import org.telegram.ui.LaunchActivity
import java.util.*

/**
 *   Created by dakishin@gmail.com
 *
 *
 *
 */
class NearMeListFragment : BaseFragment(), NearMeView {

    @ProvidePresenter
    fun providePresenter() = NearMeContainer()

    @InjectPresenter
    lateinit var container: NearMeContainer

    @BindView(R.id.progressBar)
    lateinit var progressBar: View

    @BindView(R.id.unexpected_error)
    lateinit var unexpectedError: View

    @BindView(R.id.nearMeList)
    lateinit var nearMeList: RecyclerView



    private var mMvpDelegate: MvpDelegate<Any>? = null

    fun getMvpDelegate(): MvpDelegate<Any> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate<Any>(this)
        }
        return mMvpDelegate!!
    }

    private lateinit var presenter: NearMePresenter
    private lateinit var binder: Unbinder


    private lateinit var mUserAdapter: UserAdapter
    private val TAG = NearMeListFragment::javaClass.name

    companion object {
        @JvmStatic
        fun create(): BaseFragment {
            return NearMeListFragment()
        }
    }

    override fun createView(context: Context): View? {


        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()
        val component = appComponent.nearMeComponent(NearMeModule(parentActivity as LaunchActivity, container))
        presenter = component.presenter()





        actionBar.setBackButtonImage(R.drawable.ic_ab_back)
        actionBar.setAllowOverlayTitle(true)
        actionBar.setTitle(context.getString(R.string.who_near_menu))
        actionBar.setActionBarMenuOnItemClick(object : ActionBar.ActionBarMenuOnItemClick() {
            override fun onItemClick(id: Int) {
                if (id == -1) {
                    finishFragment()
                }
            }
        })

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        fragmentView = inflater.inflate(R.layout.dating_near_me, null)

        binder = ButterKnife.bind(this, fragmentView)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        nearMeList.layoutManager = layoutManager


        nearMeList.addItemDecoration(object : DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL) {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val offset = context.resources.getDimensionPixelOffset(R.dimen.near_list_item_divider)
                outRect.set(0, offset, 0, 0)
            }
        })


        mUserAdapter = UserAdapter()
        nearMeList.adapter = mUserAdapter
        nearMeList.setHasFixedSize(true)

        mUserAdapter.clear()
        val chatId = parentActivity.resources.getInteger(R.integer.living_room_id)
        presenter.action.onNext(Action.SHOW_USER_LIST(chatId))


//        presenter.action.onNext(Action.CONSUME("test_inapp2"))
//        presenter.action.onNext(Action.CONSUME("test_app"))

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
        var user1: CompoundUser? = null
        var user2: CompoundUser? = null
        var header: String? = null
        var isByDistance = true
    }

    internal inner class UserAdapter : RecyclerView.Adapter<ViewHolder>() {

        private val items = ArrayList<NearListItem>()
        private val distanceGroups = arrayListOf(5, 10)


        val isEmpty: Boolean?
            get() = items.isEmpty()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.dating_list_item_who_near_me, parent, false)
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

        private fun initUserView(holder: UserHolder, user: CompoundUser?) {
            if (user == null) {
                holder.itemView.visibility = View.GONE
                return
            }

            holder.itemView.visibility = View.VISIBLE

            val remoteUser = getUser(user.telegramUser.id)

            holder.nameView.text = remoteUser?.first_name
            holder.avatarDrawable.setInfo(remoteUser)
            val photo = remoteUser?.photo?.photo_small
            holder.avatarImage.setImage(photo, "50_50", holder.avatarDrawable)


            holder.itemView.setOnClickListener {
                val param = Bundle()
                param.putInt("user_id", Integer.valueOf(user.telegramUser.id)!!)
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


        fun add(users: List<CompoundUser>) {
            var index = 0

            while (index < users.size) {
                var item: NearListItem
                val user = users[index]
                val isByDistance: Boolean = (user.user != null) && (user.user.distance <= 10)

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

                val headerUser1 = getHeader(item.user1!!, isByDistance)
                if ((Utils.isBlank(headerCurrentUser) || headerUser1.equals(headerCurrentUser, ignoreCase = true)) && item.isByDistance == isByDistance) {
                    item.user2 = user
                    index++
                    continue
                }

                item = NearListItem()
                item.isByDistance = isByDistance
                items.add(item)
                item.user1 = user
                item.header = headerCurrentUser

                index++
            }
            notifyDataSetChanged()
        }

        private fun getHeader(user: CompoundUser, isByDistance: Boolean): String {
            return if (isByDistance) {
                parentActivity!!.getString(R.string.near_user_header_distance, getGroup(user.user!!.distance))
            } else {
                user.user?.city ?: parentActivity!!.getString(R.string.city_not_specified)
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


    private fun getUser(telegramId: Int): TLRPC.User? {
        return MessagesController.getInstance().getUser(telegramId)
    }


    override fun renderViewModel(viewModel: NearMeViewModel) {
        progressBar.updateVisibility(viewModel.isLoading)
        unexpectedError.updateVisibility(viewModel.hasError)
        nearMeList.updateVisibility(!viewModel.hasError)
        viewModel.users?.let {
            mUserAdapter.add(it)
        }
    }


    override fun onFragmentDestroy() {
        presenter.onDestroy()
        getMvpDelegate().onDestroyView()
        getMvpDelegate().onDestroy()
        binder.unbind()
        super.onFragmentDestroy()
    }

    @OnClick(R.id.ask_admin_button)
    fun clickAskAdmin() {
        presenter.action.onNext(Action.AskAdmin())
    }

}