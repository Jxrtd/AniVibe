package org.opensource.anivibe.data

import org.opensource.anivibe.R


data class ChatMessage(
    var name: String = "",
    var message: String = "",
    var time: String = "",
    var photo: Int = R.drawable.chat_bubble_user
)
