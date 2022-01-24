package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowStampBinding
import com.themarkettheory.user.model.StampList

class StampListAdapter(val context: Context, val data: StampList, val flag: Boolean) :
    RecyclerView.Adapter<StampListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowStampBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_stamp, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        if (data.actual_stamp == null) {
            return 0
        } else {
            return data.actual_stamp!!
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.width = (context.resources.displayMetrics.widthPixels - Utils.dpToPx(
            64.0f,
            context.resources.displayMetrics
        )) / 10
        params.leftMargin = Utils.dpToPx(12.0f, context.resources.displayMetrics)
        params.height = params.width
//        holder.binding?.rl!!.layoutParams = params

        holder.binding!!.tv.setText((position + 1).toString())

        if (position < data.orderCount!!) {
            holder.binding!!.tv.background =
                ContextCompat.getDrawable(context, R.drawable.ic_stamp_selected)
            holder.binding!!.tv.setText("")
        } else {
            holder.binding!!.tv.background =
                ContextCompat.getDrawable(context, R.drawable.ic_stamp_unselected)
            holder.binding!!.tv.setText((getItemViewType(position) + 1).toString())
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowStampBinding? = null

        init {
            binding = DataBindingUtil.bind<RowStampBinding>(itemView)

        }
    }


}