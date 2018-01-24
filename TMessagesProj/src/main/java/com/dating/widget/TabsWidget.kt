package com.dating.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.telegram.messenger.R

/**
 *   Created by dakishin@gmail.com
 */
class  TabsWidget(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    enum class ActiveTab {
        LEFT,
        RIGHT,
        CENTER
    }

    var leftButton: TextView

    var rightButton: TextView

    var centerButton: TextView

    interface OnTabSelected {
        fun onSelect(tab: ActiveTab)
    }

    var onTabSelected: OnTabSelected? = null
    var activeTab: ActiveTab = ActiveTab.LEFT

    init {
        FrameLayout.inflate(context, R.layout.widget_tabs, this)
        leftButton = findViewById(R.id.privateTab) as TextView
        leftButton.setOnClickListener({selectTab(ActiveTab.LEFT)})
        rightButton = findViewById(R.id.publicTab) as TextView
        rightButton.setOnClickListener({selectTab(ActiveTab.RIGHT)})

        centerButton = findViewById(R.id.centerTab) as TextView
        centerButton.setOnClickListener({selectTab(ActiveTab.CENTER)})
    }

    fun initTabs(leftTabNameRes: Int, rightTabNameRes: Int, centerTabNameRes: Int = 0) {
        leftButton.setText(leftTabNameRes)
        rightButton.setText(rightTabNameRes)
        if (centerTabNameRes != 0) {
            centerButton.setText(centerTabNameRes)
            centerButton.visibility = View.VISIBLE
        }
    }




    fun selectTab(activeTab: ActiveTab) {

        leftButton.isSelected = false
        rightButton.isSelected = false
        centerButton.isSelected = false

        when (activeTab) {
            ActiveTab.LEFT -> {
                leftButton.isSelected = true
            }
            ActiveTab.RIGHT -> {
                rightButton.isSelected = true
            }
            ActiveTab.CENTER -> {
                centerButton.isSelected = true
            }
        }
        onTabSelected?.onSelect(activeTab)
    }
}