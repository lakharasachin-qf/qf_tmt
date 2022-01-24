package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPointsHistoryNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.newmodels.pointhistory.NewPointHistoryData

class NewPointHistoryListAdapter : RecyclerView.Adapter<NewPointHistoryListAdapter.ViewHolder>() {

    private var totalHistoryPointList = ArrayList<NewPointHistoryData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_points_history_new, FrameLayout(parent.context), false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val totalHistoryPointData = totalHistoryPointList[position]

            // setting up inword and downword arrow based on earn and spend
            if (totalHistoryPointData.getPoint!!.toString().trim().lowercase() == "earn") {
                holder.bindingRowPointsHistoryNewBinding.iv.setImageResource(R.drawable.ic_new_earn_round)
            } else {
                holder.bindingRowPointsHistoryNewBinding.iv.setImageResource(R.drawable.ic_spend_round)
            }
            // setting up the dish name and qty
            if (totalHistoryPointData.dishQty.isNullOrEmpty() || totalHistoryPointData.unit.isNullOrEmpty()) {
                holder.bindingRowPointsHistoryNewBinding.tvItemName.text =
                    totalHistoryPointData.title!!.trim()
            } else {
                holder.bindingRowPointsHistoryNewBinding.tvItemName.text =
                    "${totalHistoryPointData.title!!} (${totalHistoryPointData.dishQty!!} ${totalHistoryPointData.unit!!})"
            }

            //setting up the date and time
            val date: String = PubFun.parseDate(
                totalHistoryPointData.date!!,
                Config.requestDateFormat,
                Config.defaultDateFormat
            ).toString()
            val time: String = PubFun.parseDate(
                totalHistoryPointData.time!!,
                Config.requestTimeFormat,
                Config.defaultTimeFormat
            ).toString()
            holder.bindingRowPointsHistoryNewBinding.tvDate.text =
                "${date} at ${time.toString().uppercase()}"

            // setting up the price
            holder.bindingRowPointsHistoryNewBinding.tvTotalPrice.text =
                "${totalHistoryPointData.currency}${
                    totalHistoryPointData.finalPrice!!.toString().trim()
                }"

            // setting up the points
            if (totalHistoryPointData.getPoint!!.toString().lowercase() == "spend") {
                holder.bindingRowPointsHistoryNewBinding.tvPoints.text =
                    totalHistoryPointData.redeemPoints!!.toString().trim()
                holder.bindingRowPointsHistoryNewBinding.tvPoints.setTextColor(Color.parseColor("#EF4040"))
            } else {
                holder.bindingRowPointsHistoryNewBinding.tvPoints.text =
                    totalHistoryPointData.point!!.toString().trim()
                holder.bindingRowPointsHistoryNewBinding.tvPoints.setTextColor(Color.parseColor("#1CBD54"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun addTotalPointList(alPoints: ArrayList<NewPointHistoryData>) {
        try {
            if (alPoints.isNotEmpty()) {
                this.totalHistoryPointList.clear()
                this.totalHistoryPointList.addAll(alPoints)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return totalHistoryPointList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val view = itemView
        val bindingRowPointsHistoryNewBinding: RowPointsHistoryNewBinding =
            DataBindingUtil.bind(itemView)!!
    }
}