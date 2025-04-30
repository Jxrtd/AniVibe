package org.opensource.anivibe.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

/**
 * Manager class for chat message operations including saving/loading messages
 * and tracking last interaction times.
 */
class ChatMessageManager(private val context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "ChatMessagePrefs", Context.MODE_PRIVATE
    )

    private val conversationPrefs: SharedPreferences = context.getSharedPreferences(
        "ConversationPrefs", Context.MODE_PRIVATE
    )


    //Save the last interaction time for a specific chat
    fun saveLastInteractionTime(fanName: String) {
        val editor = sharedPrefs.edit()
        editor.putLong("${fanName}_last_time", System.currentTimeMillis())
        editor.apply()
    }

    /**
     * Get the last interaction time for a specific chat
     * @return timestamp of last interaction or 0 if never interacted
     */
    fun getLastInteractionTime(fanName: String): Long {
        // First, check if we have any chat messages in conversation data
        val json = conversationPrefs.getString("${fanName}_messages", null)
        if (json != null) {
            try {
                // Parse the conversation to find the most recent message
                val gson = Gson()
                val type = object : TypeToken<List<ConversationMessage>>() {}.type
                val messages: List<ConversationMessage> = gson.fromJson(json, type)

                if (messages.isNotEmpty()) {
                    // Return the timestamp of the most recent message
                    return messages.maxByOrNull { it.timestamp }?.timestamp ?: 0
                }
            } catch (e: Exception) {
                // If there's an error parsing, fall back to saved time
            }
        }

        // If no valid conversation data, check saved interaction time
        val savedTime = sharedPrefs.getLong("${fanName}_last_time", 0)
        if (savedTime > 0) {
            return savedTime
        }

        // If no saved time or conversation data, use the hardcoded initial timestamp
        return getHardcodedTimestampForFan(fanName)
    }

    /**
     * Returns a hardcoded timestamp for each fan's first message
     * This matches the logic in ConversationActivity
     * Ordered from most recent to oldest
     */
    private fun getHardcodedTimestampForFan(fanName: String): Long {
        val calendar = Calendar.getInstance()

        // Set specific times for each character
        when (fanName) {
            "Keiko" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -1)
                calendar.add(Calendar.MINUTE, -5)
            }
            "Hiroshi" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -2)
                calendar.add(Calendar.MINUTE, 3)
            }
            "Takuya" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -3)
                calendar.add(Calendar.MINUTE, -10)
            }
            "Sofia" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -4)
                calendar.add(Calendar.MINUTE, 7)
            }
            "Akira" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -5)
                calendar.add(Calendar.MINUTE, -2)
            }
            "Makoto" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -6)
                calendar.add(Calendar.MINUTE, 12)
            }
            "Rin" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -7)
                calendar.add(Calendar.MINUTE, -8)
            }
            "Jun" -> {
                calendar.add(Calendar.HOUR_OF_DAY, -8)
                calendar.add(Calendar.MINUTE, 15)
            }
            else -> {
                // Default: Current time
            }
        }


        // Clear seconds and milliseconds for cleaner timestamps
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }


    // Update all chat messages in the ChatFragment to show accurate times
    fun updateChatListWithAccurateTimes(chatMessages: List<ChatMessage>) {
        for (chatMessage in chatMessages) {
            val lastTime = getLastInteractionTime(chatMessage.name)
            chatMessage.time = formatTimeForChatList(lastTime)
        }
    }

    /**
     * Format a timestamp into a human-readable relative time that matches
     * the format used in the conversation list
     */
    private fun formatTimeForChatList(timestamp: Long): String {
        val now = System.currentTimeMillis()

        val diffInMillis = now - timestamp
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        return when {
            diffInDays == 0 -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val hour = calendar.get(Calendar.HOUR)
                val hourDisplay = if (hour == 0) 12 else hour
                val minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
                val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
                "$hourDisplay:$minute $amPm"
            }
            diffInDays == 1 -> {
                "Yesterday"
            }
            diffInDays < 7 -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val days = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                days[dayOfWeek]
            }
            else -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val month = calendar.get(Calendar.MONTH) + 1
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                "$month/$day"
            }
        }
    }

    /**
     * Delete all chat data for a specific fan
     */
    fun deleteChatData(fanName: String) {
        val editor = sharedPrefs.edit()
        editor.remove("${fanName}_last_time")
        conversationPrefs.edit().remove("${fanName}_messages").apply()
        editor.apply()
    }
}