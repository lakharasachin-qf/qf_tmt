package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPlanBinding

class PlanListAdapter(val context: Context) :
    RecyclerView.Adapter<PlanListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flag = booleanArrayOf(false, false, false)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowPlanBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_plan, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        if (currentPosition == position) {
            holder.binding!!.ll.background =
                ContextCompat.getDrawable(context, R.drawable.ic_plan_selected_bg)
        } else {
            holder.binding!!.ll.background =
                ContextCompat.getDrawable(context, R.drawable.ic_plan_unselected_bg)
        }

        holder.binding!!.ll.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPlanBinding? = null

        init {
            binding = DataBindingUtil.bind<RowPlanBinding>(itemView)

        }
    }

}