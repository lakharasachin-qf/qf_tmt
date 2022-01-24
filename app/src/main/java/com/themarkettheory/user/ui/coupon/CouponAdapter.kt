package com.themarkettheory.user.ui.coupon

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowCouponNewBinding
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.coupons.NewOfferListData

class CouponAdapter(
    private val context: Context,
    private val listener: ListClickListener,
    private val favoriteListener: ListClickListener
) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {
    /*ArrayList*/
    private val offerList = ArrayList<NewOfferListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_coupon_new, FrameLayout(parent.context), false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val data = offerList[position]

            /*Offer Image*/
            if (!data.image.isNullOrEmpty()) {
                holder.binding.ivCouponList.scaleType = ImageView.ScaleType.CENTER_CROP
                if (data.image.isNullOrEmpty()) {
                    holder.binding.ivCouponList.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_camera
                        )
                    )
                } else {
                    Picasso.get().load(data.image!!.trim())
                        .error(R.drawable.ic_camera)
                        .into(holder.binding.ivCouponList)
                }
            } else {
                holder.binding.ivCouponList.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_camera
                    )
                )
                holder.binding.ivCouponList.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            /*Offer Title*/
            holder.binding.tvCouponListTitle.text = PubFun.getOfferTitle(
                context,
                data.title!!.toString().trim(),
                data.discountAmount!!.toString().trim(),
                data.buyQty!!.toString().trim()
            )

            /*Offer Description*/
            holder.binding.tvCouponListDescription.text = data.description!!.trim()

            /*Offer Expire Duration*/
            holder.binding.tvCouponListExpireDuration.text = data.expireIn!!.trim()

            /*Offer Max Value*/
            holder.binding.tvCouponListMaxOfferPrice.apply {
                visibility = if (data.maxDiscount!! == 0) View.INVISIBLE else View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text = Html.fromHtml(
                        context.getString(
                            R.string.coupon_max_offer_price,
                            data.maxDiscount!!.toString()
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    @Suppress("DEPRECATION")
                    text = Html.fromHtml(
                        context.getString(
                            R.string.coupon_max_offer_price,
                            data.maxDiscount!!.toString()
                        )
                    )
                }
            }

            /*Offer favorite*/
            holder.binding.ivCouponListFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    if (data.isFavourite == 1) R.drawable.ic_like_selected else
                        R.drawable.ic_favorite_unselected
                )
            )

            /*Offer favorite Click Listener*/
            holder.binding.ivCouponListFavorite.setOnClickListener {
                favoriteListener.onClickListener(holder.view, position, data)
            }

            /*View Click Listener*/
            holder.view.setOnClickListener {
                listener.onClickListener(holder.view, position, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    fun addOfferList(alOfferList: ArrayList<NewOfferListData>) {
        try {
            if (alOfferList.isNotEmpty()) {
                offerList.clear()
                offerList.addAll(alOfferList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun addFavoriteToCoupon(position: Int, isFavorite: Int) {
        try {
            if (offerList.isNotEmpty()) {
                offerList[position].isFavourite = isFavorite
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val binding = DataBindingUtil.bind<RowCouponNewBinding>(itemView)!!
    }
}