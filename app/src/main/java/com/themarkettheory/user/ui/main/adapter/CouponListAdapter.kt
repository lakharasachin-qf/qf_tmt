package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowCouponBinding
import com.themarkettheory.user.model.OfferDatum
import com.themarkettheory.user.ui.coupon.CouponDetailActivity
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import java.text.SimpleDateFormat


class CouponListAdapter(
    val context: Activity,
    val data: List<OfferDatum>,
    val listener: (OfferDatum) -> Unit
) :
    RecyclerView.Adapter<CouponListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    var sdf: SimpleDateFormat = SimpleDateFormat()

    init {
        sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowCouponBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_coupon, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        try {
            val startDate = sdf.parse(data[getItemViewType(position)].startTime)
            val endDate = sdf.parse(data[getItemViewType(position)].endTime)
            var millies = endDate?.time!! - startDate?.time!!
            Glide.with(context).load(data[getItemViewType(position)].image)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.ivOffer)
            if (millies > 0) {
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24
                val elapsedHours = millies / hoursInMilli
                millies %= hoursInMilli
                val elapsedMinutes = millies / minutesInMilli
                millies %= minutesInMilli
                val elapsedSeconds = millies / secondsInMilli
                holder.binding!!.tvDuration.text = "${elapsedHours}h  ${elapsedMinutes}m"
            }

            holder.binding!!.rl.setOnClickListener {
                context.startActivity(
                    Intent(context, CouponDetailActivity::class.java)
                        .putExtra("data", data[getItemViewType(position)])
                )

            }

            if (data[position].is_active == 0) {
                holder.binding!!.tvActivateNow.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            } else {
                holder.binding!!.tvActivateNow.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.slate_gray
                    )
                )
            }

            holder.binding!!.tvActivateNow.setOnClickListener {
                if (data[position].is_active == 0) {
                    listener(data[getItemViewType(position)])
                    data[position].is_active = data[position].is_active!!.xor(1)
                    notifyDataSetChanged()
                }
            }

            holder.binding!!.tvServiceName.setOnClickListener {
                context.startActivity(
                    Intent(context, VendorDetailActivity::class.java)
                        .putExtra("serviceId", data[getItemViewType(position)].serviceId.toString())
                        .putExtra("category", data[getItemViewType(position)].category_id)
                        .putExtra("vendorTitle", data[getItemViewType(position)].service_name)
                )
            }

            /*holder.binding!!.ivOffer.setOnClickListener{
                context.startActivity(
                    Intent(context, CouponDetailActivity::class.java)
                    .putExtra("data",data[getItemViewType(position)]))
//                listener(data[getItemViewType(position)])
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowCouponBinding? = null

        init {
            binding = DataBindingUtil.bind<RowCouponBinding>(itemView)

        }
    }

}