package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyCartLiveDealNewBinding
import com.themarkettheory.user.interfaces.ListClickListenerCart
import com.themarkettheory.user.newmodels.bucketcart.CartList

class LiveDealBucketNewAdapter(
    private val context: Activity,
    private val listener: ListClickListenerCart
) : RecyclerView.Adapter<LiveDealBucketNewAdapter.Holder>() {
    //General
    private var cartList = ArrayList<CartList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_my_cart_live_deal_new,
                FrameLayout(parent.context),
                false
            )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val cartRowData = cartList[position]

            /*Cart isVegOrNonVeg*/
            holder.rowMyCartLiveDealNewBinding.ivCartLiveDealListVegNonVeg.setImageDrawable(
                when (cartRowData.menu!!.foodType!!) {
                    1 -> ContextCompat.getDrawable(context, R.drawable.ic_veg_icon)
                    2 -> ContextCompat.getDrawable(context, R.drawable.ic_non_veg_icon)
                    else -> ContextCompat.getDrawable(context, R.drawable.food_type_egg)
                }
            )

            /*Cart IsSpicy*/
            holder.rowMyCartLiveDealNewBinding.ivCartLiveDealListSpicy.setImageDrawable(
                if (cartRowData.menu!!.isSpicy!! == 1) ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_spicy
                )
                else ContextCompat.getDrawable(context, R.drawable.ic_not_spicy)
            )

            /*Dish Name*/
            holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListItemName.text =
                cartRowData.menu!!.title!!.trim()

            /*Dish Ingredients*/

            holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListCategory.apply {
                text = cartRowData.menu!!.ingredients!!.trim()
                visibility =
                    if (cartRowData.menu!!.ingredients!!.trim().isEmpty() ||
                        cartRowData.menu!!.ingredients!!.trim() == "0"
                    ) View.GONE else View.VISIBLE
            }

            if (cartRowData.menu!!.finalPrice != cartRowData.menu!!.actualPrice) {
                holder.rowMyCartLiveDealNewBinding.tvMenuListActualPrice.visibility = View.VISIBLE
                holder.rowMyCartLiveDealNewBinding.tvMenuListActualPrice.text =
                    "${cartRowData.menu!!.currency!!.trim()}${
                        cartRowData.menu!!.actualPrice.toString().trim()
                    }"
                holder.rowMyCartLiveDealNewBinding.tvMenuListActualPrice.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.rowMyCartLiveDealNewBinding.tvMenuListActualPrice.visibility = View.GONE
            }


            holder.rowMyCartLiveDealNewBinding.tvMenuListPointLabel.visibility =
                if (cartRowData.menu!!.point!! > 0) View.VISIBLE else View.GONE
            holder.rowMyCartLiveDealNewBinding.tvMenuListPoint.visibility =
                if (cartRowData.menu!!.point!! > 0) View.VISIBLE else View.GONE
            holder.rowMyCartLiveDealNewBinding.tvMenuListPoint.apply {
                text = cartRowData.menu!!.point.toString().trim()
                visibility =
                    if (cartRowData.menu!!.point.toString().trim()
                            .isEmpty() || cartRowData.menu!!.point == 0
                    ) View.GONE else View.VISIBLE
            }


            /*Dish Final Price*/
            holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListFinalPrice.text =
                "${cartRowData.menu!!.currency!!.trim()} ${cartRowData.menu!!.finalPrice!!}"

            /*Dish Qty*/
            holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListCount.text =
                cartRowData.qty!!.toString().trim()

            holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListTime.apply {
                text = cartRowData.menu!!.preparingTime!!.trim()
                visibility =
                    if (cartRowData.menu!!.preparingTime!!.trim().isEmpty() ||
                        cartRowData.menu!!.preparingTime!!.trim() == "0 mins"
                    ) View.GONE else View.VISIBLE
            }

            /*Dish Add*/
            holder.rowMyCartLiveDealNewBinding.ivCartLiveDealListPlus.setOnClickListener {
                val itemQty = cartRowData.qty!! + 1
                holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListCount.text =
                    itemQty.toString().trim()
                cartRowData.qty = itemQty
                listener.onClickListener(holder.view, position, cartRowData, true)
            }

            /*Dish Minus*/
            holder.rowMyCartLiveDealNewBinding.ivCartLiveDealListMinus.setOnClickListener {
                var itemQty = cartRowData.qty!! - 1
                if (itemQty <= 0) itemQty = 0
                holder.rowMyCartLiveDealNewBinding.tvCartLiveDealListCount.text =
                    itemQty.toString().trim()
                cartRowData.qty = itemQty
                listener.onClickListener(holder.view, position, cartRowData, false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun addLiveDealCart(alCartList: ArrayList<CartList>) {
        try {
            if (alCartList.isNotEmpty()) {
                cartList.clear()
                cartList.addAll(alCartList)
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val rowMyCartLiveDealNewBinding: RowMyCartLiveDealNewBinding =
            DataBindingUtil.bind(itemView)!!
    }
}