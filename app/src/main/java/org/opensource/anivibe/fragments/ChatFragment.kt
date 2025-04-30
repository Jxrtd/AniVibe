package org.opensource.anivibe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import org.opensource.anivibe.data.ChatListManager
import org.opensource.anivibe.data.ChatMessage
import org.opensource.anivibe.data.ChatMessageManager

class ChatFragment : Fragment() {

    private lateinit var chatListView: ListView
    private lateinit var adapter: ChatAdapter
    private lateinit var chatMessageManager: ChatMessageManager
    private lateinit var chatListManager: ChatListManager
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatListView = view.findViewById(R.id.chatListView)
        chatMessageManager = ChatMessageManager(requireContext())
        chatListManager = ChatListManager(requireContext())

        initializeChatList()

        chatListManager.updateChatList(chatMessages)

        adapter = ChatAdapter(
            requireContext(),
            chatMessages
        ) { chatMessage ->
            openConversation(chatMessage)
        }
        chatListView.adapter = adapter

        chatListView.setOnItemClickListener { _, _, position, _ ->
            val selectedChatMessage = chatMessages[position]
            openConversation(selectedChatMessage)
        }
    }

    override fun onResume() {
        super.onResume()
        chatListManager.updateChatList(chatMessages)
        adapter.notifyDataSetChanged()
    }

    private fun initializeChatList() {
        chatMessages.add(ChatMessage("Keiko", "", "", R.drawable.pfp_keiko))
        chatMessages.add(ChatMessage("Hiroshi", "", "", R.drawable.pfp_hiroshi))
        chatMessages.add(ChatMessage("Takuya", "", "", R.drawable.pfp_takuya))
        chatMessages.add(ChatMessage("Sofia", "", "", R.drawable.pfp_sofia))
        chatMessages.add(ChatMessage("Akira", "", "", R.drawable.pfp_akira))
        chatMessages.add(ChatMessage("Makoto", "", "", R.drawable.pfp_makoto))
        chatMessages.add(ChatMessage("Rin", "", "", R.drawable.pfp_rin))
        chatMessages.add(ChatMessage("Jun", "", "", R.drawable.pfp_jun))
    }

    private fun openConversation(chatMessage: ChatMessage) {
        val intent = Intent(requireContext(), ConversationActivity::class.java).apply {
            putExtra("FAN_NAME", chatMessage.name)
            putExtra("FAN_PHOTO", chatMessage.photo)
        }
        startActivity(intent)
    }
}