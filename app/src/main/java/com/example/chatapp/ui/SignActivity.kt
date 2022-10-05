package com.example.chatapp.ui

import android.content.Intent
import android.widget.Toast
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.example.chatapp.databinding.ActivitySignBinding
import com.example.chatapp.util.setOnSingleClickListener
import com.google.firebase.database.FirebaseDatabase


class SignActivity : BaseActivity<ActivitySignBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_sign

    override fun initListener() {
        super.initListener()

        binding.btnSignup.setOnSingleClickListener {
            var email: String
            var password: String
            var name: String

            binding.run {
                email = etEmail.text.toString().trim()
                password = etPassword.text.toString().trim()
                name = etName.text.toString()
            }

            val authInstance = App.firebaseAuthInstance

            authInstance?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        try {
                            val user = authInstance.currentUser
                            val userId = user?.uid
                            val userIdSt = userId.toString()
                            FirebaseDatabase.getInstance().getReference("User").child("users")
                                .child(userId.toString()).setValue(User(name, userIdSt, email))

                            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@SignActivity, MainActivity::class.java))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}