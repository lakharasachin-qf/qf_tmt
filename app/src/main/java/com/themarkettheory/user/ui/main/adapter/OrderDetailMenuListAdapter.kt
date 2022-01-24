package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.OrderDetail

class OrderDetailMenuListAdapter(val context: Context, val data: List<OrderDetail>) :
    RecyclerView.Adapter<OrderDetailMenuListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowOrderMenusBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_order_menus, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]
        if (data[position].menu?.discountedPrice.isNullOrEmpty()) {
            holder.binding!!.tvPrice.setText(data[position].menu?.currency + data[position].menu?.price)
        } else {
            holder.binding!!.tvPrice.setText(data[position].menu?.currency + data[position].menu?.discountedPrice)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowOrderMenusBinding? = null

        init {
            binding = DataBindingUtil.bind<RowOrderMenusBinding>(itemView)

        }
    }

}