package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import java.util.*

class RestaurantQuickBiteItemsAdapter(val context: Context) :
    RecyclerView.Adapter<RestaurantQuickBiteItemsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(3)

    init {
        Arrays.fill(flags, false)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowQuickBiteItemsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_quick_bite_items, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
//        holder.binding!!.ivRestaurant.setImageResource(images[position])
        holder.binding!!.tvAdd.setOnClickListener {
            if (!flags[position]!!) {
                holder.binding!!.tvAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.binding!!.tvAdd.setText(context.getString(R.string.added))
            } else {
                holder.binding!!.tvAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_border_rounded_r3_5)
                holder.binding!!.tvAdd.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.malachite
                    )
                )
                holder.binding!!.tvAdd.setText(context.getString(R.string.add))
            }
            flags[position] = !flags[position]!!
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowQuickBiteItemsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowQuickBiteItemsBinding>(itemView)

        }
    }

}