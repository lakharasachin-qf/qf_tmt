package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowOpenDaysBinding
import com.themarkettheory.user.model.OpenDays


class OpenDaysAdapter(
    val context: Activity,
    val data: List<OpenDays>,
    val listener: (OpenDays) -> Unit
) :
    RecyclerView.Adapter<OpenDaysAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowOpenDaysBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_open_days, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = data[getItemViewType(position)]

        holder.binding!!.tvDay.setText(data[getItemViewType(position)].day)
        holder.binding!!.tvtime.setText(
            data[getItemViewType(position)].openTime + "-" + data[getItemViewType(
                position
            )].closeTime
        )
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowOpenDaysBinding? = null

        init {
            binding = DataBindingUtil.bind<RowOpenDaysBinding>(itemView)

        }
    }

}