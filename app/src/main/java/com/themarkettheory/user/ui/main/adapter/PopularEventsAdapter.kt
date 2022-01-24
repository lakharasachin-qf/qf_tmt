package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPopularEventsBinding

/*import com.themarkettheory.user.model.UpcomingEvent*/

class PopularEventsAdapter(val context: Context /*val data:List<UpcomingEvent>,val listener : (UpcomingEvent) -> Unit*/) :
    RecyclerView.Adapter<PopularEventsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowPopularEventsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_popular_events, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        /*return data.size*/
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*holder.binding?.obj = data[position]

        holder.binding!!.tvDate.setText(
            Utils.formatDate(
                data[position].start!!,
                "yyyy-MM-dd HH:mm:ss",
                "dd MMM, yyyy '-' hh:mm a") +" | "+ data[position].event_type
        )
        var currency :String? = ""
        val min : Float? = data[getItemViewType(position)].prices?.minOfOrNull { it.price!!.toFloat() }
        data[getItemViewType(position)].prices?.map {
            currency = it.currency!!
        }
        if(min != null){
            holder.binding!!.tvPrice.setText(currency+min.toString())
        }
        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.ivPopularEvent)*/

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPopularEventsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowPopularEventsBinding>(itemView)

            /*binding!!.rl.setOnClickListener {
                listener(data[adapterPosition])
            }*/
        }
    }

}