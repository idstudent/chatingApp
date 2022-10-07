package com.example.chatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ListTalkItemMineBinding
import com.example.chatapp.databinding.ListTalkItemOthersBinding
import com.example.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter(
    private var chatRoomKey: String?
) : ListAdapter<Message, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
) {
    var messageKeys = ArrayList<String>()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderUid == myUid) 1 else 0
    }

    fun submit(messageKeys: List<String>) {
        this.messageKeys = (messageKeys as ArrayList<String>).clone() as ArrayList<String>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                MyMessageViewHolder(
                    ListTalkItemMineBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                OtherMessageViewHolder(
                    ListTalkItemOthersBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position).senderUid == myUid) {
            (holder as MyMessageViewHolder).onBind(getItem(position) as Message)
        } else {
            (holder as OtherMessageViewHolder).onBind(getItem(position) as Message, position)
        }
    }

    inner class OtherMessageViewHolder(
        private val binding: ListTalkItemOthersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Message, position: Int) {
            binding.message = item
            setShown(position)
        }


        private fun setShown(position: Int) {
            chatRoomKey?.let {
                App.firebaseDatabaseInstance?.getReference("ChatRoom")
                    ?.child("chatRooms")?.child(it)?.child("messages")
                    ?.child(messageKeys[position])?.child("confirmed")?.setValue(true)
                    ?.addOnSuccessListener {
                        Log.e("successljy", "success")
                    }
            }
        }
    }

    inner class MyMessageViewHolder(
        private val binding: ListTalkItemMineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Message) {
            binding.message = item
        }
    }

}
