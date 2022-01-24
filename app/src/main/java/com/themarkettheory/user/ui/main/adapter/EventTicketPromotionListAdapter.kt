package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.helper.Utils


class EventTicketPromotionListAdapter(val context: Activity) :
    RecyclerView.Adapter<EventTicketPromotionListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var images = intArrayOf(R.drawable.partner1, R.drawable.partner2, R.drawable.partner3)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowEventTicketPartnersBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_event_ticket_partners, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = Utils.dpToPx(12.0f, context.resources.displayMetrics)

        if (position % 3 == 0) {
            holder.binding!!.iv.setImageResource(R.drawable.partner1)
        } else if (position % 3 == 1) {
            holder.binding!!.iv.setImageResource(R.drawable.partner2)
            params.leftMargin = Utils.dpToPx(12.0f, context.resources.displayMetrics)
        } else if (position % 3 == 2) {
            holder.binding!!.iv.setImageResource(R.drawable.partner3)
            params.leftMargin = Utils.dpToPx(12.0f, context.resources.displayMetrics)
        }
        holder.binding?.rl!!.layoutParams = params
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowEventTicketPartnersBinding? = null

        init {
            binding = DataBindingUtil.bind<RowEventTicketPartnersBinding>(itemView)

        }
    }

}