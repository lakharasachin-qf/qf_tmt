package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*

class MenuCategoryListAdapter(val context: Context) :
    RecyclerView.Adapter<MenuCategoryListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowFixMealsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_fix_meals, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowFixMealsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowFixMealsBinding>(itemView)

        }
    }

}