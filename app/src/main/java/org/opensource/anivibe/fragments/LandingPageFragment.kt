package org.opensource.anivibe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.ToPostActivity
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.ItemAdapter

class LandingPageFragment : Fragment(R.layout.anivibe_landingpagefragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = listOf(
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),
            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime"),


            Item("@username", R.drawable.profile1,"just_rewatched_attack_on_titan_and_i_m_still_not_over_that_ending_eren_was_really_out_here_playing_4d_chess_while_everyone_else_was_in_a_sh_nen_aot_anime")


        )

        val adapter = ItemAdapter(requireContext(), items)
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.post).setOnClickListener {
            startActivity(Intent(requireContext(), ToPostActivity::class.java))
        }
    }
}
