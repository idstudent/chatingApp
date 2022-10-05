package com.example.chatapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.*
import com.example.chatapp.databinding.ListPersonItemBinding
import com.example.chatapp.model.ChatRoom
import com.example.chatapp.model.User
import com.example.chatapp.ui.ChatRoomActivity
import com.example.chatapp.util.setOnSingleClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatUsersListAdapter : ListAdapter<User, ChatUsersListAdapter.ChatUserViewHolder>(
    object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var my : User?= null

    fun setUserIsMe(my : User) {
        this.my = my
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        return ChatUserViewHolder(
            ListPersonItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ChatUserViewHolder(
        private val binding: ListPersonItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var item: User? = null

        init {
            binding.root.setOnSingleClickListener {
                item?.let { user -> makeChat(user) }
            }
        }

        fun onBind(item: User) {
            this.item = item

            binding.tvEmail.text = item.email
            binding.tvName.text = item.name
        }

        private fun makeChat(user: User) {     //채팅방 추가
            val database =
                App.firebaseDatabaseInstance?.getReference("ChatRoom")?.child("chatRooms")
            val chatRoom = my?.uid?.let { myUid ->
                user.uid?.let { userUid ->
                    ChatRoom(         //추가할 채팅방 정보 세팅
                        mapOf(myUid to true, userUid to true),
                        null
                    )
                }
            }


            database?.orderByChild("users/${user.uid}")
                ?.equalTo(true)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            database.push().setValue(chatRoom)
                                .addOnSuccessListener {
                                    chatRoom?.let {
                                        goToChatRoom(it, user)
                                    }
                                }
                        } else {
                            chatRoom?.let {
                                goToChatRoom(it, user)
                            }
                        }

                    }
                })
        }

        fun goToChatRoom(chatRoom: ChatRoom, opponentUid: User) {
            val intent = Intent(binding.root.context, ChatRoomActivity::class.java)
            intent.putExtra("ChatRoom", chatRoom)
            intent.putExtra("Opponent", opponentUid)
            intent.putExtra("ChatRoomKey", "")
            binding.root.context.startActivity(intent)
        }
    }

}