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
import com.themarkettheory.user.databinding.RowRestaurantPointDetailBinding
import com.themarkettheory.user.model.TotalPointDetailList


class RestaurantPointDetailListAdapter(
    val context: Activity,
    var data: ArrayList<TotalPointDetailList>, val listener: (TotalPointDetailList) -> Unit
) :
    RecyclerView.Adapter<RestaurantPointDetailListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowRestaurantPointDetailBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_restaurant_point_detail, p0, false)

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

        Glide.with(context).load(data[getItemViewType(position)].image)
            .error(R.drawable.ic_camera)
            .into(holder.binding!!.iv)

        if (data[getItemViewType(position)].in_cart == 1) {
            holder.binding!!.btnRedeemNow.background =
                ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
            holder.binding!!.btnRedeemNow.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            holder.binding!!.btnRedeemNow.setText(context.getString(R.string.added))
        } else {
            holder.binding!!.btnRedeemNow.setText(context.getString(R.string.redeem_now))
            holder.binding!!.btnRedeemNow.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.robins_egg_blue
                )
            )
            holder.binding!!.btnRedeemNow.background =
                ContextCompat.getDrawable(context, R.drawable.ic_redeem_now_bg)
        }
        holder.binding!!.btnRedeemNow.setOnClickListener {
            data[getItemViewType(position)].in_cart =
                data[getItemViewType(position)].in_cart!!.xor(1)
            data[getItemViewType(position)].adapterPosition = getItemViewType(position)
            listener(data[getItemViewType(position)])
//            notifyDataSetChanged()
        }
    }

    fun updateData(tempData: List<TotalPointDetailList>) {
        data = ArrayList()
        data.addAll(tempData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowRestaurantPointDetailBinding? = null

        init {
            binding = DataBindingUtil.bind<RowRestaurantPointDetailBinding>(itemView)

        }
    }

}