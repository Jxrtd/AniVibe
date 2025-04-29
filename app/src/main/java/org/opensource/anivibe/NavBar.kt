package org.opensource.anivibe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.opensource.anivibe.databinding.NavbarBinding
import org.opensource.anivibe.fragments.LandingPageFragment
import org.opensource.anivibe.fragments.ProfileFragment
import org.opensource.anivibe.fragments.SaveAnimeListFragment

class NavBar : AppCompatActivity() {
    private var binding: NavbarBinding? = null
    private var selectedButtonId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NavbarBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        supportActionBar?.hide()

        val menubutton = findViewById<ImageButton>(R.id.menubutton)

        if (savedInstanceState == null) {
            replaceFragment(LandingPageFragment())
            updateButtonState(binding!!.homebtn)
        }
        binding!!.watchlist.setOnClickListener { v ->
            replaceFragment(SaveAnimeListFragment())
            updateButtonState(binding!!.watchlist)
        }
        binding!!.profilebutton.setOnClickListener { v ->
            replaceFragment(ProfileFragment())
            updateButtonState(binding!!.profilebutton)
        }
        binding!!.homebtn.setOnClickListener { v ->
            replaceFragment(LandingPageFragment())
            updateButtonState(binding!!.homebtn)
        }
        binding!!.btnchat.setOnClickListener { v ->
            replaceFragment(ChatFragment())
            updateButtonState(binding!!.btnchat)
        }


        menubutton.setOnClickListener {
            Log.d("CSIT 284", "Sign-Up Clicked")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer1, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun updateButtonState(clickedButton: ImageButton) {
        if (selectedButtonId != null) {
            findViewById<View>(selectedButtonId!!).isSelected = false
        }
        clickedButton.setSelected(true)
        selectedButtonId = clickedButton.id
    }
}
