package com.example.chatapp.ui

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.*
import com.example.chatapp.databinding.FragmentMessageBinding
import com.example.chatapp.model.ChatRoom
import com.example.chatapp.ui.adapter.ChatListAdapter
import com.example.chatapp.util.setOnSingleClickListener
import com.example.chatapp.viwemodel.ChatListViewModel
import com.google.firebase.database.ktx.getValue
import org.koin.androidx.viewmodel.ext.android.viewModel


class MessageFragment : BaseFragment<FragmentMessageBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_message

    private val chatListViewModel: ChatListViewModel by viewModel()
    private var chatRooms = ArrayList<ChatRoom>()
    private var chatRoomKeys = ArrayList<String>()

    override fun initView() {
        super.initView()

        val chatListAdapter = ChatListAdapter()

        binding.run {
            rvChat.layoutManager = LinearLayoutManager(activity)
            rvChat.adapter = chatListAdapter
        }

        chatListViewModel.result.observe(this) {
            chatRooms.clear()
            chatRoomKeys.clear()

            it.children.map { data ->
                data.getValue<ChatRoom>()?.let { value -> chatRooms.add(value) }
                data?.key?.let { key -> chatRoomKeys.add(key) }
            }
            chatListAdapter.submitList(chatRooms)
            chatListAdapter.submit(chatRoomKeys)

        }
    }

    override fun initListener() {
        super.initListener()

        binding.btnNewMessage.setOnSingleClickListener {
            val intent = Intent(activity, AddChatRoomActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initViewModel() {
        super.initViewModel()

        chatListViewModel.getChatList()
    }
}