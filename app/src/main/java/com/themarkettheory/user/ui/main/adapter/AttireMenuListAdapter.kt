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
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.RetailMenu


class AttireMenuListAdapter(
    val context: Activity,
    val data: List<RetailMenu>,
    val listener: (RetailMenu) -> Unit
) :
    RecyclerView.Adapter<AttireMenuListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAttireMenuBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_attire_menu, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[getItemViewType(position)]
        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)
        if (data[getItemViewType(position)].discountedPrice.isNullOrEmpty()) {
            holder.binding!!.tvPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].price.toString()
            )
        } else {
            holder.binding!!.tvPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].discountedPrice.toString()
            )
        }

        holder.binding!!.rl.setOnClickListener {
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAttireMenuBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAttireMenuBinding>(itemView)

        }
    }

}