package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowEventPassBinding

/*import com.themarkettheory.user.model.Price*/

class EventPassListAdapter(val context: Activity /*val data: List<Price>,val listener : (List<Price>) -> Unit*/) :
    RecyclerView.Adapter<EventPassListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowEventPassBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_event_pass, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        /*return data.size*/
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*holder.binding?.obj = data[position]

        if (position == data.size - 1) {
            holder.binding!!.view.visibility = View.GONE
        }

        holder.binding!!.ivMinus.setOnClickListener {
            val qty = holder.binding!!.tvQty.text.toString()
            if (qty != "0") {
                val q: Int = holder.binding!!.tvQty.text.toString().toInt().minus(1)
                holder.binding!!.tvQty.setText(q.toString())
                data[getItemViewType(position)].qty = q
            }
            listener(data)
        }

        holder.binding!!.ivPlus.setOnClickListener {
            val qty = holder.binding!!.tvQty.text.toString()
            if(qty.toInt() < data[getItemViewType(position)].allowPeople!!.toInt()) {
                val q: Int = holder.binding!!.tvQty.text.toString().toInt().plus(1)
                holder.binding!!.tvQty.setText(q.toString())
                data[getItemViewType(position)].qty = q
                listener(data)
            }else{
                Utils.showToast(context,"Sorry! you cannot buy more than "+data[getItemViewType(position)].allowPeople+" tickets for this event")
            }

        }*/
    }

    /* fun getPassData(): List<Price> {
         return data
     }*/

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowEventPassBinding? = null

        init {
            binding = DataBindingUtil.bind<RowEventPassBinding>(itemView)

        }
    }

}