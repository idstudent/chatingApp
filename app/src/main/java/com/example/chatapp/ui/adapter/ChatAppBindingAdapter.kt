package com.example.chatapp.ui.adapter

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.chatapp.model.User

@BindingAdapter("jobAndCompany")
fun setJobAndCompany(view : AppCompatTextView, user : User) {
    if(user == null){
        view.isVisible = false
        return
    }
    view.text = "${user.job} @${user.company}"
}
