package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowTableBookingsNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingData

class MyTableBookingAdapter(private val listener: ListClickListener) :
    RecyclerView.Adapter<MyTableBookingAdapter.Holder>() {

    //General
    private var tableBookingList = ArrayList<MyTableBookingData>()
    private var tableBookingFilterList = ArrayList<MyTableBookingData>()

    //Booking Status Type
    private val newOrderType = 0
    private val approvedType = 1
    private val declineType = 2
    private val completedType = 3
    private val cancelledType = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_table_bookings_new,
            FrameLayout(parent.context),
            false
        )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val dataRow = tableBookingList[position]
            //Restaurant Image
            if (dataRow.service_details!!.image.isNullOrEmpty()) {
                holder.bindingTableBooking.ivBookingNewImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        Config.mainActivity!!,
                        R.drawable.ic_camera
                    )
                )
            } else {
                Picasso.get().load(dataRow.service_details!!.image!!)
                    .error(R.drawable.ic_camera)
                    .into(holder.bindingTableBooking.ivBookingNewImage)
            }
            //isVeg
            holder.bindingTableBooking.ivBookingNewVeg.visibility =
                if (dataRow.service_details!!.is_veg == 1) View.VISIBLE else View.GONE
            //isNong
            holder.bindingTableBooking.ivBookingNewNonVeg.visibility =
                if (dataRow.service_details!!.is_non_veg == 1) View.VISIBLE else View.GONE
            //isEgg
            holder.bindingTableBooking.ivBookingNewEgg.visibility =
                if (dataRow.service_details!!.is_egg == 1) View.VISIBLE else View.GONE

            //region Booking Status
            holder.bindingTableBooking.tvBookingNewStatus.text = when (dataRow.status!!) {
                newOrderType -> "Pending" //0
                approvedType -> "Approved" //1
                declineType -> "Declined" //2
                completedType -> "Completed" //3
                else -> "Cancelled" //4
            }
            holder.bindingTableBooking.tvBookingNewStatus.background =
                ContextCompat.getDrawable(
                    Config.mainActivity!!, when (dataRow.status!!) {
                        newOrderType -> R.drawable.bg_my_pumpkin_circle
                        approvedType -> R.drawable.bg_my_green_circle
                        completedType -> R.drawable.bg_my_robbins_egg_blue_circle
                        else -> R.drawable.bg_my_sunset_orange_circle
                    }
                )
            //endregion

            //Title
            holder.bindingTableBooking.tvBookingNewRestaurantName.text =
                dataRow.service_details!!.title!!.trim()

            //region Total Person with Adult and Child Details
            holder.bindingTableBooking.clBookingNewTableBooking.visibility =
                if (dataRow.order_details!!.id!! == 0) View.VISIBLE else View.GONE
            holder.bindingTableBooking.tvBookingNewTableCount.text =
                dataRow.total_person!!.toString()
            holder.bindingTableBooking.tvBookingNewTextPersonCount.text =
                dataRow.adult!!.toString()
            holder.bindingTableBooking.tvBookingNewTextChildCount.text =
                dataRow.child!!.toString()
            //endregion

            //region Order Number
            holder.bindingTableBooking.clBookingOrder.visibility =
                if (dataRow.order_details!!.id!! == 0) View.GONE else View.VISIBLE
            holder.bindingTableBooking.tvBookingNewOrderNumber.text =
                dataRow.order_details!!.order_number!!.toString()
            //endregion

            //Booking Date
            holder.bindingTableBooking.tvBookingNewDate.text =
                PubFun.parseDate(
                    dataRow.booking_date!!.trim(),
                    Config.requestDateFormat,
                    Config.defaultDateFormat
                )
            //Booking Time
            holder.bindingTableBooking.tvBookingNewTime.text =
                PubFun.parseDate(
                    dataRow.booking_time!!.trim(),
                    Config.requestTimeFormat,
                    Config.defaultTimeFormat
                )
            //Rating Bar
            holder.bindingTableBooking.rbBookingNewStar.rating =
                dataRow.service_details!!.rating!!.toFloat()
            //Rating Count
            holder.bindingTableBooking.tvBookingNewRating.text =
                "(${dataRow.service_details!!.total_rating!!.trim()})"
            //View OnClick Listener
            holder.view.setOnClickListener {
                listener.onClickListener(holder.view, position, dataRow)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return tableBookingList.size
    }

    fun addTableBookingList(alTableBooking: ArrayList<MyTableBookingData>) {
        try {
            if (alTableBooking.isNotEmpty()) {
                this.tableBookingList.clear()
                this.tableBookingFilterList.clear()
                this.tableBookingList = alTableBooking
                this.tableBookingFilterList.addAll(alTableBooking)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val bindingTableBooking: RowTableBookingsNewBinding = DataBindingUtil.bind(itemView)!!
    }
}