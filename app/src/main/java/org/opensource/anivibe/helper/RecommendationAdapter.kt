package org.opensource.anivibe.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.opensource.anivibe.R

class RecommendationAdapter(private val recommendations: List<String>) :
    RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    class RecommendationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankTextView: TextView = view.findViewById(R.id.recommendationRankTextView)
        val nameTextView: TextView = view.findViewById(R.id.recommendationNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recommendation_item, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.rankTextView.text = "${position + 1}."
        holder.nameTextView.text = recommendation
    }

    override fun getItemCount() = recommendations.size.coerceAtMost(3)
}