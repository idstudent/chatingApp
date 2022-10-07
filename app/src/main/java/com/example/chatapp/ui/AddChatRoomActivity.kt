package com.example.chatapp.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.ui.adapter.ChatUsersListAdapter
import com.example.chatapp.databinding.ActivityAddChatRoomBinding
import com.example.chatapp.util.setOnSingleClickListener
import com.example.chatapp.viwemodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddChatRoomActivity : BaseActivity<ActivityAddChatRoomBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_add_chat_room

    private val userViewModel: UserViewModel by viewModel()
    private var adapter = ChatUsersListAdapter()

    override fun initView() {
        super.initView()

        binding.run {
            rvPeoples.layoutManager = LinearLayoutManager(this@AddChatRoomActivity)
            rvPeoples.adapter = adapter
        }

        userViewModel.getUsers.observe(this) {
            adapter.submitList(it)
            userViewModel.my.value?.let { it1 -> adapter.setUserIsMe(it1) }
        }

        userViewModel.searchUsers.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun initListener() {
        super.initListener()

        binding.run {
            btnClose.setOnSingleClickListener {
                finish()
            }
            etOpponentName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    userViewModel.getSearchUsers(s.toString())
                }
            })
        }
    }

    override fun initViewModel() {
        super.initViewModel()

        userViewModel.getAllUser()
    }
}