package com.example.chatapp.ui

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.util.setOnSingleClickListener
import com.example.chatapp.viwemodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_login

    private val userViewModel: UserViewModel by viewModel()

    override fun initListener() {
        super.initListener()

        binding.run {
            btnLogin.setOnSingleClickListener {

                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (email.isBlank() && password.isBlank()) {
                    Toast.makeText(this@LoginActivity, "아이디 또는 패스워드를 입력해주세요", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    userViewModel.userLogin(email, password)
                }
            }

            btnSignup.setOnSingleClickListener {
                val intent = Intent(this@LoginActivity, SignActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun initViewModel() {
        super.initViewModel()

        userViewModel.result.observe(this, Observer { loginSuccess ->
            if (loginSuccess) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}