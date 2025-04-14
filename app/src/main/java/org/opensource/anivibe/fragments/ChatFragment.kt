package org.opensource.anivibe.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.opensource.anivibe.ChatAdapter
import org.opensource.anivibe.ConversationActivity
import org.opensource.anivibe.R
import org.opensource.anivibe.data.ChatMessage

class ChatFragment : Fragment(R.layout.chat_message) {
    private lateinit var listOfChatMessage: MutableList<ChatMessage>
    private lateinit var arrayAdapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.chatListView)

        listOfChatMessage = mutableListOf(
            ChatMessage("Keiko", "Hey there! I was just reading some...", "8:50 pm", R.drawable.pfp_keiko),
            ChatMessage("Hiroshi", "Good day! Just got back from an ...", "8:30 pm", R.drawable.pfp_hiroshi),
            ChatMessage("Takuya", "Hi... Been thinking about the deeper...", "7:30 pm", R.drawable.pfp_takuya),
            ChatMessage("Sofia", "Hey! Did you watch the latest episode?...", "3:02 pm", R.drawable.pfp_sofia),
            ChatMessage("Akira", "I'm 10 billion percent sure this is...", "3:00 pm", R.drawable.pfp_akira),
            ChatMessage("Makoto", "Hey! I'm practicing my Naruto run for...", "Tue", R.drawable.pfp_makoto),
            ChatMessage("Rin", "Just finished rewatching my favorite anime...", "Mon", R.drawable.pfp_rin),
            ChatMessage("Jun", "Hey there! Want to talk about the new ...", "Mon", R.drawable.pfp_jun)
        )

        arrayAdapter = ChatAdapter(requireContext(), listOfChatMessage)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            // Start conversation activity
            openConversation(listOfChatMessage[position])
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDialog(position)
            true
        }
    }

    private fun openConversation(chatMessage: ChatMessage) {
        val intent = Intent(requireContext(), ConversationActivity::class.java).apply {
            putExtra("FAN_NAME", chatMessage.name)
            putExtra("FAN_PHOTO", chatMessage.photo)
        }
        startActivity(intent)
    }

    private fun showDetailsDialog(chatMessage: ChatMessage) {
        AlertDialog.Builder(requireContext())
            .setTitle("${chatMessage.name} Info")
            .setMessage(chatMessage.toString())
            .setPositiveButton("Okay", null)
            .show()
    }

    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete chat")
            .setMessage("Are you sure you want to delete chat with ${listOfChatMessage[position].name}?")
            .setPositiveButton("Delete") { _, _ ->
                val removedItem = listOfChatMessage.removeAt(position)
                arrayAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Chat with ${removedItem.name} has been deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}