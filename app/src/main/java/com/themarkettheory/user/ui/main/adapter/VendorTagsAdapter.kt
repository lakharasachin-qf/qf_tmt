package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowVendorTagBinding
import com.themarkettheory.user.model.VendorTag


class VendorTagsAdapter(val context: Activity, val data: List<VendorTag>) :
    RecyclerView.Adapter<VendorTagsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowVendorTagBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_vendor_tag, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        holder.binding!!.tvTags.text = "• " + data[position].name
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowVendorTagBinding? = null

        init {
            binding = DataBindingUtil.bind<RowVendorTagBinding>(itemView)

        }
    }

}