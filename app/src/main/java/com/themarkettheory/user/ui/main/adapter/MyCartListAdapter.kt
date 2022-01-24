package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyCartBinding
import com.themarkettheory.user.model.CartList

class MyCartListAdapter(
    val context: Context,
    val data: ArrayList<CartList>,
    val listener: (ArrayList<CartList>, Int) -> Unit
) :
    RecyclerView.Adapter<MyCartListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowMyCartBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_my_cart, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getCartData(): ArrayList<CartList> {
        return data
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[getItemViewType(position)]

        if (data[getItemViewType(position)].menu?.isNonveg == 0) {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_veg_icon)
        } else {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_non_veg_icon)
        }

        if (data[getItemViewType(position)].menu?.isSpicy == 1) {
            holder.binding!!.ivSpicy.visibility = View.VISIBLE
        } else {
            holder.binding!!.ivSpicy.visibility = View.GONE
        }
        holder.binding!!.tvTitle.setText(data[position].menu?.title)
        holder.binding!!.tv.setText(data[position].menu?.cuisines_name)

        if (data[getItemViewType(position)].category_id == "1") {
            holder.binding!!.llRestaurantIndicators.visibility = View.GONE
        } else {
            holder.binding!!.llRestaurantIndicators.visibility = View.VISIBLE
        }

        if (data[getItemViewType(position)].menu?.discountedPrice.isNullOrEmpty()) {
            holder.binding!!.rlDiscountedPrice.visibility = View.GONE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].menu?.currency + data[getItemViewType(
                    position
                )].menu?.price
            )
        } else {
            holder.binding!!.rlDiscountedPrice.visibility = View.VISIBLE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].menu?.currency + data[getItemViewType(
                    position
                )].menu?.discountedPrice
            )
            holder.binding!!.tvPrice.setText(
                data[getItemViewType(position)].menu?.currency + data[getItemViewType(
                    position
                )].menu?.price
            )
        }

        if (data[getItemViewType(position)].is_redeem == "1") {
            holder.binding!!.rlDiscountedPrice.visibility = View.GONE
            holder.binding!!.tvFinalPrice.setText(data[getItemViewType(position)].menu?.currency + "0")
        }

        holder.binding!!.ivMinus.setOnClickListener {
            val count = holder.binding!!.tvCount.text.toString()
            if (count.toInt() > 1) {
                holder.binding!!.tvCount.setText((count.toInt() - 1).toString())
                data[getItemViewType(position)].qty =
                    holder.binding!!.tvCount.text.toString().toInt()
                listener(data, getItemViewType(position))
            } else {
                data[getItemViewType(position)].qty = 0
                listener(data, getItemViewType(position))
                data.removeAt(getItemViewType(position))
                notifyItemRemoved(getItemViewType(position))
            }
        }

        holder.binding!!.ivPlus.setOnClickListener {
            if (data[getItemViewType(position)].is_redeem == "0") {
                val count = holder.binding!!.tvCount.text.toString()
                holder.binding!!.tvCount.setText((count.toInt() + 1).toString())
                data[getItemViewType(position)].qty =
                    holder.binding!!.tvCount.text.toString().toInt()
                listener(data, getItemViewType(position))
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowMyCartBinding? = null

        init {
            binding = DataBindingUtil.bind<RowMyCartBinding>(itemView)

        }
    }

}