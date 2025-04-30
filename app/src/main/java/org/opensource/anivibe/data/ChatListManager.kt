package org.opensource.anivibe.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatListManager(private val context: Context) {

    private val chatMessageManager = ChatMessageManager(context)
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "ChatListPrefs", Context.MODE_PRIVATE
    )
    private val conversationPrefs: SharedPreferences = context.getSharedPreferences(
        "ConversationPrefs", Context.MODE_PRIVATE
    )

    fun sortChatListByRecent(chatMessages: MutableList<ChatMessage>) {
        chatMessages.sortByDescending { chatMessageManager.getLastInteractionTime(it.name) }
    }

    fun updateMessagePreviews(chatMessages: List<ChatMessage>) {
        for (chatMessage in chatMessages) {
            val lastMessage = getLastMessageFromCharacter(chatMessage.name)
            if (lastMessage.isNotEmpty()) {
                chatMessage.message = if (lastMessage.length > 50) {
                    "${lastMessage.substring(0, 47)}..."
                } else {
                    lastMessage
                }
            }
        }
    }

    private fun getLastMessageFromCharacter(fanName: String): String {
        val json = conversationPrefs.getString("${fanName}_messages", null)
        if (json != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<List<ConversationMessage>>() {}.type
                val messages: List<ConversationMessage> = gson.fromJson(json, type)

                return messages.filter { !it.isFromUser }
                    .maxByOrNull { it.timestamp }?.message ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun updateChatList(chatMessages: MutableList<ChatMessage>) {
        sortChatListByRecent(chatMessages)
        updateMessagePreviews(chatMessages)
        chatMessageManager.updateChatListWithAccurateTimes(chatMessages)
    }
}