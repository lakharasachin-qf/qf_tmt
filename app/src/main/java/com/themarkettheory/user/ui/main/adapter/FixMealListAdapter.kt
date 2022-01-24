package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.Menu
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FixMealListAdapter(
    val context: Context,
    val data: ArrayList<Menu>,
    val listener: (Menu, Int) -> Unit
) :
    RecyclerView.Adapter<FixMealListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowFixMealsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_fix_meals, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        if (data[position].discountedPrice.isNullOrEmpty()) {
            holder.binding!!.tvFinalPrice.setText(data[position].currency + data[position].price)
            holder.binding!!.rlPrice.visibility = View.GONE
        } else {
            holder.binding!!.rlPrice.visibility = View.VISIBLE
            holder.binding!!.tvFinalPrice.setText(data[position].currency + data[position].discountedPrice)
            holder.binding!!.tvPrice.setText(data[position].currency + data[position].price)
        }

        Glide.with(context).load(data[position].image).into(holder.binding!!.iv)

        if (data[position].isNonveg == 1) {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_non_veg_icon)
        } else {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_veg_icon)
        }

        if (data[position].category_id != 1) {
            holder.binding!!.ivVeg.visibility = View.GONE
            holder.binding!!.ivSpicy.visibility = View.GONE
        } else {
            holder.binding!!.ivVeg.visibility = View.VISIBLE
            holder.binding!!.ivSpicy.visibility = View.VISIBLE
        }

        if (data[position].isSpicy == 1) {
            holder.binding!!.ivSpicy.visibility = View.VISIBLE
        } else {
            holder.binding!!.ivSpicy.visibility = View.GONE
        }

        if (data[position].cart_qty!! > 0) {
            holder.binding!!.tvAdd.background =
                ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
            holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding!!.tvAdd.setText(context.getString(R.string.added))
        } else {
            holder.binding!!.tvAdd.background =
                ContextCompat.getDrawable(context, R.drawable.ic_border_rounded_r3_5)
            holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.malachite))
            holder.binding!!.tvAdd.setText(context.getString(R.string.add))
        }

        try {
            setTimer(holder, data[position].live_deals?.endTime!!, position)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.binding!!.tvAdd.setOnClickListener {
            if (!data[position].isSelected!!) {
                holder.binding!!.tvAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.binding!!.tvAdd.setText(context.getString(R.string.added))
            } else {
                holder.binding!!.tvAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_border_rounded_r3_5)
                holder.binding!!.tvAdd.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.malachite
                    )
                )
                holder.binding!!.tvAdd.setText(context.getString(R.string.add))
            }
            data[position].isSelected = !data[position].isSelected!!
            listener(data[position], position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowFixMealsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowFixMealsBinding>(itemView)

        }
    }

    fun setTimer(holder: ViewHolder, startDate: String, position: Int) {
        /*if (::timer.isInitialized) {
            timer.cancel()
        }*/
        val dateFormatGmt = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val timezone = TimeZone.getTimeZone("GMT")
        val c = Calendar.getInstance(timezone)
//        c.add(Calendar.MINUTE,-30)
//        c.add(Calendar.HOUR_OF_DAY,-5)
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"))
        val currentDate = dateFormatGmt.parse(dateFormatGmt.format(c.time))
        val currentTime = currentDate.time
        val startDate = sdf.parse(startDate)
        var millies = startDate.time - currentTime

        if (millies > 0) {
            var timer = object : CountDownTimer(millies, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    var millies = millisUntilFinished
                    val secondsInMilli: Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val hoursInMilli = minutesInMilli * 60
                    val daysInMilli = hoursInMilli * 24
                    val elapsedHours = millies / hoursInMilli
                    millies = millies % hoursInMilli
                    val elapsedMinutes = millies / minutesInMilli;
                    millies = millies % minutesInMilli;

                    val elapsedSeconds = millies / secondsInMilli;


//                tv_remaining_time.text = hour.toString() + "h " + minute.toString()+"m "+second.toString()+"s Remaining"
                    holder.binding!!.tvTime.text =
                        "${elapsedHours}h  ${elapsedMinutes}m  ${elapsedSeconds}s "

                    //here you can have your logic to set text to edittext
                }

                override fun onFinish() {
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeRemoved(0, data.size)
                }

            }.start()
        }
    }

}