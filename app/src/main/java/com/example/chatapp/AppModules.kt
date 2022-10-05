package com.example.chatapp

import com.example.chatapp.viwemodel.ChatListViewModel
import com.example.chatapp.viwemodel.ChatMessageViewModel
import com.example.chatapp.viwemodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModules {
    private val viewModel = module {
        viewModel {
            UserViewModel()
        }
        viewModel {
            ChatListViewModel()
        }
        viewModel {
            ChatMessageViewModel()
        }
    }

    private val etc = module {
        single {
            FirebaseDatabase.getInstance()
        }
        single {
            FirebaseAuth.getInstance()
        }
    }
    val modules = listOf(viewModel, etc)
}