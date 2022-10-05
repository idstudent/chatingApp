package com.example.chatapp.ui

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.MessageAdapter
import com.example.chatapp.model.User
import com.example.chatapp.databinding.ActivityChatRoomBinding
import com.example.chatapp.model.ChatRoom
import com.example.chatapp.model.Message
import com.example.chatapp.util.setOnSingleClickListener
import com.example.chatapp.viwemodel.ChatListViewModel
import com.example.chatapp.viwemodel.ChatMessageViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatRoomActivity : BaseActivity<ActivityChatRoomBinding>() {
    lateinit var chatRoom: ChatRoom
    private var opponentUser: User ?= null
    private var chatRoomKey: String ?= null
    private var myUid: String? = null

    private var adapter :  MessageAdapter ?= null

    private val chatMessageViewModel : ChatMessageViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.activity_chat_room

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityChatRoomBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initializeProperty()
//        initializeView()
//        initializeListener()
//        setupChatRooms()
//    }

    override fun initView() {
        super.initView()

        chatRoomKey = intent.getStringExtra("ChatRoomKey") ?: throw RuntimeException("not key")
        opponentUser = (intent.getSerializableExtra("Opponent")) as User

        chatMessageViewModel.message.observe(this) {
            adapter?.submitList(it)
            chatMessageViewModel.keys.value?.let { keys -> adapter?.submit(keys) }
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
                            break
                        }
                    }
                })
        } else {
            chatRoomKey?.let {
                chatMessageViewModel.getMessages(it)
            }
        }
        binding.run {
            rvMessage.layoutManager = LinearLayoutManager(this@ChatRoomActivity)
            rvMessage.adapter = MessageAdapter(chatRoomKey)
        }


    }

    override fun initListener() {
        super.initListener()

        binding.run {
            btnClose.setOnSingleClickListener { finish() }
            btnSubmit.setOnSingleClickListener {
                try {
                    myUid?.let { myUid ->
                        val message = Message(myUid, getDateTimeString(), etMessage.text.toString())

                        chatRoomKey?.let { chatRoomKey ->
                            App.firebaseDatabaseInstance?.getReference("ChatRoom")?.child("chatRooms")
                                ?.child(chatRoomKey)?.child("messages")
                                ?.push()?.setValue(message)?.addOnSuccessListener {
                                    etMessage.text.clear()
                                }?.addOnCanceledListener {
                                    Toast.makeText(this@ChatRoomActivity, "메시지 전송이 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                        }

                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("putMessage", "메시지 전송 중 오류가 발생하였습니다.")
                }

            }
        }
    }

    private fun getDateTimeString(): String {          //메시지 보낸 시각 정보 반환
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
