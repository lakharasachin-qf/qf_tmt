package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.PointHistoryList
import com.themarkettheory.user.ui.main.activity.PointDetailActivity

class PointHistoryListAdapter(val context: Context, val data: List<PointHistoryList>) :
    RecyclerView.Adapter<PointHistoryListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowPointsHistoryBinding =
            //  DataBindingUtil.inflate(layoutInflater, R.layout.row_points_history_new, p0, false)
            DataBindingUtil.inflate(layoutInflater, R.layout.row_points_history, p0, false)

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
        holder.binding!!.tvDate.setText(
            data[getItemViewType(position)].date + " at " + data[getItemViewType(
                position
            )].time
        )

        if (data[getItemViewType(position)].get_point == "earn") {
            holder.binding!!.iv.setImageResource(R.drawable.ic_earn_round)
        } else {
            holder.binding!!.iv.setImageResource(R.drawable.ic_spend_round)
        }
        holder.binding!!.rl.setOnClickListener {
            context.startActivity(
                Intent(context, PointDetailActivity::class.java)
                    .putExtra("orderId", data[getItemViewType(position)].orderId)
                    .putExtra("serviceId", data[getItemViewType(position)].serviceId)
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPointsHistoryBinding? = null

        //        internal var binding: RowPointsHistoryNewBinding? = null
        init {
            // binding = DataBindingUtil.bind<RowPointsHistoryNewBinding>(itemView)
            binding = DataBindingUtil.bind<RowPointsHistoryBinding>(itemView)

        }
    }

}