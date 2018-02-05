package com.dating.util

import android.support.v4.widget.SwipeRefreshLayout
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView


fun View.updateVisibility(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.updateVisibilityUsingInvisible(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}


fun View.isInvisible() = !isVisible()
fun View.isVisible() = visibility == View.VISIBLE

fun SwipeRefreshLayout.setLoading(isLoading: Boolean) {
    if (isRefreshing == isLoading) {
        return
    } else {
        isRefreshing = isLoading
    }
}

fun FrameLayout.setLoading(isLoading: Boolean) {
    if (isLoading) {
        bringToFront()
    }
    updateVisibility(isLoading)
}

fun EditText.afterTextChanged(afterTextChanged: (Editable?) -> Unit)
        = addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {
        afterTextChanged(p0)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
})

fun EditText.onTextChanged(onTextChanged: (CharSequence?) -> Unit)
        = addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onTextChanged(p0)
    }
})

fun TextView.underline() {
    val text = SpannableString(text)
    text.setSpan(UnderlineSpan(), 0, text.length, 0)
    this.text = text
}

fun String?.isDate() =
        this?.matches(Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")) ?: false