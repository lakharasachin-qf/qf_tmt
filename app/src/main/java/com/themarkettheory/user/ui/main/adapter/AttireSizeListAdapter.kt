package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.Size
import java.util.*


class AttireSizeListAdapter(
    val context: Activity,
    val data: List<Size>,
    val listener: (Size) -> Unit
) :
    RecyclerView.Adapter<AttireSizeListAdapter.ViewHolder>() {
    private var currentPosition: Int = -1
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    var sizes = arrayOf("M", "L", "XL", "XXL")
    var flag = booleanArrayOf(false, false, false, false)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAttireSizesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_attire_sizes, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        holder.binding!!.tvSize.setText(data[getItemViewType(position)].name)

        if (currentPosition == position) {
            holder.binding!!.tvSize.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_selected)
            holder.binding!!.tvSize.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding!!.tvSize.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_unselected)
            holder.binding!!.tvSize.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
        }

        holder.binding!!.rl.setOnClickListener {
            currentPosition = position
            listener(data[getItemViewType(position)])
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAttireSizesBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAttireSizesBinding>(itemView)

        }
    }

}