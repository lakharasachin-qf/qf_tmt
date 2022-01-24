package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.OfferDatum


class AppliedCouponListAdapter(val context: Activity, val data: List<OfferDatum>) :
    RecyclerView.Adapter<AppliedCouponListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAppliedCouponBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_applied_coupon, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = data[getItemViewType(position)]

        holder.binding!!.tvCouponCode.setText("Applied Coupon (" + data[position].couponCode + ")")
        if (data[position].finaldiscountedAmount == null) {
            holder.binding!!.tvDiscount.setText(data[position].currency + data[position].applicable_discount_amount)
        } else {
            holder.binding!!.tvDiscount.setText(data[position].currency + data[position].finaldiscountedAmount)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAppliedCouponBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAppliedCouponBinding>(itemView)

        }
    }

}