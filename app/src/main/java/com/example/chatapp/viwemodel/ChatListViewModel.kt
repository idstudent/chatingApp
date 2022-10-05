package com.example.chatapp.viwemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.App
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatListViewModel : ViewModel() {
    private val _result: MutableLiveData<DataSnapshot> = MutableLiveData()
    val result: LiveData<DataSnapshot> get() = _result

    fun getChatList() {
        val myUid = App.firebaseAuthInstance?.currentUser?.uid.toString()

        App.firebaseDatabaseInstance?.getReference("ChatRoom")?.child("chatRooms")
            ?.orderByChild("users/$myUid")
            ?.equalTo(true)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {

                    _result.value = snapshot
                }
            })
    }
}