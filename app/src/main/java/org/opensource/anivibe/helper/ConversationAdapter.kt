package org.opensource.anivibe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.data.ConversationMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(
    private val context: Context,
    private val messages: List<ConversationMessage>,
    private val fanPhotoResId: Int,
    private val fanName: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER_MESSAGE = 1
        private const val VIEW_TYPE_CHARACTER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) {
            VIEW_TYPE_USER_MESSAGE
        } else {
            VIEW_TYPE_CHARACTER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER_MESSAGE) {
            val view = LayoutInflater.from(context).inflate(
                R.layout.item_message_sent,
                parent,
                false
            )
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(
                R.layout.item_message_received,
                parent,
                false
            )
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.itemViewType == VIEW_TYPE_USER_MESSAGE) {
            (holder as SentMessageViewHolder).bind(message)
        } else {
            (holder as ReceivedMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textViewSentMessage)
        private val timeText: TextView = itemView.findViewById(R.id.textViewSentTime)

        fun bind(message: ConversationMessage) {
            messageText.text = message.message
            timeText.text = formatTime(message.timestamp)
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        private val timeText: TextView = itemView.findViewById(R.id.textViewReceivedTime)
        private val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        private val nameText: TextView = itemView.findViewById(R.id.textViewReceivedName)

        fun bind(message: ConversationMessage) {
            messageText.text = message.message
            timeText.text = formatTime(message.timestamp)
            profileImage.setImageResource(fanPhotoResId)
            nameText.text = fanName
        }
    }

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val days = diff / (24 * 60 * 60 * 1000)

        return when {
            days == 0L -> {
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
            }
            days == 1L -> {
                "Yesterday at " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
            }
            days < 7L -> {
                SimpleDateFormat("EEE 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
            }
            else -> {
                SimpleDateFormat("MMM d 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }
}