package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMyPointsNewBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.mypoints.NewMyPointData

class NewMyPointListAdapter(
    private val context: Context,
    private val redeemClickListener: ListClickListener,
    private val listener: ListClickListener
) :
    RecyclerView.Adapter<NewMyPointListAdapter.ViewHolder>() {
    private var totalMyPointsDataList = ArrayList<NewMyPointData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_my_points_new, FrameLayout(parent.context), false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val totalMyPointData = totalMyPointsDataList[position]

            // setting up the images
            if (totalMyPointData.image.isNullOrEmpty()) {
                holder.bindingRow.ivMyPointsImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(totalMyPointData.image)
                    .error(R.drawable.ic_camera)
                    .into(holder.bindingRow.ivMyPointsImage)
            }
            // setting up the title of the dish
            holder.bindingRow.tvMyPointsItemName.text = totalMyPointData.title!!.toString().trim()

            // setting up the category
            holder.bindingRow.tvMyPointsCategory.text =
                totalMyPointData.categoryName!!.toString().trim()

            //setting up the veg nogveg icon
            holder.bindingRow.ivMyPointsVegNonVeg.setImageDrawable(
                when (totalMyPointData.foodType!!) {
                    1 -> ContextCompat.getDrawable(context, R.drawable.ic_veg_icon)
                    2 -> ContextCompat.getDrawable(context, R.drawable.ic_non_veg_icon)
                    else -> ContextCompat.getDrawable(context, R.drawable.food_type_egg)
                }
            )
            //Spicy or Non-Spicy
            holder.bindingRow.ivMyPointsSpicy.setImageDrawable(
                if (totalMyPointData.isSpicy == 1) ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_spicy
                )
                else ContextCompat.getDrawable(context, R.drawable.ic_not_spicy)
            )

            // setting up the points
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.bindingRow.tvMyPointsText.setText(
                    Html.fromHtml(
                        context.getString(
                            R.string.points_text,
                            totalMyPointData.redeemPoints!!.toString().trim()
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                holder.bindingRow.tvMyPointsText.setText(
                    Html.fromHtml(
                        context.getString(
                            R.string.points_text,
                            totalMyPointData.redeemPoints!!.toString().trim()
                        )
                    )
                )
            }

            // setting up the buton color
            if (totalMyPointData.isRedeem == 1) {
                holder.bindingRow.tvMyPointsRedeemPoints.apply {
                    text = "Redeemed"
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    background =
                        ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                }
            } else {
                holder.bindingRow.tvMyPointsRedeemPoints.apply {
                    text = "Redeem Now"
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.myRobinsEggBlue
                        )
                    )
                    background =
                        ContextCompat.getDrawable(context, R.drawable.bg_button_black_pearl)
                }
            }

            // setting up the onclick listener on button redeem
            holder.bindingRow.tvMyPointsRedeemPoints.setOnClickListener {
                try {
                    redeemClickListener.onClickListener(holder.view, position, totalMyPointData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            /*View Click Listener*/
            holder.view.setOnClickListener {
                listener.onClickListener(holder.view, position, totalMyPointData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return totalMyPointsDataList.size
    }

    fun addTotalPointList(alPoints: ArrayList<NewMyPointData>) {
        try {
            if (alPoints.isNotEmpty()) {
                this.totalMyPointsDataList.clear()
                this.totalMyPointsDataList.addAll(alPoints)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun isRedeemButton(position: Int, isRedeem: Int) {
        try {
            if (totalMyPointsDataList.isNotEmpty()) {
                totalMyPointsDataList[position].isRedeem = isRedeem
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val bindingRow: RowMyPointsNewBinding = DataBindingUtil.bind(itemView)!!
    }
}