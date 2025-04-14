package org.opensource.anivibe.fragments

import android.content.Context
import android.content.Intent
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
import org.opensource.anivibe.EditProfileActivity
import org.opensource.anivibe.R

class ProfileFragment : Fragment(R.layout.anivibe_profilefragment) {

    private lateinit var btnMyDetails: Button
    private lateinit var btnMyAnimeStats: Button
    private lateinit var btnEditProfile: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMyDetails    = view.findViewById(R.id.mydetails)
        btnMyAnimeStats = view.findViewById(R.id.myanimestat)
        btnEditProfile  = view.findViewById(R.id.btnEditProfile)
        val container    = view.findViewById<FrameLayout>(R.id.fragmentContainer2)

        checkNotNull(container)     { "fragmentContainer2 missing!" }
        checkNotNull(btnEditProfile){ "btnEditProfile missing!" }

        // 1) Default state: MyDetails active
        setActiveTab(btnMyDetails)
        setInactiveTab(btnMyAnimeStats)

        // 2) Load the first child fragment only once
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer2, MyDetailsFragment())
                .commit()
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
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
            // if you ever want to pop your child back stack:
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

        // Username
        val name = prefs.getString("username", "Your Name")!!
        requireView().findViewById<TextView>(R.id.profile_name_textview).text = name

        // Bio
        val bio = prefs.getString("bio", "")
        requireView().findViewById<TextView>(R.id.profile_bio_textview).text =
            if (bio.isNullOrBlank()) "No bio yet" else bio

        // Profile pic
        val imgView = requireView().findViewById<CircleImageView>(R.id.main_profile_image)
        val picPrefs = requireContext()
            .getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        picPrefs.getString("profile_image", null)?.let { fn ->
            requireContext().openFileInput(fn).use { fis ->
                imgView.setImageBitmap(BitmapFactory.decodeStream(fis))
            }
        }
    }
}
