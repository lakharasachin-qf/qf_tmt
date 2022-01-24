package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowParticipantsBinding
import com.themarkettheory.user.model.Participant


class ParticipantListAdapter(val context: Activity, val data: List<Participant>) :
    RecyclerView.Adapter<ParticipantListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowParticipantsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_participants, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowParticipantsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowParticipantsBinding>(itemView)

        }
    }

}