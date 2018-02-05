package com.dating.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.telegram.messenger.R;


/**
 * Created by dakishin@gmail.com
 * on 31.12.2015.
 */
public class ProgressBar extends android.widget.ProgressBar {
    public ProgressBar(Context context) {
        super(context);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.progress_bar),
                android.graphics.PorterDuff.Mode.SRC_IN);

    }

}
