package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyOrdersNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.myorders.MyOrderNewData


class OrderListAdapter(val context: Activity, private val listener: ListClickListener) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    //General
    private var myOrderNewDataList = ArrayList<MyOrderNewData>()

    //Booking Status Type
    private val newOrderType = 0
    private val approvedType = 1
    private val declineType = 2
    private val completedType = 3
    private val cancelledType = 4

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_my_orders_new, FrameLayout(p0.context), false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return myOrderNewDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val myOrderNewData = myOrderNewDataList[position]

            //Restaurant Image
            if (myOrderNewData.serviceDetails!!.image.isNullOrEmpty()) {
                holder.bindingMyOrders.ivMyOrdersNewImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(myOrderNewData.serviceDetails!!.image!!)
                    .error(R.drawable.ic_camera)
                    .into(holder.bindingMyOrders.ivMyOrdersNewImage)
            }

            //setting up restaurant title
            holder.bindingMyOrders.tvMyOrdersNewRestaurantName.text =
                myOrderNewData.serviceDetails!!.title!!.trim()

            //setting up order number
            holder.bindingMyOrders.tvMyOrdersNewOrderNumber.text =
                "#${myOrderNewData.orderDetails!!.orderNumber!!.trim()}"

            //setting up booking date
            holder.bindingMyOrders.tvMyOrdersNewDate.text = PubFun.parseDate(
                myOrderNewData.bookingDate!!,
                Config.requestDateFormat,
                Config.defaultDateFormat
            )

            //setting up booking time
            holder.bindingMyOrders.tvMyOrdersNewTime.text = PubFun.parseDate(
                myOrderNewData.bookingTime!!, Config.requestTimeFormat, Config.defaultTimeFormat
            )

            //setting up rating
            holder.bindingMyOrders.tvMyOrdersNewRating.text =
                myOrderNewData.serviceDetails!!.rating!!

            //setting up rating icon
            holder.bindingMyOrders.rbMyOrdersNewStar.rating =
                myOrderNewData.serviceDetails!!.rating!!.toFloat()

            //isVeg
            holder.bindingMyOrders.ivMyOrdersNewVeg.visibility =
                if (myOrderNewData.serviceDetails!!.isVeg == 1) View.VISIBLE else View.GONE
            //isNong
            holder.bindingMyOrders.ivMyOrdersNewNonVeg.visibility =
                if (myOrderNewData.serviceDetails!!.isNonVeg == 1) View.VISIBLE else View.GONE
            //isEgg
            holder.bindingMyOrders.ivMyOrdersNewEgg.visibility =
                if (myOrderNewData.serviceDetails!!.isEgg == 1) View.VISIBLE else View.GONE

            //setting up the price
            holder.bindingMyOrders.tvMyOrderListPrice.text =
                "${myOrderNewData.serviceDetails!!.currency}${
                    myOrderNewData.orderDetails!!.total!!.toString().trim()
                }"

            //region Booking Status
            holder.bindingMyOrders.tvMyOrdersNewStatus.text = when (myOrderNewData.status!!) {
                newOrderType -> "Pending" //0
                approvedType -> "Approved" //1
                declineType -> "Declined" //2
                completedType -> "Completed" //3
                else -> "Cancelled" //4
            }
            holder.bindingMyOrders.tvMyOrdersNewStatus.background =
                ContextCompat.getDrawable(
                    Config.mainActivity!!, when (myOrderNewData.status!!) {
                        newOrderType -> R.drawable.bg_my_pumpkin_circle
                        approvedType -> R.drawable.bg_my_green_circle
                        completedType -> R.drawable.bg_my_robbins_egg_blue_circle
                        else -> R.drawable.bg_my_sunset_orange_circle
                    }
                )
            //endregion

            //View On Click Listener
            holder.view.setOnClickListener {
                listener.onClickListener(holder.view, position, myOrderNewData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addMyOrderDataList(alMyOrder: ArrayList<MyOrderNewData>) {
        try {
            if (alMyOrder.isNotEmpty()) {
                this.myOrderNewDataList.clear()
                this.myOrderNewDataList = alMyOrder
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val bindingMyOrders: RowMyOrdersNewBinding = DataBindingUtil.bind(itemView)!!
    }

}