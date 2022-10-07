package com.example.chatapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
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
import com.example.chatapp.util.Util
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
                intent.putExtra("chatRoom", item)
                intent.putExtra("opponent", opponentUser)
                intent.putExtra("chatRoomKey", chatRoomKeys[adapterPosition])
                binding.root.context.startActivity(intent)
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
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (data in snapshot.children) {
                            opponentUser = data.getValue<User>() ?: User()

                            binding.tvName.text = "${data.getValue<User>()?.name}  @${data.getValue<User>()?.job}"
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
                item?.messages?.values?.sortedWith(compareBy { it.sendDate })
                    ?.last()
            binding.tvMessage.text = lastMessage?.content
            binding.tvSendDate.text = lastMessage?.sendDate?.let {
                Util().getLastMessageTime(it)
            }
        }


        private fun setMessageCount() {
            val unconfirmedCount =
                item?.messages?.filter {
                    !it.value.confirmed && it.value.senderUid != myUid
                }?.size ?: 0

            binding.run {
                if (unconfirmedCount > 0) {
                    tvChatCount.visibility = View.VISIBLE
                    tvChatCount.text = unconfirmedCount.toString()

                } else {
                    tvChatCount.visibility = View.INVISIBLE
                }
            }
        }
    }
}