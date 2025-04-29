package org.opensource.anivibe.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manager class for handling chat list ordering and message preview
 */
class ChatListManager(private val context: Context) {

    private val chatMessageManager = ChatMessageManager(context)
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "ChatListPrefs", Context.MODE_PRIVATE
    )
    private val conversationPrefs: SharedPreferences = context.getSharedPreferences(
        "ConversationPrefs", Context.MODE_PRIVATE
    )

    /**
     * Sorts chat list items based on last interaction time
     */
    fun sortChatListByRecent(chatMessages: MutableList<ChatMessage>) {
        // Sort the list by most recent interaction time (newest first)
        chatMessages.sortByDescending { chatMessageManager.getLastInteractionTime(it.name) }
    }

    /**
     * Updates the message preview text for all chat items
     */
    fun updateMessagePreviews(chatMessages: List<ChatMessage>) {
        for (chatMessage in chatMessages) {
            val lastMessage = getLastMessageFromCharacter(chatMessage.name)
            if (lastMessage.isNotEmpty()) {
                // Limit preview to 50 characters and add ellipsis if needed
                chatMessage.message = if (lastMessage.length > 50) {
                    "${lastMessage.substring(0, 47)}..."
                } else {
                    lastMessage
                }
            }
        }
    }

    /**
     * Retrieves the last message sent by a character
     */
    private fun getLastMessageFromCharacter(fanName: String): String {
        val json = conversationPrefs.getString("${fanName}_messages", null)
        if (json != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<List<ConversationMessage>>() {}.type
                val messages: List<ConversationMessage> = gson.fromJson(json, type)

                // Filter for character messages (not from user) and find the most recent one
                return messages.filter { !it.isFromUser }
                    .maxByOrNull { it.timestamp }?.message ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    /**
     * Updates both the order and message preview for a chat list
     */
    fun updateChatList(chatMessages: MutableList<ChatMessage>) {
        sortChatListByRecent(chatMessages)
        updateMessagePreviews(chatMessages)
        chatMessageManager.updateChatListWithAccurateTimes(chatMessages)
    }
}