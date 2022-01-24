package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowBookingsBinding
import com.themarkettheory.user.model.MyBookingList


class BookingListAdapter(
    val context: Activity,
    val data: List<MyBookingList>,
    val listener: (MyBookingList) -> Unit
) :
    RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowBookingsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_bookings, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[getItemViewType(position)]

        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)
        holder.binding!!.tvDate.setText(
            Utils.formatDate(
                data[getItemViewType(position)].bookingDate!!,
                "yyyy-MM-dd",
                "dd MMM yyyy"
            ) + " | " + data[getItemViewType(position)].bookingTime
        )
        holder.binding!!.rl.setOnClickListener {
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowBookingsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowBookingsBinding>(itemView)

        }
    }

}