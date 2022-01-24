package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowAddressTagBinding

class AddressTagListAdapter(
    val context: Context,
    val item_list: List<String>,
    val listener: (String) -> Unit
) :
    RecyclerView.Adapter<AddressTagListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAddressTagBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_address_tag, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        if (currentPosition == position) {
            holder.binding!!.tvTag.background =
                ContextCompat.getDrawable(context, R.drawable.ic_address_type_bg_selected)
        } else {
            holder.binding!!.tvTag.background =
                ContextCompat.getDrawable(context, R.drawable.ic_address_type_bg_unselected)
        }
        holder.binding!!.tvTag.setText(item_list[getItemViewType(position)])

        holder.binding!!.tvTag.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
            listener(item_list[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAddressTagBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAddressTagBinding>(itemView)

        }
    }

}