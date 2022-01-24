package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.SaloonSpaList

class SalonSelectedServicesAdapter(val context: Context, val data: ArrayList<SaloonSpaList>) :
    RecyclerView.Adapter<SalonSelectedServicesAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowSalonServiceBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_salon_service, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        if (data[position].discountedPrice.isNullOrEmpty()) {
            holder.binding!!.tvPrice.setText(data[position].currency + data[position].mrpPrice)
        } else {
            holder.binding!!.tvPrice.setText(data[position].currency + data[position].discountedPrice)
        }
        holder.binding!!.tvTitle.setText(data[position].title)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowSalonServiceBinding? = null

        init {
            binding = DataBindingUtil.bind<RowSalonServiceBinding>(itemView)

        }
    }

}