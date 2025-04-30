package org.opensource.anivibe.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class ChatMessageManager(context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "ChatMessagePrefs", Context.MODE_PRIVATE
    )

    private val conversationPrefs: SharedPreferences = context.getSharedPreferences(
        "ConversationPrefs", Context.MODE_PRIVATE
    )


    fun saveLastInteractionTime(fanName: String) {
        val editor = sharedPrefs.edit()
        editor.putLong("${fanName}_last_time", System.currentTimeMillis())
        editor.apply()
    }

    fun getLastInteractionTime(fanName: String): Long {
        val json = conversationPrefs.getString("${fanName}_messages", null)
        if (json != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<List<ConversationMessage>>() {}.type
                val messages: List<ConversationMessage> = gson.fromJson(json, type)

                if (messages.isNotEmpty()) {
                    return messages.maxByOrNull { it.timestamp }?.timestamp ?: 0
                }
            } catch (e: Exception) {
            }
        }

        val savedTime = sharedPrefs.getLong("${fanName}_last_time", 0)
        if (savedTime > 0) {
            return savedTime
        }

        return getHardcodedTimestampForFan(fanName)
    }

    private fun getHardcodedTimestampForFan(fanName: String): Long {
        val calendar = Calendar.getInstance()

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
            }
        }


        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }


    fun updateChatListWithAccurateTimes(chatMessages: List<ChatMessage>) {
        for (chatMessage in chatMessages) {
            val lastTime = getLastInteractionTime(chatMessage.name)
            chatMessage.time = formatTimeForChatList(lastTime)
        }
    }

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

}