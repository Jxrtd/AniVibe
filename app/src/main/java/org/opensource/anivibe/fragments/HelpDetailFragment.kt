package org.opensource.anivibe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.opensource.anivibe.R

class HelpDetailFragment : DialogFragment() {
    private lateinit var helpTitle: TextView
    private lateinit var helpContent: TextView

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_CONTENT = "content"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_help_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        helpTitle = view.findViewById(R.id.help_title)
        helpContent = view.findViewById(R.id.help_content)

        val closeButton = view.findViewById<ImageButton>(R.id.btn_close)
        closeButton.setOnClickListener {
            dismiss()
        }

        arguments?.let {
            helpTitle.text = it.getString(ARG_TITLE)
            helpContent.text = it.getString(ARG_CONTENT)
        }
    }
}