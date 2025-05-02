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
        val keikoGreeting = "Hey there! I was just reading some Tokyo Ghoul manga. What's up?"
        chatMessages.add(ChatMessage("Keiko", keikoGreeting, "", R.drawable.pfp_keiko))

        val hiroshiGreeting = "Good day! Just got back from an anime convention! How are you doing today?"
        chatMessages.add(ChatMessage("Hiroshi", hiroshiGreeting, "", R.drawable.pfp_hiroshi))

        val takuyaGreeting = "Hi... Been thinking about the deeper themes in anime lately..."
        chatMessages.add(ChatMessage("Takuya", takuyaGreeting, "", R.drawable.pfp_takuya))

        val sofiaGreeting = "Hey! Did you watch the latest episode? So good!"
        chatMessages.add(ChatMessage("Sofia", sofiaGreeting, "", R.drawable.pfp_sofia))

        val akiraGreeting = "I'm 10 billion percent sure this is going to be an interesting conversation about anime!"
        chatMessages.add(ChatMessage("Akira", akiraGreeting, "", R.drawable.pfp_akira))

        val makotoGreeting = "Hey! I'm practicing my Naruto run for the next convention! What's up?"
        chatMessages.add(ChatMessage("Makoto", makotoGreeting, "", R.drawable.pfp_makoto))

        val rinGreeting = "Just finished rewatching my favorite anime series."
        chatMessages.add(ChatMessage("Rin", rinGreeting, "", R.drawable.pfp_rin))

        val junGreeting = "Hey there! Want to talk about the new seasonal anime?"
        chatMessages.add(ChatMessage("Jun", junGreeting, "", R.drawable.pfp_jun))

        chatMessageManager.updateChatListWithAccurateTimes(chatMessages)
    }

    private fun openConversation(chatMessage: ChatMessage) {
        val intent = Intent(requireContext(), ConversationActivity::class.java).apply {
            putExtra("FAN_NAME", chatMessage.name)
            putExtra("FAN_PHOTO", chatMessage.photo)
        }
        startActivity(intent)
    }
}