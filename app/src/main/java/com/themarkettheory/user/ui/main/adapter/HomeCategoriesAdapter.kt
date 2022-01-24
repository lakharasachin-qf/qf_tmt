package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowHomeCategoryBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.newmodels.getcategories.Category


class HomeCategoriesAdapter(
    val context: Activity,
    val data: List<Category>,
    val listener: (Category) -> Unit
) :
    RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder>() {
    var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowHomeCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_home_category, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        if (currentPosition == position) {
            holder.binding!!.rl.background =
                ContextCompat.getDrawable(context, R.drawable.ic_menu_selected_bg)
            holder.binding!!.tv.setTextColor(ContextCompat.getColor(context, R.color.myWhite))
            holder.binding!!.iv.setColorFilter(ContextCompat.getColor(context, R.color.myWhite))
        } else {
            holder.binding!!.rl.background =
                ContextCompat.getDrawable(context, R.drawable.ic_menu_bg)
            holder.binding!!.tv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.robins_egg_blue
                )
            )
            holder.binding!!.iv.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.robins_egg_blue
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }
        Glide.with(context).load(data[getItemViewType(position)].icon)
            .error(R.drawable.ic_camera)
            .into(holder.binding!!.iv)
        holder.binding!!.tv.text = data[getItemViewType(position)].name

        holder.binding!!.ll.setOnClickListener {
            Config.foodCategoryPreviousPosition = currentPosition
            currentPosition = position
            data[getItemViewType(position)].currentPosition = position
            listener(data[getItemViewType(position)])
            notifyDataSetChanged()
        }
    }

    fun setPosition(pos: Int) {
        currentPosition = pos
        notifyDataSetChanged()
    }

    fun setSelectionAndCallClickListener(position: Int) {
        currentPosition = position
        data[getItemViewType(position)].currentPosition = position
        listener(data[getItemViewType(position)])
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowHomeCategoryBinding? = null

        init {
            binding = DataBindingUtil.bind<RowHomeCategoryBinding>(itemView)
        }
    }

}