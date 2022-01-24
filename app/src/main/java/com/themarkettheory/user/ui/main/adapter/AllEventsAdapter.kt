package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowUpcomingEventsBinding

/*import com.themarkettheory.user.model.UpcomingEvent*/


class AllEventsAdapter(val context: Activity/*val data:List<UpcomingEvent>,val listener : (UpcomingEvent) -> Unit*/) :
    RecyclerView.Adapter<AllEventsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowUpcomingEventsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_upcoming_events, p0, false)

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
        /*holder.binding?.obj = data[getItemViewType(position)]

        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)
        holder.binding!!.tvDesc.setText(Utils.formatDate(data[position].start!!,"yyyy-MM-dd HH:mm:ss","dd MMM, yyyy '-' hh:mm a") + data[position].tags)

        if(data[position].is_favourite == 1){
            holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
        }else{
            holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
        }

        holder.binding!!.ivFavorite.setOnClickListener {
            if(data[position].is_favourite == 1){
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
            }else{
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
            }
            data[position].is_favourite = data[position].is_favourite!!.xor(1)
            listener(data[getItemViewType(position)])
            notifyDataSetChanged()
        }*/
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowUpcomingEventsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowUpcomingEventsBinding>(itemView)

        }
    }

}