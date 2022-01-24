package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyPointsBinding
import com.themarkettheory.user.model.MyPointsList

class MyPointListAdapter(
    val context: Context,
    var data: ArrayList<MyPointsList>, val listener: (MyPointsList) -> Unit
) :
    RecyclerView.Adapter<MyPointListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0

    //    var images = intArrayOf(R.drawable.ic_quick_bites1,R.drawable.ic_points2,R.drawable.ic_trending_offer,R.drawable.ic_restaurant3)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowMyPointsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_my_points, p0, false)

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
            if (data[getItemViewType(position)].is_redeem == 0) {
                holder.binding!!.btnRedeemNow.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.slate_gray
                    )
                )
                holder.binding!!.btnRedeemNow.setText(context.getString(R.string.earn_now))
                holder.binding!!.btnRedeemNow.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_redeem_now_bg)
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
        }

        if (data[getItemViewType(position)].isNonveg == 1) {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_non_veg_icon)
        } else {
            holder.binding!!.ivVeg.setImageResource(R.drawable.ic_veg_icon)
        }

        if (data[getItemViewType(position)].isSpicy == 1) {
            holder.binding!!.ivSpicy.visibility = View.VISIBLE
        } else {
            holder.binding!!.ivSpicy.visibility = View.GONE
        }

        holder.binding!!.btnRedeemNow.setOnClickListener {
            if (data[getItemViewType(position)].in_cart == 0) {

                data[getItemViewType(position)].in_cart = 1
                data[getItemViewType(position)].cart_qty =
                    data[getItemViewType(position)].cart_qty!! + 1
                /*if (data[getItemViewType(position)].is_redeem == 1) {
                    holder.binding!!.btnRedeemNow.background = ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                    holder.binding!!.btnRedeemNow.setTextColor(ContextCompat.getColor(context, R.color.white))
                    holder.binding!!.btnRedeemNow.setText(context.getString(R.string.added))
//                    data[getItemViewType(position)].is_redeem = data[getItemViewType(position)].is_redeem!!.xor(1)
                }*/
            } else {
                data[getItemViewType(position)].in_cart = 0
                data[getItemViewType(position)].cart_qty =
                    data[getItemViewType(position)].cart_qty!! - 1
                /*if (data[getItemViewType(position)].is_redeem == 1) {
                    holder.binding!!.btnRedeemNow.background = null
                    holder.binding!!.btnRedeemNow.setBackgroundColor(ContextCompat.getColor(context, R.color.robins_egg_blue))
                    holder.binding!!.btnRedeemNow.setTextColor(ContextCompat.getColor(context, R.color.white))
                    holder.binding!!.btnRedeemNow.setText(context.getString(R.string.added))
//                    data[getItemViewType(position)].is_redeem = data[getItemViewType(position)].is_redeem!!.xor(1)
                }*/
            }
            data[getItemViewType(position)].adapterPosition = getItemViewType(position)
            listener(data[getItemViewType(position)])
//            notifyDataSetChanged()
        }
    }

    fun updateData(tempData: List<MyPointsList>) {
        data = ArrayList()
        data.addAll(tempData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowMyPointsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowMyPointsBinding>(itemView)

        }
    }

}