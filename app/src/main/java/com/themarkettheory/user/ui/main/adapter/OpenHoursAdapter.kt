package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowOpeningHoursBinding
import com.themarkettheory.user.newmodels.overview.OpeningTime


class OpenHoursAdapter(val context: Activity, val data: List<OpeningTime>) :
    RecyclerView.Adapter<OpenHoursAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowOpeningHoursBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_opening_hours, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        holder.binding!!.tvTitle.setText(data[position].day)

        val timesAdapter = TimesAdapter(context, data[position].hours!!)
        holder.binding!!.rvTimes.adapter = timesAdapter
        holder.binding!!.rvTimes.layoutManager = LinearLayoutManager(context)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowOpeningHoursBinding? = null

        init {
            binding = DataBindingUtil.bind<RowOpeningHoursBinding>(itemView)

        }
    }

}