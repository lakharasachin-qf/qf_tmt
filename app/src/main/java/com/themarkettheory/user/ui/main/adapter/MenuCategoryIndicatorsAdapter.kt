package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMenuCategoryIndicatorBinding
import com.themarkettheory.user.model.MenuList
import kotlinx.android.synthetic.main.fragment_restaurant_menu.*
import java.util.*

class MenuCategoryIndicatorsAdapter(
    val context: Context,
    val data: List<MenuList>,
    val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<MenuCategoryIndicatorsAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowMenuCategoryIndicatorBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_menu_category_indicator, p0, false)

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
//        holder.binding?.obj = data[position]

        holder.binding!!.tvCategory.setText(data[getItemViewType(position)].name)
        holder.binding!!.tvItemcount.setText(data[getItemViewType(position)].menu!!.size.toString())

        if (currentPosition == position) {
            holder.binding!!.tvCategory.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding!!.tvDot.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding!!.tvItemcount.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        } else {
            holder.binding!!.tvCategory.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
            holder.binding!!.tvItemcount.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
            holder.binding!!.tvDot.setTextColor(ContextCompat.getColor(context, R.color.slate_gray))
        }

        holder.binding!!.rl.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
            listener(getItemViewType(position))
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowMenuCategoryIndicatorBinding? = null

        init {
            binding = DataBindingUtil.bind<RowMenuCategoryIndicatorBinding>(itemView)

        }
    }

}