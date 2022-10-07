package com.example.chatapp.ui

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.MessageAdapter
import com.example.chatapp.model.User
import com.example.chatapp.databinding.ActivityChatRoomBinding
import com.example.chatapp.model.Message
import com.example.chatapp.util.setOnSingleClickListener
import com.example.chatapp.viwemodel.ChatMessageViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatRoomActivity : BaseActivity<ActivityChatRoomBinding>() {
    private var chatRoomKey: String? = null

    private val chatMessageViewModel: ChatMessageViewModel by viewModel()
    private var size = 0

    override val layoutId: Int
        get() = R.layout.activity_chat_room

    override fun initView() {
        super.initView()

        chatRoomKey = intent.getStringExtra("chatRoomKey") ?: throw RuntimeException("not key")
        val opponentUser = (intent.getSerializableExtra("opponent")) as User

        val adapter = MessageAdapter(chatRoomKey)

        binding.run {
            user = opponentUser
            rvMessage.layoutManager = LinearLayoutManager(this@ChatRoomActivity)
            rvMessage.adapter = adapter
        }

        chatMessageViewModel.message.observe(this) {
            size = it.size
            adapter.submitList(it)

        }
        chatMessageViewModel.keys.observe(this) {
            it?.let { keys -> adapter.submit(keys) }
        }

        if (chatRoomKey.isNullOrBlank()) {
            App.firebaseDatabaseInstance?.getReference("ChatRoom")
                ?.child("chatRooms")?.orderByChild("users/${opponentUser?.uid}")
                ?.equalTo(true)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            chatRoomKey = data.key
                            chatRoomKey?.let { chatMessageViewModel.getMessages(it) }
                            break
                        }
                    }
                })
        } else {
            chatRoomKey?.let {
                chatMessageViewModel.getMessages(it)
            }
        }
    }

    override fun initListener() {
        super.initListener()

        val myUid = App.firebaseAuthInstance?.currentUser?.uid

        binding.run {
            btnClose.setOnSingleClickListener { finish() }
            btnSend.setOnSingleClickListener {
                try {
                    myUid?.let { myUid ->
                        val message = Message(myUid, getDateTimeString(), etMessage.text.toString())

                        chatRoomKey?.let { chatRoomKey ->
                            App.firebaseDatabaseInstance?.getReference("ChatRoom")
                                ?.child("chatRooms")
                                ?.child(chatRoomKey)?.child("messages")
                                ?.push()?.setValue(message)?.addOnSuccessListener {
                                    etMessage.text?.clear()
                                    rvMessage.scrollToPosition(size - 1)
                                }?.addOnCanceledListener {
                                    Toast.makeText(
                                        this@ChatRoomActivity,
                                        "메시지 전송이 실패했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun getDateTimeString(): String {
        try {
            val localDateTime = LocalDateTime.now()
            localDateTime.atZone(TimeZone.getDefault().toZoneId())
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            return localDateTime.format(dateTimeFormatter).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("getTimeError")
        }
    }
}
