package org.opensource.anivibe.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R
import org.opensource.anivibe.ToPostActivity
import org.opensource.anivibe.data.Item
import org.opensource.anivibe.helper.ItemAdapter
import org.opensource.anivibe.repository.PostRepository

class LandingPageFragment : Fragment(R.layout.anivibe_landingpagefragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val itemList = mutableListOf<Item>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        itemAdapter = ItemAdapter(requireContext(), itemList) { position ->
            deletePost(position)
        }

        recyclerView.adapter = itemAdapter

        refreshPosts()

        view.findViewById<View>(R.id.post).setOnClickListener {
            val intent = Intent(requireContext(), ToPostActivity::class.java)
            postActivityLauncher.launch(intent)
        }

    }

    private val postActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                refreshPosts()
            }
        }

    private fun refreshPosts() {
        itemList.clear()
        itemList.addAll(PostRepository.getPosts(requireContext()))
        itemAdapter.notifyDataSetChanged()
    }

    private fun deletePost(position: Int) {
        PostRepository.deletePost(requireContext(), position)
        itemList.removeAt(position)
        itemAdapter.notifyItemRemoved(position)
    }
}
