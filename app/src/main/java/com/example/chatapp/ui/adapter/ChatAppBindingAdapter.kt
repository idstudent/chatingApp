package com.example.chatapp.ui.adapter

import android.annotation.SuppressLint
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.chatapp.model.User

@SuppressLint("SetTextI18n")
@BindingAdapter("jobAndCompany")
fun setJobAndCompanyText(view : AppCompatTextView, user : User) {
    view.text = "${user.job} @${user.company}"
}

@BindingAdapter("setDateText")
fun setDate(view : AppCompatTextView, sendDate : String?) {
    if(sendDate == null) {
        return
    }
    if (sendDate.isNotBlank()) {
        val timeString = sendDate.substring(8, 12)
        val hour = timeString.substring(0, 2)
        val minute = timeString.substring(2, 4)

        val timeformat = "%02d:%02d"

        if (hour.toInt() > 11) {
            view.text = "오후 ${timeformat.format(hour.toInt() - 12, minute.toInt())}"
        } else {
            view.text = "오전 ${timeformat.format(hour.toInt(), minute.toInt())}"
        }
    }
}
