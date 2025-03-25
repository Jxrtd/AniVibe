package org.opensource.anivibe.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.opensource.anivibe.ChatAdapter
import org.opensource.anivibe.R
import org.opensource.anivibe.data.ChatMessage

class ChatFragment : Fragment(R.layout.chat_message) {
    private lateinit var listOfChatMessage: MutableList<ChatMessage>
    private lateinit var arrayAdapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.chatListView)

        listOfChatMessage = mutableListOf(
            ChatMessage("Mitsuki Koga", "You: Listen to this!", "8:50 pm", R.drawable.pfp_mitsuki_koga),
            ChatMessage("Osawa Aya", "Good morning!", "8:50 pm", R.drawable.pfp_aya_oosawa),
            ChatMessage("Ken Kaneki", "Life is suffering...", "7:30 pm", R.drawable.pfp_ken_kaneki),
            ChatMessage("Xanderford Montefalco", "My dad's a mafia boss...", "3:02 pm", R.drawable.pfp_xanderford_montefalco),
            ChatMessage("Ishigami Senku", "I'm 10 billion percent sure!", "3:00 pm", R.drawable.pfp_senku_ishigami),
            ChatMessage("Naruto Uzumaki", "Wanna go eat at Ichiraku ramen?", "Tue", R.drawable.pfp_naruto_uzumaki),
            ChatMessage("Sasuke Uchiha", "I must avenge my clan...", "Mon", R.drawable.pfp_sasuke_uchiha),
            ChatMessage("Yu Jitae", "Let's go eat ice cream :P", "Mon", R.drawable.pfp_yu_jitae)
        )

        arrayAdapter = ChatAdapter(requireContext(), listOfChatMessage)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            showDetailsDialog(listOfChatMessage[position])
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDialog(position)
            true
        }
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
            .setTitle("Delete item")
            .setMessage("Are you sure you want to delete ${listOfChatMessage[position].name}?")
            .setPositiveButton("Delete") { _, _ ->
                val removedItem = listOfChatMessage.removeAt(position)
                arrayAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${removedItem.name} has been deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
