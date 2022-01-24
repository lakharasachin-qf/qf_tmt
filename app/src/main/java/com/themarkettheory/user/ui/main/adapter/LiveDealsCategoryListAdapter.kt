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
import com.themarkettheory.user.model.LiveMenuList
import com.themarkettheory.user.model.Menu

class LiveDealsCategoryListAdapter(
    val context: Context,
    val data: ArrayList<LiveMenuList>,
    val listener: (Menu) -> Unit
) :
    RecyclerView.Adapter<LiveDealsCategoryListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowLiveDealCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_live_deal_category, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getLiveData(): ArrayList<LiveMenuList> {
        return data
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        val fixMealListAdapter = FixMealListAdapter(context, data[position].menu!!) { it, pos ->
            data[position].menu?.set(pos, it)
            listener(it)
        }
        holder.binding!!.rvCategory.adapter = fixMealListAdapter
        holder.binding!!.rvCategory.layoutManager = LinearLayoutManager(context)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowLiveDealCategoryBinding? = null

        init {
            binding = DataBindingUtil.bind<RowLiveDealCategoryBinding>(itemView)

        }
    }

}