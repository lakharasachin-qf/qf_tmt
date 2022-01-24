package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowTagsBinding
import com.themarkettheory.user.newmodels.overview.Tag


class TagListAdapter(val context: Activity, val data: List<Tag>) :
    RecyclerView.Adapter<TagListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowTagsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_tags, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        holder.binding!!.tvTag.setText(data[position].name)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowTagsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowTagsBinding>(itemView)

        }
    }

}