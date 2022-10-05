package com.example.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentMyPageBinding
import com.example.chatapp.util.setOnSingleClickListener
import com.google.firebase.auth.FirebaseAuth


class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_my_page

    override fun initListener() {
        super.initListener()

        binding.btnLogout.setOnSingleClickListener {
            activity?.let {
                val builder = AlertDialog.Builder(it)
                    .setTitle("로그아웃")
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인") { dialog, _ ->
                        try {
                            FirebaseAuth.getInstance().signOut()             //로그아웃
                            startActivity(Intent(it, LoginActivity::class.java))
                            dialog.dismiss()
                            (activity as MainActivity).finish()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            dialog.dismiss()
                        }
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.show()
            }
        }
    }
}