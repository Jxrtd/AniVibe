package org.opensource.anivibe

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class Application : AppCompatActivity() {
    
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer1, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}