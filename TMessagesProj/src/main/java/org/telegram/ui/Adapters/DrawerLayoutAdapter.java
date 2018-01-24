/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {

    private Context mContext;
    private ArrayList<Item> items = new ArrayList<>(11);

    public DrawerLayoutAdapter(Context context) {
        mContext = context;
        Theme.createDialogsResources(context);
        resetItems();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return holder.getItemViewType() == 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new DrawerProfileCell(mContext);
                break;
            case 1:
            default:
                view = new EmptyCell(mContext, AndroidUtilities.dp(8));
                break;
            case 2:
                view = new DividerCell(mContext);
                break;
            case 3:
                view = new DrawerActionCell(mContext);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new RecyclerListView.Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance().getUser(UserConfig.getClientUserId()));
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                break;
            case 3:
                items.get(position).bind((DrawerActionCell) holder.itemView);
                break;
        }
    }

    /*
    @return
        0 профиль
        1 отступ
        2 линия
        3 пункт меню
     */
    @Override
    public int getItemViewType(int index) {
        if (mContext.getResources().getBoolean(R.bool.is_russian_mode)) {
            switch (index) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 3;
                case 3:
                    return 3;
                case 4:
                    return 3;
                case 5:
                    return 3;
                case 6:
                    return 3;
                case 7:
                    return 2;
                case 8:
                    return 3;
            }
        } else {
            switch (index) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 3;
                case 3:
                    return 3;
                case 4:
                    return 3;
                case 5:
                    return 1;
                case 6:
                    return 1;
                case 7:
                    return 2;
                case 8:
                    return 3;
            }
        }


        return 3;

    }

    private void resetItems() {
        items.clear();
        if (!UserConfig.isClientActivated()) {
            return;
        }
        items.add(null); // profile  index 0
        items.add(null); // padding  index 1
//        index 2
        items.add(new Item(-1, mContext.getString(R.string.who_is_near), R.drawable.menu_newgroup));

//        index 3
        items.add(new Item(-2, mContext.getString(R.string.living_room), R.drawable.ic_chat_bubble_outline_white_24dp));

//        index 4
        items.add(new Item(-3, mContext.getString(R.string.ask_priest_room), R.drawable.ic_chat_bubble_outline_white_24dp));

        if (mContext.getResources().getBoolean(R.bool.is_russian_mode)) {
//        index 5
            items.add(new Item(-4, mContext.getString(R.string.bogoslov_room), R.drawable.ic_chat_bubble_outline_white_24dp));
            //        index 6
            items.add(new Item(-5, mContext.getString(R.string.treba_menu), R.drawable.chat_priest));
        } else {
            items.add(null); // profile  index 5
            items.add(null); // padding  index 6
        }


//        items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
//        items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
//        items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));


//        index 6
        items.add(null); // divider

//        items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
//        if (MessagesController.getInstance().callsEnabled) {
//            items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
//        }
//        items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite));

//        index 7
        items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
//        items.add(new Item(9, LocaleController.getString("TelegramFaq", R.string.TelegramFaq), R.drawable.menu_help));
    }

    public int getId(int position) {
        if (position < 0 || position >= items.size()) {
            return -1;
        }
        Item item = items.get(position);
        return item != null ? item.id : -1;
    }

    private class Item {
        public int icon;
        public String text;
        public int id;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(text, icon);
        }
    }
}
