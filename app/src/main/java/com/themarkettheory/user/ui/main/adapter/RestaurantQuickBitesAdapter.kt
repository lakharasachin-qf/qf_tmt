package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.MenuList

class RestaurantQuickBitesAdapter(val context: Context, val data: MenuList) :
    RecyclerView.Adapter<RestaurantQuickBitesAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flag = booleanArrayOf(false, false)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowQuickBitesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_quick_bites, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data
//        holder.binding!!.ivRestaurant.setImageResource(images[position])
        //Glide.with(context).load(data.image).into(holder.binding!!.iv)

        val quickBiteItemsAdapter = RestaurantQuickBiteItemsAdapter(context)
        holder.binding!!.rvQuickBitesItems.adapter = quickBiteItemsAdapter
        holder.binding!!.rvQuickBitesItems.layoutManager = LinearLayoutManager(context)

        /*if(!data[getItemViewType(position)].isSelected!!){
            holder.binding!!.rvQuickBitesItems.visibility = View.VISIBLE
            holder.binding!!.view.visibility = View.VISIBLE
        }else{
            holder.binding!!.rvQuickBitesItems.visibility = View.GONE
            holder.binding!!.view.visibility = View.GONE
        }*/

        holder.binding!!.iv.setOnClickListener {
            /* if(!data[getItemViewType(position)].isSelected!!){
                 holder.binding!!.rvQuickBitesItems.visibility = View.VISIBLE
                 holder.binding!!.view.visibility = View.VISIBLE
             }else{
                 holder.binding!!.rvQuickBitesItems.visibility = View.GONE
                 holder.binding!!.view.visibility = View.GONE
             }
             data[getItemViewType(position)].isSelected = !data[getItemViewType(position)].isSelected!!*/
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowQuickBitesBinding? = null

        init {
            binding = DataBindingUtil.bind<RowQuickBitesBinding>(itemView)

        }
    }

}