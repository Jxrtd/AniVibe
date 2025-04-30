package org.opensource.anivibe

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class SettingsHelpAndSupportAcitivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_help_and_support_acitivity)

        val lvHelpAndSupport = findViewById<ListView>(R.id.lvHelpAndSupport)

        data class HelpTopic(
            val title: String,
            val content: String,
            val iconResId: Int = R.drawable.ic_help
        )

        val helpTopics = listOf(
            HelpTopic(
                "Frequently Asked Questions",
                "Here are answers to common questions:\n\n" +
                        "Q: How do I create an account?\n" +
                        "A: Go to Login screen, and click 'Sign Up'.\n\n" +
                        "Q: How do I reset my password?\n" +
                        "A: Go to settings, click Eit Profile Details, and update password.\n\n" +
                        "Q: Can I use the app offline?\n" +
                        "A: Some features are available offline, but most require an internet connection.\n\n",
                R.drawable.ic_faq
            ),
            HelpTopic(
                "Contact Support",
                "You can reach our support team at:\n\n" +
                        "Email: support@anivibe.com\n" +
                        "Phone: 1-800-ANIVIBE\n" +
                        "Hours: Monday-Friday, 9am-5pm EST\n\n" +
                        "For faster resolution, please include:\n" +
                        "• Your username\n" +
                        "• Device model\n" +
                        "• App version\n" +
                        "• Screenshots of any errors",
                R.drawable.ic_contact
            ),
            HelpTopic(
                "Terms and Conditions",
                "Our Terms and Conditions outline the rules, guidelines, and obligations that users must agree to when using our service. These terms govern your use of the application and provide important information about your rights and responsibilities.\n\n" +
                        "By using AniVibe, you agree to these terms in full. If you disagree with any part of these terms, you must not use our application.\n\n" +
                        "Last updated: April 2025",
                R.drawable.ic_terms
            ),
            HelpTopic(
                "Privacy Policy",
                "Our Privacy Policy explains how we collect, use, and protect user data. We are committed to ensuring the privacy and security of all our users.\n\n" +
                        "Information we collect includes:\n" +
                        "• Account information\n" +
                        "• Usage data\n" +
                        "• Device information\n\n" +
                        "We use this information to improve our services and provide a better user experience.",
                R.drawable.ic_privacy
            ),
            HelpTopic(
                "Send Feedback",
                "We value your input! Please share your thoughts about the app and how we can improve your experience.\n\n" +
                        "Your feedback helps us make AniVibe better for everyone. Feel free to suggest new features or report any issues you've encountered.",
                R.drawable.ic_feedback
            ),
            HelpTopic(
                "Troubleshooting Guide",
                "Common Issues:\n\n" +
                        "1. App Crashes\n" +
                        "• Ensure your app is updated to the latest version\n" +
                        "• Clear the app cache in your device settings\n" +
                        "• Restart your device\n\n" +
                        "2. Login Problems\n" +
                        "• Try resetting your password\n" +
                        "• Check your internet connection\n" +
                        "• Verify your account email\n\n" +
                        "3. Media Not Loading\n" +
                        "• Check your internet connection\n" +
                        "• Clear app cache\n" +
                        "• Try switching between Wi-Fi and mobile data",
                R.drawable.ic_troubleshoot
            ),
            HelpTopic(
                "App Updates & Changelog",
                "Version 2.3.1 (Current):\n" +
                        "• Fixed profile picture bugs\n" +
                        "• Improved performance\n" +
                        "• Added new animation features\n\n" +
                        "Version 2.2.0:\n" +
                        "• Redesigned user interface\n" +
                        "• Added dark theme\n" +
                        "• Fixed media player issues\n\n" +
                        "Version 2.1.0:\n" +
                        "• Added sharing capabilities\n" +
                        "• Improved search functionality\n" +
                        "• Enhanced user profiles",
                R.drawable.ic_updates
            )
        )

        // Create custom adapter for the help topics
        class HelpTopicAdapter(
            private val context: Activity,
            private val topics: List<HelpTopic>
        ) : ArrayAdapter<HelpTopic>(context, R.layout.help_list_item, topics) {

            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.help_list_item, parent, false)

                val topic = topics[position]

                val icon = view.findViewById<android.widget.ImageView>(R.id.item_icon)
                val text = view.findViewById<TextView>(R.id.item_text)

                icon.setImageResource(topic.iconResId)
                text.text = topic.title

                return view
            }
        }

        val adapter = HelpTopicAdapter(this, helpTopics)
        lvHelpAndSupport.adapter = adapter

        lvHelpAndSupport.setOnItemClickListener { _, _, position, _ ->
            val selectedTopic = helpTopics[position]

            val dialogBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_help_content, null)
            dialogBuilder.setView(dialogView)

            val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
            val contentTextView = dialogView.findViewById<TextView>(R.id.dialog_content)
            val closeButton = dialogView.findViewById<android.widget.Button>(R.id.dialog_close_button)

            titleTextView.text = selectedTopic.title
            contentTextView.text = selectedTopic.content

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            closeButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
            alertDialog.window?.attributes?.windowAnimations = R.anim.slide_up
        }

        val back: ImageButton = findViewById(R.id.devbackbutton)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}