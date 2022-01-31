package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMenuCategoryBinding
import com.themarkettheory.user.model.Menu
import com.themarkettheory.user.model.MenuList
import java.util.*

class RestaurantCategoryListAdapter(
    val context: Activity,
    val data: List<MenuList>,
    val listener: (Menu) -> Unit
) :
    RecyclerView.Adapter<RestaurantCategoryListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowMenuCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_menu_category, p0, false)

        return ViewHolder(binding.root)
    }

    fun getMenuData(): List<MenuList> {
        return data
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        data[getItemViewType(position)].menu?.mapIndexed { index, menu ->
            menu.name = data[getItemViewType(position)].name
        }
        val restaurantRecommendedListAdapter = RestaurantRecommendedListAdapter(
            context,
            data[getItemViewType(position)].menu!!
        ) { data1, pos ->
            data[position].menu!!.set(pos, data1)
            listener(data1)
        }
        holder.binding!!.rvMenuCategory.adapter = restaurantRecommendedListAdapter
        holder.binding!!.rvMenuCategory.layoutManager = LinearLayoutManager(context)
//        val restaurantQuickBitesAdapter = RestaurantQuickBitesAdapter(context,data[getItemViewType(position)])
//        holder.binding!!.rvMenuCategory.adapter = restaurantQuickBitesAdapter
//        holder.binding!!.rvMenuCategory.layoutManager = LinearLayoutManager(context)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowMenuCategoryBinding? = null

        init {
            binding = DataBindingUtil.bind<RowMenuCategoryBinding>(itemView)

        }
    }

}