package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.graphics.Color
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowTotalPointsBinding
import com.themarkettheory.user.model.TotalPointsData
import com.themarkettheory.user.model.TotalPointsList

class TotalPointsListAdapter(
    val context: Activity,
    val data: List<TotalPointsData.TotalPointsList>,
    val listener: (TotalPointsData.TotalPointsList) -> Unit
) :
    RecyclerView.Adapter<TotalPointsListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    var colors = arrayOf("#2fb8ee", "#ffbe00", "#19b254", "#8230ff", "#da532f", "#2fb8ee")


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowTotalPointsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_total_points, p0, false)

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
        if (position != 0) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.rightMargin = Utils.dpToPx(16.0f, context.resources.displayMetrics)
            params.leftMargin = Utils.dpToPx(16.0f, context.resources.displayMetrics)
            params.topMargin = Utils.dpToPx(-5.0f, context.resources.displayMetrics)
            holder.binding?.llParent!!.layoutParams = params
        }
        holder.binding?.rl!!.background =
            ContextCompat.getDrawable(context, R.drawable.ic_solid_rounded_r7)
        holder.binding!!.tvTotalPoints.setText(
            data[getItemViewType(position)].points.toString() + "\n" + context.getString(
                R.string.points
            )
        )

        val pos = getItemViewType(position) % 6
        holder.binding?.rl!!.background.setColorFilter(
            Color.parseColor(colors[pos]),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )

        holder.binding!!.rl.setOnClickListener {
            listener(data[position])
        }
        holder.binding!!.iv.setOnClickListener {
            listener(data[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowTotalPointsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowTotalPointsBinding>(itemView)
        }
    }

}