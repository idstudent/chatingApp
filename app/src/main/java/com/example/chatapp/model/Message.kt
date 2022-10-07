package com.example.chatapp.model

import java.io.Serializable

data class Message(
    var senderUid: String = "",
    var send_date: String = "",
    var content: String = "",
    var confirmed: Boolean = false
) : Serializable