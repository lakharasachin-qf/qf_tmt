package com.themarkettheory.user.ui.main.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyCartPointNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.interfaces.ListClickListenerCart
import com.themarkettheory.user.ui.restaurant.MyBucketCartRes
import java.text.DecimalFormat
import java.text.NumberFormat

class BucketMyPointAdapter(private val listener: ListClickListenerCart) :
    RecyclerView.Adapter<BucketMyPointAdapter.Holder>() {
    private var bucketCartList = ArrayList<MyBucketCartRes>()
    val numberFormat: NumberFormat = DecimalFormat("#0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_my_cart_point_new,
            FrameLayout(parent.context), false
        )
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val bucketCartData = bucketCartList[position]
            //setting image for veg nonveg
            holder.RowMyCartNewBinding!!.ivCartPointListVegNonVeg.setImageDrawable(
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
            holder.RowMyCartNewBinding.ivCartPointListSpicy.setImageDrawable(
                ContextCompat.getDrawable(
                    Config.mainActivity!!,
                    when {
                        (bucketCartData.isSpicy) == 1 -> R.drawable.ic_spicy
                        else -> R.drawable.ic_not_spicy
                    }
                )
            )

            //setting the menu Title
            holder.RowMyCartNewBinding.tvCartPointListItemName.text =
                bucketCartData.title.trim()

            //setting the menu category name
            holder.RowMyCartNewBinding.tvCartPointListCategory.text =
                bucketCartData.categoryName.trim()

            // final price
            holder.RowMyCartNewBinding.tvCartPointListFinalPrice.text =
                "${bucketCartData.currency}${numberFormat.format(bucketCartData.finalPrice)}"

            //actual price
            holder.RowMyCartNewBinding.tvCartPointListActualPrice.apply {
                visibility =
                    if (bucketCartData.finalPrice != bucketCartData.actualPrice)
                        View.VISIBLE else View.GONE
                text =
                    "${bucketCartData.currency}${numberFormat.format(bucketCartData.actualPrice)}"
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }

            //points
            holder.RowMyCartNewBinding.tvCartPointListPoint.text =
                bucketCartData.point.toString()

            // preparing time
            holder.RowMyCartNewBinding.tvCartPointListTime.text =
                bucketCartData.preparingTime.trim()

            // item quantity
            holder.RowMyCartNewBinding.tvCartPointListCount.text =
                bucketCartData.qty.toString().trim()

            // set onclick listener
            holder.RowMyCartNewBinding.ivCartPointListMinus.setOnClickListener {
                var itemQty = bucketCartData.qty - 1
                if (itemQty <= 0) itemQty = 0
                holder.RowMyCartNewBinding.tvCartPointListCount.text = itemQty.toString()
                bucketCartData.qty = itemQty
                listener.onClickListener(holder.itemView, position, bucketCartData, false)
            }
            // set onclick litener to plus button
            holder.RowMyCartNewBinding.ivCartPointListPlus.setOnClickListener {
                val itemQty = bucketCartData.qty + 1
                holder.RowMyCartNewBinding.tvCartPointListCount.text = itemQty.toString()
                bucketCartData.qty = itemQty
                listener.onClickListener(holder.itemView, position, bucketCartData, true)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return bucketCartList.size
    }


    fun setBucketData(alBucektData: ArrayList<MyBucketCartRes>) {
        try {
            if (alBucektData.isNotEmpty()) {
                bucketCartList.apply {
                    clear()
                    addAll(alBucektData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun removeItem(position: Int) {
        try {
            if (bucketCartList.isNotEmpty()) {
                bucketCartList.removeAt(position)
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val RowMyCartNewBinding = DataBindingUtil.bind<RowMyCartPointNewBinding>(itemView)

    }
}