package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowRecentSearchBinding
import com.themarkettheory.user.model.RecentList

class RecentSearchListAdapter(
    val context: Context,
    val data: List<RecentList>,
    val listener: (RecentList) -> Unit
) :
    RecyclerView.Adapter<RecentSearchListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowRecentSearchBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_recent_search, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        holder.binding!!.ll.setOnClickListener {
            listener(data[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowRecentSearchBinding? = null

        init {
            binding = DataBindingUtil.bind<RowRecentSearchBinding>(itemView)

        }
    }

}