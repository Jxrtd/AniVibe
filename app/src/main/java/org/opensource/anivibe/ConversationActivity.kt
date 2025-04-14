package org.opensource.anivibe

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.adapters.ConversationAdapter
import org.opensource.anivibe.data.ConversationMessage

class ConversationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var fanNameTextView: TextView
    private lateinit var conversationAdapter: ConversationAdapter
    private val messagesList = mutableListOf<ConversationMessage>()

    private var fanName: String = ""
    private var fanPhotoResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        // Get data from intent
        fanName = intent.getStringExtra("FAN_NAME") ?: "Unknown"
        fanPhotoResId = intent.getIntExtra("FAN_PHOTO", R.drawable.chat_bubble_user)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewConversation)
        messageInput = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.buttonSend)
        fanNameTextView = findViewById(R.id.textViewCharacterName)

        // Set profile image in header
        val profileImageHeader = findViewById<ImageView>(R.id.imageViewProfileHeader)
        profileImageHeader.setImageResource(fanPhotoResId)

        // Set fan name in toolbar
        fanNameTextView.text = fanName

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true  // Messages stack from bottom
        }
        conversationAdapter = ConversationAdapter(this, messagesList, fanPhotoResId, fanName)
        recyclerView.adapter = conversationAdapter

        // Add some initial greeting message from fan
        addFanMessage(getInitialGreeting())

        // Set up send button
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Set up keyboard send action
        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                return@setOnEditorActionListener true
            }
            false
        }

        // Set up back button
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            finish()
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            // Add user message
            addUserMessage(messageText)

            // Clear input field
            messageInput.text.clear()

            // Generate character response
            generateFanResponse(messageText)
        }
    }

    private fun addUserMessage(message: String) {
        val userMessage = ConversationMessage(
            message = message,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        messagesList.add(userMessage)
        conversationAdapter.notifyItemInserted(messagesList.size - 1)
        scrollToBottom()
    }

    private fun addFanMessage(message: String) {
        val fanMessage = ConversationMessage(
            message = message,
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        messagesList.add(fanMessage)
        conversationAdapter.notifyItemInserted(messagesList.size - 1)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        recyclerView.smoothScrollToPosition(messagesList.size - 1)
    }

    private fun getInitialGreeting(): String {
        return when (fanName) {
            "Keiko" -> "Hey there! I was just reading some Tokyo Ghoul manga. What's up?"
            "Hiroshi" -> "Good day! Just got back from an anime convention! How are you doing today?"
            "Takuya" -> "Hi... Been thinking about the deeper themes in anime lately..."
            "Sofia" -> "Hey! Did you watch the latest episode? So good!"
            "Akira" -> "I'm 10 billion percent sure this is going to be an interesting conversation about anime!"
            "Makoto" -> "Hey! I'm practicing my Naruto run for the next convention! What's up?"
            "Rin" -> "Just finished rewatching my favorite anime series."
            "Jun" -> "Hey there! Want to talk about the new seasonal anime?"
            else -> "Hello! Nice to talk to a fellow anime fan."
        }
    }

    private fun generateFanResponse(userMessage: String) {
        // More realistic fan responses based on their favorite characters/shows

        val response = when {
            userMessage.contains("favorite character", ignoreCase = true) ||
                    userMessage.contains("who do you like", ignoreCase = true) ->
                when (fanName) {
                    "Keiko" -> "Ken Kaneki from Tokyo Ghoul! His character development is so deep and I relate to his struggles."
                    "Hiroshi" -> "Definitely Aya from Perfect Blue. Such a complex character in a masterpiece of psychological horror."
                    "Takuya" -> "I'm a huge fan of Ken Kaneki. His philosophy about suffering and growth really speaks to me."
                    "Sofia" -> "Xander from Mafia Academy! He's so mysterious with his family background."
                    "Akira" -> "Senku from Dr. Stone! His scientific approach to problems is exactly how I try to live my life!"
                    "Makoto" -> "Naruto Uzumaki, of course! His determination against all odds is so inspiring!"
                    "Rin" -> "Sasuke Uchiha. Everyone thinks I'm being edgy, but his character arc is actually really complex."
                    "Jun" -> "I love slice-of-life characters like Yu from Ice Cream Days. They feel so real."
                    else -> "I have too many favorites to count! What about you?"
                }

            userMessage.contains("favorite anime", ignoreCase = true) ||
                    userMessage.contains("best show", ignoreCase = true) ->
                when (fanName) {
                    "Keiko" -> "Tokyo Ghoul, hands down! The manga is even better than the anime though."
                    "Hiroshi" -> "Perfect Blue is a masterpiece, but I also love Satoshi Kon's other works."
                    "Takuya" -> "Tokyo Ghoul really resonated with me. I also enjoy other psychological series like Evangelion."
                    "Sofia" -> "Mafia Academy is so underrated! The complex relationships and plot twists are amazing."
                    "Akira" -> "Dr. Stone! It makes science so cool and interesting!"
                    "Makoto" -> "Naruto will always be my number one! I've watched the entire series three times!"
                    "Rin" -> "I really appreciate the Uchiha saga in Naruto. The themes of revenge and redemption are powerful."
                    "Jun" -> "Ice Cream Days is my comfort anime. Sometimes the simple slice-of-life shows are the best."
                    else -> "I watch all genres, but I have a special place in my heart for the classics."
                }

            userMessage.contains("cosplay", ignoreCase = true) ->
                when (fanName) {
                    "Keiko" -> "I've been working on a Kaneki cosplay! The mask is tricky to get right."
                    "Hiroshi" -> "I just finished my Aya cosplay for the next convention! Took months to perfect."
                    "Takuya" -> "I've done Kaneki before. The hair dye was a pain, but worth it."
                    "Sofia" -> "I'm planning a Xander cosplay with the full school uniform from Mafia Academy!"
                    "Akira" -> "My Senku cosplay won second place at last year's convention! The hair was a challenge!"
                    "Makoto" -> "I've cosplayed as Naruto so many times! I've perfected the headband placement!"
                    "Rin" -> "My Sasuke cosplay is almost complete. Still working on the Sharingan contacts."
                    "Jun" -> "I prefer casual cosplays like Yu's iconic ice cream shop uniform!"
                    else -> "I've been thinking about trying cosplay! Any suggestions for beginners?"
                }

            userMessage.contains("hello", ignoreCase = true) ||
                    userMessage.contains("hi", ignoreCase = true) ->
                when (fanName) {
                    "Makoto" -> "Hey there! Believe it! (Sorry, I can't help saying that, haha!)"
                    "Rin" -> "Hey. Been watching anything good lately?"
                    "Takuya" -> "Hello... *touches chin* Sorry, picked up that habit from Kaneki."
                    else -> "Hi there! Always good to chat with fellow anime fans!"
                }

            userMessage.contains("how are you", ignoreCase = true) ->
                when (fanName) {
                    "Keiko" -> "I'm doing great! Just finished a Tokyo Ghoul rewatch marathon."
                    "Takuya" -> "Managing... just finished a pretty emotional anime, still processing it."
                    "Rin" -> "Focused on my cosplay project at the moment."
                    else -> "I'm doing well! Just got some new anime merch that I'm excited about!"
                }

            userMessage.contains("food", ignoreCase = true) ||
                    userMessage.contains("eat", ignoreCase = true) ->
                when (fanName) {
                    "Makoto" -> "I actually tried making ramen like they have at Ichiraku! It didn't turn out quite right, but it was fun!"
                    "Jun" -> "I love visiting themed cafes that serve food inspired by anime! The Ice Cream Days cafe in Akihabara is amazing!"
                    "Keiko" -> "I can't eat hamburgers without thinking about Kaneki's tragic story now..."
                    else -> "There's this great ramen place nearby that reminds me of anime food!"
                }

            userMessage.contains("manga", ignoreCase = true) ->
                when (fanName) {
                    "Keiko" -> "The Tokyo Ghoul manga is SO much better than the anime! The art is incredible."
                    "Takuya" -> "I collect manga obsessively. My shelves are overflowing!"
                    "Akira" -> "Dr. Stone's manga has so many scientific details that didn't make it into the anime!"
                    else -> "I'm always looking for manga recommendations! What do you enjoy reading?"
                }

            userMessage.contains("convention", ignoreCase = true) ||
                    userMessage.contains("con", ignoreCase = true) ->
                when (fanName) {
                    "Hiroshi" -> "I go to at least 5 conventions a year! Just got back from one actually."
                    "Sofia" -> "I'm saving up for AnimeExpo next year! Can't wait to meet some of the voice actors!"
                    "Makoto" -> "Conventions are the best! I love participating in the cosplay contests!"
                    else -> "I love conventions! The energy there is amazing, being surrounded by people who share your passion."
                }

            else -> {
                // Default responses based on fan personality
                when (fanName) {
                    "Keiko" -> "Have you read the Tokyo Ghoul manga? The anime adaptation missed so many important parts!"
                    "Hiroshi" -> "I've been thinking about the visual symbolism in Perfect Blue lately. Satoshi Kon was a genius."
                    "Takuya" -> "Sometimes I think about Kaneki's quote: 'It's better to be hurt than to hurt others.' So profound."
                    "Sofia" -> "My friends think I'm too obsessed with Mafia Academy, but they just don't understand the complexity!"
                    "Akira" -> "I've been trying some science experiments inspired by Dr. Stone! Nothing dangerous, I promise!"
                    "Makoto" -> "I'm rewatching the Chunin Exam arc again! It never gets old!"
                    "Rin" -> "I think Sasuke's character is deeply misunderstood by casual viewers."
                    "Jun" -> "Have you watched any good slice-of-life anime recently? I need recommendations!"
                    else -> "So what kind of anime are you into these days?"
                }
            }
        }

        // Simulate typing delay (optional)
        recyclerView.postDelayed({
            addFanMessage(response)
        }, 1000)
    }
}