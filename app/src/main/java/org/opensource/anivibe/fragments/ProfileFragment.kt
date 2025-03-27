package org.opensource.anivibe.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.opensource.anivibe.R

class ProfileFragment : Fragment(R.layout.anivibe_profilefragment) {
    private lateinit var btnMyDetails: Button
    private lateinit var btnMyAnimeStats: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMyDetails = view.findViewById(R.id.mydetails)
        btnMyAnimeStats = view.findViewById(R.id.myanimestat)

        // Load MyDetailsFragment as the default fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, MyDetailsFragment())
            .commit()

        // Set default button colors
        setActiveButton(btnMyDetails)
        setInactiveButton(btnMyAnimeStats)

        btnMyDetails.setOnClickListener {
            replaceFragment(MyDetailsFragment(), btnMyDetails, btnMyAnimeStats)
        }

        btnMyAnimeStats.setOnClickListener {
            replaceFragment(AnimeStatFragment(), btnMyAnimeStats, btnMyDetails)
        }
    }

    private fun replaceFragment(fragment: Fragment, activeButton: Button, inactiveButton: Button) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer2, fragment)
            .commit()

        setActiveButton(activeButton)
        setInactiveButton(inactiveButton)
    }

    private fun setActiveButton(button: Button) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun setInactiveButton(button: Button) {
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }
}
