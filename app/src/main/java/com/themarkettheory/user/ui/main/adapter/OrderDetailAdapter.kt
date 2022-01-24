package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyOrderDetailsNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.ui.restaurant.MyBucketCartRes
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderDetailAdapter :
    RecyclerView.Adapter<OrderDetailAdapter.Holder>() {
    private var orderMenuList = ArrayList<MyBucketCartRes>()
    private val numberFormat: NumberFormat = DecimalFormat("#0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_my_order_details_new,
            FrameLayout(parent.context), false
        )
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return orderMenuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val bucketCartData = orderMenuList[position]
            //setting image for veg nonveg
            holder.RowMyOrderDetailsNewBinding!!.ivRowOrderDetailsListVegNonVeg.setImageDrawable(
                ContextCompat.getDrawable(
                    Config.mainActivity!!,
                    when (bucketCartData.foodType) {
                        1 -> R.drawable.ic_veg_icon
                        2 -> R.drawable.ic_non_veg_icon
                        else -> R.drawable.food_type_egg
                    }
                )
            )
            // spicy or not
            holder.RowMyOrderDetailsNewBinding.ivRowOrderDetailsListSpicy.setImageDrawable(
                ContextCompat.getDrawable(
                    Config.mainActivity!!,
                    when {
                        (bucketCartData.isSpicy) == 1 -> R.drawable.ic_spicy
                        else -> R.drawable.ic_not_spicy
                    }
                )
            )

            //setting the menu Title
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListItemName.text =
                bucketCartData.title.trim()

            //setting the menu category name
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListCategory.text =
                bucketCartData.categoryName.trim()

            // final price
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListFinalPrice.text =
                "${bucketCartData.currency}${numberFormat.format(bucketCartData.finalPrice)}"

            //actual price
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListActualPrice.apply {
                visibility =
                    if (bucketCartData.finalPrice != bucketCartData.actualPrice)
                        View.VISIBLE else View.GONE
                text =
                    "${bucketCartData.currency}${numberFormat.format(bucketCartData.actualPrice)}"
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }

            //points
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListPoint.text =
                bucketCartData.point.toString()

            // preparing time
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListTime.text =
                bucketCartData.preparingTime.trim()

            // item quantity
            holder.RowMyOrderDetailsNewBinding.tvRowOrderDetailsListCount.text =
                bucketCartData.qty.toString().trim()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBucketData(alOrderDetail: ArrayList<MyBucketCartRes>) {
        try {
            if (alOrderDetail.isNotEmpty()) {
                orderMenuList.apply {
                    clear()
                    addAll(alOrderDetail)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val RowMyOrderDetailsNewBinding =
            DataBindingUtil.bind<RowMyOrderDetailsNewBinding>(itemView)

    }
}