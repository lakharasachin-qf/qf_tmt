package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowOrderConfirmationNewBinding
import com.themarkettheory.user.newmodels.orderconfirmation.MenuDetail
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderConfirmationAdapter() : RecyclerView.Adapter<OrderConfirmationAdapter.Holder>() {
    private var orderConfirmationDataList = ArrayList<MenuDetail>()
    private val numberFormat: NumberFormat = DecimalFormat("#0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_order_confirmation_new, FrameLayout(parent.context), false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val orderConfirmationData = orderConfirmationDataList[position]

        //setting up the title, qty, unit in one
        holder.RowOrderConfirmationNewBinding!!.tvRowOrderConfirmationDetails.text =
            "${orderConfirmationData.menu!!.title} " +
                    "(${orderConfirmationData.menu!!.dishQty} " +
                    "${orderConfirmationData.menu!!.unit}) X " +
                    "${orderConfirmationData.qty}"

        // setting up the price
        holder.RowOrderConfirmationNewBinding.tvRowOrderConfirmationTotal.text =
            "${orderConfirmationData.menu!!.currency}" +
                    "${
                        numberFormat.format(
                            orderConfirmationData.menu!!.finalPrice!!.replace(
                                ",",
                                ""
                            ).toDouble()
                        )
                    }"
    }

    override fun getItemCount(): Int {
        return orderConfirmationDataList.size
    }

    fun addConfirmationOrderDataList(alConfirmOrder: ArrayList<MenuDetail>) {
        try {
            if (alConfirmOrder.isNotEmpty()) {
                this.orderConfirmationDataList.clear()
                this.orderConfirmationDataList = alConfirmOrder
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val RowOrderConfirmationNewBinding =
            DataBindingUtil.bind<RowOrderConfirmationNewBinding>(itemView)
    }


}