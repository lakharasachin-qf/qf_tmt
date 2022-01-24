package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowCollectStampsBinding
import com.themarkettheory.user.model.StampList

class CollectStampListAdapter(val context: Context, val data: List<StampList>) :
    RecyclerView.Adapter<CollectStampListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowCollectStampsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_collect_stamps, p0, false)

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

        val stampListAdapter = StampListAdapter(context, data[getItemViewType(position)], true)
        holder.binding!!.rvStamps.adapter = stampListAdapter
        holder.binding!!.rvStamps.layoutManager = GridLayoutManager(context, 10)

        /*val stampListAdapter2 = StampListAdapter(context,false)
        holder.binding!!.rvStamps2.adapter = stampListAdapter2
        holder.binding!!.rvStamps2.layoutManager = GridLayoutManager(context,  10)*/

        /*holder.binding!!.ivInfo.setOnClickListener {
            context.startActivity(Intent(context,PointDetailActivity::class.java)
                .putExtra("orderId",""))
        }*/
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowCollectStampsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowCollectStampsBinding>(itemView)

        }
    }


}