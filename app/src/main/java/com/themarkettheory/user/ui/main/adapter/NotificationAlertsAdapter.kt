package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowNotificationAlertBinding
import com.themarkettheory.user.databinding.RowNotificationReminderBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.model.NotificationList
import com.themarkettheory.user.ui.main.activity.MyOrdersActivity
import com.themarkettheory.user.ui.main.activity.MyTableBookingsActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NotificationAlertsAdapter(val context: Activity, val data: List<NotificationList>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentPosition: Int = 0
    private val TYPE_ALERT = 1
    private val TYPE_REMINDER = 2
    private val TYPE_UPDATE = 3
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        var layout = 0
        val viewHolder: RecyclerView.ViewHolder?
        when (p1) {
            TYPE_ALERT -> {
                layout = R.layout.row_notification_alert
                val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                viewHolder = AlertViewHolder(view)
            }
            TYPE_REMINDER -> {
                //layout = R.layout.row_notification_reminder
                layout = R.layout.row_notification_reminder
                val videoView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                viewHolder = ReminderViewHolder(videoView)
            }
            /*TYPE_UPDATE -> {
                layout = R.layout.row_notification_version
                val updateView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                viewHolder = VersionViewHolder(updateView)
            }*/
            else -> viewHolder = null

        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return TYPE_ALERT
        /*else if(position == 2)
            return TYPE_UPDATE*/
        /*else if(data[position].notificationType == 1 || data[position].notificationType == 4)
            return TYPE_REMINDER*/
        else
            return TYPE_REMINDER
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.binding?.obj = data[position]
        val viewType = holder.itemViewType
        when (viewType) {
            TYPE_ALERT -> (holder as AlertViewHolder).showDetails(position)
            TYPE_REMINDER -> (holder as ReminderViewHolder).showDetails(position)
            //TYPE_UPDATE -> (holder as VersionViewHolder).showDetails(position)
        }

    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNotificationAlertBinding? = null

        init {
            binding = DataBindingUtil.bind<RowNotificationAlertBinding>(itemView)
        }

        fun showDetails(position: Int) {
            binding!!.obj = data[position]
//            Glide.with(context).load(data[position].image).into(binding!!.ivOffer)

            var format: DateFormat = SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH)

            binding!!.timeDate.text = PubFun.parseDate(
                data[position].date,
                Config.requestDateFormat,
                Config.newPattern
            ) + " " + PubFun.parseDate(
                data[position].time,
                Config.requestTimeFormat,
                Config.defaultTimeFormat
            )
            binding!!.title.text = data[position].title
            binding!!.description.text = data[position].message

            binding!!.ll.setOnClickListener {
                if (data[position].type == 8 || data[position].type == 4 || data[position].type == 18) {
                    context.startActivity(
                        Intent(context, MyTableBookingsActivity::class.java)
                    )
                } else if (data[position].type == 5 || data[position].type == 6 || data[position].type == 15) {
                    context.startActivity(
                        Intent(
                            context,
                            MyOrdersActivity::class.java
                        ).putExtra("type", Constants.Order).putExtra("notification", "yes")
                    )
                }
            }
        }
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNotificationReminderBinding? = null

        init {
            binding = DataBindingUtil.bind<RowNotificationReminderBinding>(itemView)
        }

        fun showDetails(position: Int) {
            binding!!.obj = data[position]
            var format: DateFormat = SimpleDateFormat("yyyy-MM-DD", Locale.ENGLISH)

            binding!!.timeDate.text = PubFun.parseDate(
                data[position].date,
                Config.requestDateFormat,
                Config.newPattern
            ) + " " + PubFun.parseDate(
                data[position].time,
                Config.requestTimeFormat,
                Config.defaultTimeFormat
            )

            binding!!.title.text = data[position].title
            binding!!.description.text = data[position].message

            binding!!.ll.setOnClickListener {
                if (data[position].type == 8 || data[position].type == 4 || data[position].type == 18) {
                    context.startActivity(
                        Intent(context, MyTableBookingsActivity::class.java)

                    )
                } else if (data[position].type == 5 || data[position].type == 6 || data[position].type == 15) {
                    context.startActivity(
                        Intent(context, MyOrdersActivity::class.java)
                            .putExtra("type", Constants.Order).putExtra("notification", "yes")
                    )
                }
            }

//            binding!!.ll.setOnClickListener {
//                if (data[position].notificationType == 5 || data[position].notificationType == 6) {
//                    context.startActivity(
//                        Intent(context, MyOrdersActivity::class.java)
//                            .putExtra("type", Constants.Order)
//                    )
//                } else if (data[position].notificationType == 4 || data[position].notificationType == 7) {
//                    context.startActivity(Intent(context, MyTableBookingsActivity::class.java))
//                }
//            }
        }
    }

    /*inner class VersionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNotificationVersionBinding? = null
        init {
            binding = DataBindingUtil.bind<RowNotificationVersionBinding>(itemView)
        }

        fun showDetails(position: Int) {

        }
    }*/
}


/*  case BOOKING_ACCEPT     = "8"
    case BOOKING_REJECT     = "4"
    case BOOKING_CANCEL     = "18"
screen : MyBooking

case ORDER_ACCEPT       = "5"
    case ORDER_REJECT       = "6"
    case ORDER_CANCEL       = "15"
screen : MyOrder

Coupon on click target scren : trading coupon

*/