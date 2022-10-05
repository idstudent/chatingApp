package com.example.chatapp.ui

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<BINDING : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: BINDING
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = DataBindingUtil.setContentView(this, layoutId)

        initView()
        initListener()
        initViewModel()
    }


    open fun initView() {}
    open fun initListener() {}
    open fun initViewModel() {}
}
