package org.opensource.anivibe.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView
import org.opensource.anivibe.R

class ProfileFragment : Fragment(R.layout.anivibe_profilefragment) {

    private lateinit var btnMyDetails: Button
    private lateinit var btnMyAnimeStats: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMyDetails    = view.findViewById(R.id.mydetails)
        btnMyAnimeStats = view.findViewById(R.id.myanimestat)
        val container   = view.findViewById<FrameLayout>(R.id.fragmentContainer2)

        checkNotNull(container) { "fragmentContainer2 missing!" }

        setActiveTab(btnMyDetails)
        setInactiveTab(btnMyAnimeStats)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer2, MyDetailsFragment())
                .commit()
        }

        btnMyDetails.setOnClickListener {
            replaceChild(MyDetailsFragment(), btnMyDetails, btnMyAnimeStats)
        }

        btnMyAnimeStats.setOnClickListener {
            replaceChild(AnimeStatFragment(), btnMyAnimeStats, btnMyDetails)
        }

        setupBackPressHandler()
    }

    private fun replaceChild(
        fragment: Fragment,
        active: Button,
        inactive: Button
    ) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, fragment)
            .commit()

        setActiveTab(active)
        setInactiveTab(inactive)
    }

    private fun setActiveTab(button: Button) {
        button.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.accentRed)
        )
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun setInactiveTab(button: Button) {
        button.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.tabInactive)
        )
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStackImmediate()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileInfo()
    }

    private fun loadProfileInfo() {
        val prefs = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val name = prefs.getString("username", "Your Name")!!
        requireView().findViewById<TextView>(R.id.profile_name_textview).text = name

        val bio = prefs.getString("bio", "")
        requireView().findViewById<TextView>(R.id.profile_bio_textview).text =
            if (bio.isNullOrBlank()) "No bio yet" else bio

        val imgView = requireView().findViewById<CircleImageView>(R.id.main_profile_image)
        val picPrefs = requireContext()
            .getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        try {
            picPrefs.getString("profile_image", null)?.let { filename ->
                val actualFilename = if (filename.contains("/")) {
                    filename.substring(filename.lastIndexOf("/") + 1)
                } else {
                    filename
                }

                try {
                    requireContext().openFileInput(actualFilename).use { fis ->
                        imgView.setImageBitmap(BitmapFactory.decodeStream(fis))
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProfileFragment", "Error loading profile image", e)
                    imgView.setImageResource(R.drawable.profile_circle)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileFragment", "Error in loadProfileInfo", e)
            imgView.setImageResource(R.drawable.profile_circle)
        }
    }
}