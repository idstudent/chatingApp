package com.example.chatapp.ui

import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initView() {
        super.initView()

        NavigationUI.setupWithNavController(binding.navBar, findNavController(R.id.nav_host))
    }
}