package org.opensource.anivibe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.opensource.anivibe.data.ChatMessage

class ChatAdapter(
    val context: Context,
    private val listOfChatMessage: List<ChatMessage>,
    val onClickItem: (chatMessage: ChatMessage) -> Unit = {}
) : BaseAdapter() {

    override fun getCount(): Int = listOfChatMessage.size

    override fun getItem(position: Int): Any = listOfChatMessage[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.chat_list_item,
            parent,
            false
        )

        val chatMessage = listOfChatMessage[position]

        val imageViewPhoto = view.findViewById<ImageView>(R.id.imageview_photo)
        val textViewName = view.findViewById<TextView>(R.id.textview_name)
        val textViewMessage = view.findViewById<TextView>(R.id.textview_message)
        val textViewTime = view.findViewById<TextView>(R.id.textview_time)

        textViewName.text = chatMessage.name
        textViewMessage.text = chatMessage.message
        textViewTime.text = chatMessage.time
        imageViewPhoto.setImageResource(chatMessage.photo)

        view.setOnClickListener {
            onClickItem(chatMessage)
        }

        return view
    }
}