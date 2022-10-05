package com.example.chatapp.viwemodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.App
import com.example.chatapp.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatMessageViewModel : ViewModel() {
    private var messageList = ArrayList<Message>()
    private var messageKeyList = ArrayList<String>()

    private val _message: MutableLiveData<List<Message>> = MutableLiveData()
    val message: LiveData<List<Message>> get() = _message

    private val _keys: MutableLiveData<List<String>> = MutableLiveData()
    val keys: LiveData<List<String>> get() = _keys

    fun getMessages(chatRoomKey : String) {
        App.firebaseDatabaseInstance?.getReference("ChatRoom")
            ?.child("chatRooms")?.child(chatRoomKey)?.child("messages")   //전체 메시지 목록 가져오기
            ?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        data.getValue<Message>()?.let { messageList.add(it) }
                        data?.key?.let { messageKeyList.add(it) }
                    }

                    _message.value = messageList
                    _keys.value = messageKeyList
                }
            })
    }
}