package org.opensource.anivibe.data

data class ConversationMessage(
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long
)