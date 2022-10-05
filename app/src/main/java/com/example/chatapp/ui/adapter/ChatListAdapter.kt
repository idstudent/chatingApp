package com.example.chatapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.App
import com.example.chatapp.ui.ChatRoomActivity
import com.example.chatapp.model.ChatRoom

import com.example.chatapp.model.User
import com.example.chatapp.databinding.ListChatroomItemBinding
import com.example.chatapp.util.setOnSingleClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ChatListAdapter : ListAdapter<ChatRoom, ChatListAdapter.ChatRoomViewHolder>(
    object : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }
) {
    var chatRoomKeys: ArrayList<String> = arrayListOf()
    val myUid = App.firebaseAuthInstance?.currentUser?.uid.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(
            ListChatroomItemBinding.inflate(
                LayoutInflater.from((parent.context)),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun submit(chatRoomKeys: ArrayList<String>) {
        this.chatRoomKeys.addAll(chatRoomKeys)
    }

    inner class ChatRoomViewHolder(
        private val binding: ListChatroomItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var item: ChatRoom? = null
        private var opponentUser = User("", "")

        init {
            binding.root.setOnSingleClickListener {
                val intent = Intent(binding.root.context, ChatRoomActivity::class.java)
                intent.putExtra("ChatRoom", item)      //채팅방 정보
                intent.putExtra("Opponent", opponentUser)          //상대방 사용자 정보
                intent.putExtra("ChatRoomKey", chatRoomKeys[adapterPosition])     //채팅방 키 정보
                binding.root.context.startActivity(intent)                            //해당 채팅방으로 이동
            }
        }

        fun onBind(item: ChatRoom) {
            this.item = item
            val userIdList = item.users?.keys
            val opponent = userIdList?.first { it != myUid }

            App.firebaseDatabaseInstance?.getReference("User")?.child("users")?.orderByChild("uid")
                ?.equalTo(opponent)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            opponentUser = data.getValue<User>() ?: User("", "")
                            binding.txtName.text = data.getValue<User>()?.name ?: ""
                        }
                    }
                })


            if (item.messages?.isNotEmpty() == true) {
                setLastMessage()
                setMessageCount()
            }
        }

        private fun setLastMessage() {
            val lastMessage =
                item?.messages?.values?.sortedWith(compareBy({ it.send_date }))
                    ?.last()
            binding.txtMessage.text = lastMessage?.content
            binding.txtMessageDate.text =
                lastMessage?.send_date?.let {
                    getLastMessageTime(it)
                }
        }


        private fun setMessageCount() {
            val unconfirmedCount =
                item?.messages?.filter {
                    !it.value.confirmed && it.value.senderUid != myUid
                }?.size ?: 0

            binding.run {
                if (unconfirmedCount > 0) {
                    txtChatCount.isVisible = true
                    txtChatCount.text = unconfirmedCount.toString()

                } else {
                    txtChatCount.isVisible = false
                }
            }
        }

        private fun getLastMessageTime(time: String): String {
            val currentTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()) //현재 시각
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

            val messageMonth: Int =
                time.substring(4, 6).toInt()
            val messageDate = time.substring(6, 8).toInt()
            val messageHour = time.substring(8, 10).toInt()
            val messageMinute = time.substring(10, 12).toInt()

            val formattedCurrentTimeString =
                currentTime.format(dateTimeFormatter)
            val currentMonth = formattedCurrentTimeString.substring(4, 6).toInt()
            val currentDate = formattedCurrentTimeString.substring(6, 8).toInt()
            val currentHour = formattedCurrentTimeString.substring(8, 10).toInt()
            val currentMinute = formattedCurrentTimeString.substring(10, 12).toInt()

            val monthAgo = currentMonth - messageMonth
            val dayAgo = currentDate - messageDate
            val hourAgo = currentHour - messageHour
            val minuteAgo = currentMinute - messageMinute

            if (monthAgo > 0)
                return monthAgo.toString() + "개월 전"
            else {
                return if (dayAgo > 0) {
                    if (dayAgo == 1)
                        "어제"
                    else
                        dayAgo.toString() + "일 전"
                } else {
                    if (hourAgo > 0)
                        hourAgo.toString() + "시간 전"
                    else {
                        if (minuteAgo > 0)
                            minuteAgo.toString() + "분 전"
                        else
                            "방금"
                    }
                }
            }
        }
    }
}