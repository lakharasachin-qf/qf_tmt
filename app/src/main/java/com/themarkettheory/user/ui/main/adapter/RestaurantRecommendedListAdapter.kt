package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowRestaurantRecommendedListBinding
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.Menu
import java.util.*

class RestaurantRecommendedListAdapter(
    val context: Activity,
    val data: List<Menu>,
    val listener: (Menu, Int) -> Unit
) :
    RecyclerView.Adapter<RestaurantRecommendedListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowRestaurantRecommendedListBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.row_restaurant_recommended_list,
                p0,
                false
            )

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]
        Glide.with(context).load(data[getItemViewType(position)].image)
            .error(R.drawable.ic_camera)
            .into(holder.binding!!.ivMenu)

        if (data[getItemViewType(position)].discountedPrice.isNullOrEmpty() || data[getItemViewType(
                position
            )].discountedPrice!!.toFloat() == data[getItemViewType(position)].price!!.toFloat()
        ) {
            holder.binding!!.rlTotalPrice.visibility = View.GONE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].price
            )
        } else {
            holder.binding!!.rlTotalPrice.visibility = View.VISIBLE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].discountedPrice
            )
            holder.binding!!.tvPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].price
            )
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

        if (data[getItemViewType(position)].in_cart!! == 1) {
            data[getItemViewType(position)].isSelected = true
            holder.binding!!.tvAdd.background =
                ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
            holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding!!.tvAdd.setText(context.getString(R.string.added))
        } else {
            holder.binding!!.tvAdd.background =
                ContextCompat.getDrawable(context, R.drawable.ic_border_rounded_r3_5)
            holder.binding!!.tvAdd.setTextColor(ContextCompat.getColor(context, R.color.malachite))
            holder.binding!!.tvAdd.setText(context.getString(R.string.add))
        }

        if (data[getItemViewType(position)].preparing_time.isNullOrEmpty()) {
            holder.binding!!.tvPreparingTime.visibility = View.GONE
        } else {
            holder.binding!!.tvPreparingTime.visibility = View.VISIBLE
        }

        if (data[getItemViewType(position)].only_in_restro == 0) {
            holder.binding!!.tvDineInOnly.visibility = View.GONE
            holder.binding!!.tvAdd.visibility = View.VISIBLE
        } else {
            holder.binding!!.tvDineInOnly.visibility = View.VISIBLE
            holder.binding!!.tvAdd.visibility = View.INVISIBLE
        }

        holder.binding!!.tvAdd.setOnClickListener {
            if (data[getItemViewType(position)].only_in_restro == 0) {
                if (data[getItemViewType(position)].in_cart == 0) {
                    holder.binding!!.tvAdd.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                    holder.binding!!.tvAdd.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    holder.binding!!.tvAdd.setText(context.getString(R.string.added))
                } else {
                    holder.binding!!.tvAdd.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_border_rounded_r3_5)
                    holder.binding!!.tvAdd.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.malachite
                        )
                    )
                    holder.binding!!.tvAdd.setText(context.getString(R.string.add))
                }
                data[getItemViewType(position)].isSelected =
                    !data[getItemViewType(position)].isSelected!!
                data[getItemViewType(position)].in_cart =
                    data[getItemViewType(position)].in_cart!!.xor(1)
                listener(data[getItemViewType(position)], getItemViewType(position))
            } else {
                Utils.showToast(context, "Sorry!, You cann't add this product")
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowRestaurantRecommendedListBinding? = null

        init {
            binding = DataBindingUtil.bind<RowRestaurantRecommendedListBinding>(itemView)


        }
    }

}