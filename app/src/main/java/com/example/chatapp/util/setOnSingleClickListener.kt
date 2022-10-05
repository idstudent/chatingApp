package com.example.chatapp.util

import android.os.SystemClock
import android.view.View

fun View.setOnSingleClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SingleClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SingleClickListener(
    private var defaultInterval: Int = 500,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }

        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}