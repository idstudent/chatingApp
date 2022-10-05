package com.example.chatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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
        if (getItem(position).senderUid == myUid) {       //레이아웃 항목 초기화
            (holder as MyMessageViewHolder).onBind(getItem(position) as Message)
        } else {
            (holder as OtherMessageViewHolder).onBind(getItem(position) as Message, position)
        }
    }

    private fun getDateText(sendDate: String): String {

        var dateText = ""
        val timeString: String
        if (sendDate.isNotBlank()) {
            timeString = sendDate.substring(8, 12)
            val hour = timeString.substring(0, 2)
            val minute = timeString.substring(2, 4)

            val timeformat = "%02d:%02d"

            if (hour.toInt() > 11) {
                dateText = "오후 ${timeformat.format(hour.toInt() - 12, minute.toInt())}"
            } else {
                dateText = "오전 ${timeformat.format(hour.toInt(), minute.toInt())}"
            }
        }
        return dateText
    }

    inner class OtherMessageViewHolder(
        private val binding: ListTalkItemOthersBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Message, position: Int) {
            binding.run {
                txtMessage.text = item.content
                txtDate.text = getDateText(item.send_date)
                txtIsShown.isVisible = !item.confirmed
            }

            setShown(position)
        }



        private fun setShown(position: Int) {          //메시지 확인하여 서버로 전송
            chatRoomKey?.let {
                App.firebaseDatabaseInstance?.getReference("ChatRoom")
                    ?.child("chatRooms")?.child(it)?.child("messages")
                    ?.child(messageKeys[position])?.child("confirmed")?.setValue(true)
                    ?.addOnSuccessListener {
                        Log.e("successljy","success")
                    }
            }
        }
    }

    inner class MyMessageViewHolder(
        private val binding: ListTalkItemMineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Message) {
            binding.run {
                tvMessage.text = item.content
                tvDate.text = getDateText(item.send_date)

                tvCheck.isVisible = !item.confirmed
            }
        }
    }

}
