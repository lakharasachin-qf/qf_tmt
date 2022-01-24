package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowSelectSpecialistBinding
import com.themarkettheory.user.model.Data

class SelectSpecialistListAdapter(
    val context: Context,
    val data: List<Data>,
    val listener: (Data) -> Unit
) :
    RecyclerView.Adapter<SelectSpecialistListAdapter.ViewHolder>() {
    private var currentPosition: Int = -1
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowSelectSpecialistBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_select_specialist, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        if (currentPosition == position) {
            holder.binding!!.iv.borderWidth = Utils.dpToPx(1.0f, context.resources.displayMetrics)
            holder.binding!!.iv.borderColor =
                ContextCompat.getColor(context, R.color.robins_egg_blue)
            holder.binding!!.tvSpecialistName.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.robins_egg_blue
                )
            )
        } else {
            holder.binding!!.iv.borderWidth = Utils.dpToPx(0.0f, context.resources.displayMetrics)
            holder.binding!!.iv.borderColor =
                ContextCompat.getColor(context, android.R.color.transparent)
            holder.binding!!.tvSpecialistName.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
        }

        holder.binding!!.tvSpecialistName.setText(data[getItemViewType(position)].name)
        Glide.with(context).load(data[getItemViewType(position)].profilePic)
            .into(holder.binding!!.iv)

        holder.binding!!.ll.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowSelectSpecialistBinding? = null

        init {
            binding = DataBindingUtil.bind<RowSelectSpecialistBinding>(itemView)

        }
    }


}