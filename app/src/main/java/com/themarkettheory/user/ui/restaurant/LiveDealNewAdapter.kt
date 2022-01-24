package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowLiveDealsNewBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.livedeals.LiveDealList

class LiveDealNewAdapter(
    private val context: Activity,
    private val listener: ListClickListener
) : RecyclerView.Adapter<LiveDealNewAdapter.Holder>() {
    //General
    private var liveDealList = ArrayList<LiveDealList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.row_live_deals_new,
                FrameLayout(parent.context),
                false
            )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val liveDealData = liveDealList[position]

            /*Dish Image*/
            if (liveDealData.menu!!.image.isNullOrEmpty()) {
                holder.rowLiveDealsNewBinding.ivRowLiveDealDishImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(liveDealData.menu!!.image!!.trim())
                    .error(R.drawable.ic_camera)
                    .into(holder.rowLiveDealsNewBinding.ivRowLiveDealDishImage)
            }

            /*Dish Name*/
            holder.rowLiveDealsNewBinding.tvRowLiveDealsDishName.text =
                liveDealData.menu!!.title!!.trim()

            /*Dish Ingredients*/
            holder.rowLiveDealsNewBinding.tvRowLiveDealsDescription.text =
                liveDealData.menu!!.ingredients!!.trim()

            /*Dish IsVegOrNonVeg*/
            holder.rowLiveDealsNewBinding.ivRowLiveDealsVegNonVeg.setImageDrawable(
                when (liveDealData.menu!!.foodType!!) {
                    1 -> ContextCompat.getDrawable(context, R.drawable.ic_veg_icon)
                    2 -> ContextCompat.getDrawable(context, R.drawable.ic_non_veg_icon)
                    else -> ContextCompat.getDrawable(context, R.drawable.food_type_egg)
                }
            )

            /*Dish IsSpicy*/
            holder.rowLiveDealsNewBinding.ivRowLiveDealsListSpicy.setImageDrawable(
                if (liveDealData.menu!!.isSpicy!! == 1) ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_spicy
                )
                else ContextCompat.getDrawable(context, R.drawable.ic_not_spicy)
            )

            /*Dish Final Price*/
            holder.rowLiveDealsNewBinding.tvRowLiveDealsFinalPrice.text =
                "${liveDealData.menu!!.currency!!} ${liveDealData.menu!!.finalPrice!!.trim()}"

            /*Add Button*/
            holder.rowLiveDealsNewBinding.tvLiveDealListAdd.apply {
                /*Set On Click Listener*/
                setOnClickListener {
                    listener.onClickListener(holder.view, position, liveDealData)
                }

                /*Design Properties*/
                when (liveDealData.menu!!.inCart!!) {
                    1 -> {
                        background = ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                        text = "Added"
                        setTextColor(
                            ContextCompat.getColor(context, R.color.white)
                        )
                    }
                    else -> {
                        background =
                            ContextCompat.getDrawable(context, R.drawable.bg_button_grey_border)
                        text = "+ add"
                        setTextColor(
                            ContextCompat.getColor(context, R.color.myMountainMeadow)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return liveDealList.size
    }

    fun addLiveDealData(alLiveDealList: ArrayList<LiveDealList>) {
        try {
            if (alLiveDealList.isNotEmpty()) {
                liveDealList.clear()
                liveDealList.addAll(alLiveDealList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun isInCartButton(position: Int, inCart: Int) {
        try {
            if (liveDealList.isNotEmpty()) {
                liveDealList[position].menu!!.inCart = inCart
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val rowLiveDealsNewBinding: RowLiveDealsNewBinding = DataBindingUtil.bind(itemView)!!
    }
}