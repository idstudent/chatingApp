package com.example.chatapp

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    companion object {
        var firebaseAuthInstance: FirebaseAuth ?= null
        var firebaseDatabaseInstance : FirebaseDatabase ?= null
    }
    override fun onCreate() {
        super.onCreate()

        firebaseAuthInstance = FirebaseAuth.getInstance()
        firebaseDatabaseInstance = FirebaseDatabase.getInstance()

        startKoin {
            androidContext(this@App)
            modules(AppModules.modules)
        }
    }
}