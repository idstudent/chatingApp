package com.example.chatapp.model

import java.io.Serializable

data class User(
    val job: String? = "",
    val company: String? = "",
    val name: String? = "",
    val uid: String? = "",
    val email: String = ""
) : Serializable