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
            val job : String
            val company : String
            var email: String
            var password: String
            var name: String

            binding.run {
                job = etJob.text.toString().trim()
                company = etCompany.text.toString().trim()
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
                                .child(userId.toString()).setValue(User(job, company, name, userIdSt, email))

                            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@SignActivity, MainActivity::class.java))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        /*
                            파이어베이스 realtimebase 삭제했는데, 백업이 되있는건지
                            db에는 안보이는데, 전에 생성한 계정으로 생성이 안됩니다
                            test@naver.com, test123@naver.com 등등으로 만들어서 회원가입이 안되니
                            가입 테스트시 다른거로 가입해주세요
                        */
                        task.addOnFailureListener {
                            if(it.localizedMessage.contains("The email address is already in use by another account")) {
                                Toast.makeText(this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
                            }else {
                                Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }
                }
        }
    }
}