package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyEventsBinding
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.MyEventList

class MyEventsAdapter(
    val context: Context,
    val list: ArrayList<MyEventList>,
    val listener: (MyEventList) -> Unit
) :
    RecyclerView.Adapter<MyEventsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowMyEventsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_my_events, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = list[position]

        Glide.with(context).load(list[position].eventDetail?.image).into(holder.binding!!.iv)
        holder.binding!!.tvDate.setText(
            Utils.formatDate(
                list[position].date!!,
                "yyyy-MM-dd",
                "dd, MMM yyyy"
            ) + " | " + list[position].time
        )

        holder.binding!!.rl.setOnClickListener {
            listener(list[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowMyEventsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowMyEventsBinding>(itemView)

        }
    }


}