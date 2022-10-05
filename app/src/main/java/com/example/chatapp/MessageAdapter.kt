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
    //    var messages: ArrayList<Message> = arrayListOf()     //메시지 목록
    var messageKeys = ArrayList<String>()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
//    @RequiresApi(Build.VERSION_CODES.O)
//    val recyclerView = (context as ChatRoomActivity).recycler_talks   //목록이 표시될 리사이클러 뷰


    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderUid == myUid) 1 else 0
    }

    fun submit(messageKeys: List<String>) {
        this.messageKeys.addAll(messageKeys)
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

        private fun getDateText(sendDate: String): String {

            var dateText = ""
            var timeString = ""
            if (sendDate.isNotBlank()) {
                timeString = sendDate.substring(8, 12)
                var hour = timeString.substring(0, 2)
                var minute = timeString.substring(2, 4)

                var timeformat = "%02d:%02d"

                if (hour.toInt() > 11) {
                    dateText += "오후 "
                    dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
                } else {
                    dateText += "오전 "
                    dateText += timeformat.format(hour.toInt(), minute.toInt())
                }
            }
            return dateText
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
                txtMessage.text = item.content
                txtDate.text = getDateText(item.send_date)

                txtIsShown.isVisible = !item.confirmed
            }
        }

        private fun getDateText(sendDate: String): String {
            var dateText = ""
            var timeString = ""
            if (sendDate.isNotBlank()) {
                timeString = sendDate.substring(8, 12)
                var hour = timeString.substring(0, 2)
                var minute = timeString.substring(2, 4)

                var timeformat = "%02d:%02d"

                if (hour.toInt() > 11) {
                    dateText += "오후 "
                    dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
                } else {
                    dateText += "오전 "
                    dateText += timeformat.format(hour.toInt(), minute.toInt())
                }
            }
            return dateText
        }
    }

}
