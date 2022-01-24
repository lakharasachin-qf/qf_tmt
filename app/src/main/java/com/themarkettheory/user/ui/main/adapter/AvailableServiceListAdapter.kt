package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowAvailableServicesBinding
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.SaloonSpaList
import java.util.*

class AvailableServiceListAdapter(
    val context: Activity,
    val list: List<SaloonSpaList>,
    val listener: () -> Unit
) :
    RecyclerView.Adapter<AvailableServiceListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay

    //    var spaImages = intArrayOf(R.drawable.ic_spa1,R.drawable.ic_spa2,R.drawable.ic_spa3,R.drawable.ic_spa4)
//    var salonImages = intArrayOf(R.drawable.ic_salon2,R.drawable.ic_salon3,R.drawable.ic_salon4)
    var currentposition = -1
    var flags = booleanArrayOf(false, false, false, false, false, false, false, false)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAvailableServicesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_available_services, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = list[position]
        var pos = getItemViewType(position)
        if (list[position].isSelected) {
            var pos = getItemViewType(position)
            holder.binding!!.iv.borderWidth = Utils.dpToPx(1.0f, context.resources.displayMetrics)
            holder.binding!!.iv.borderColor =
                ContextCompat.getColor(context, R.color.robins_egg_blue)
            holder.binding!!.tvService.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.robins_egg_blue
                )
            )
        } else {
            holder.binding!!.iv.borderWidth = Utils.dpToPx(0.0f, context.resources.displayMetrics)
            holder.binding!!.iv.borderColor =
                ContextCompat.getColor(context, android.R.color.transparent)
            holder.binding!!.tvService.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
        }

        Glide.with(context).load(list[getItemViewType(position)].image).into(holder.binding!!.iv)

        holder.binding!!.ll.setOnClickListener {
            list[position].isSelected = !list[position].isSelected
            notifyDataSetChanged()
            listener()
        }

    }

    fun getServiceData(): List<SaloonSpaList> {
        return list
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAvailableServicesBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAvailableServicesBinding>(itemView)

        }
    }

}