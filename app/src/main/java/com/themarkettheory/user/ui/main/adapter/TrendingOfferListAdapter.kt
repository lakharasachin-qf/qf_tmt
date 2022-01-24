package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.databinding.RowCouponNewBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.coupons.NewOfferListData
import com.themarkettheory.user.newmodels.home.Offer
import com.themarkettheory.user.ui.coupon.CouponDetailActivity


class TrendingOfferListAdapter(
    val context: Activity,
    val data: List<Offer>,
    val flag: Boolean,
    private val rowListener: ListClickListener,
    private val favoriteListener: ListClickListener,
    private val isOpeningFrom: String,
    private val serviceId: Int,
    private val serviceTitle: String,
    private val serviceAddress: String,
    val listener: (Offer) -> Unit
) :
    RecyclerView.Adapter<TrendingOfferListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowCouponNewBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_coupon_new, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val offerData = data[position]

            /*Offer Image*/
            if (offerData.image.isNullOrEmpty()) {
                holder.binding!!.ivCouponList.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(offerData.image!!.trim())
                    .error(R.drawable.ic_camera)
                    .into(holder.binding!!.ivCouponList)
            }
            /*Offer Title*/
            holder.binding!!.tvCouponListTitle.text = PubFun.getOfferTitle(
                context,
                offerData.title!!.trim(),
                offerData.discountAmount!!.toString().trim(),
                offerData.buyQty!!.toString().trim()
            )
            /*Offer Description*/
            holder.binding!!.tvCouponListDescription.text = offerData.description!!.trim()
            /*Offer Expiry Duration*/
            holder.binding!!.tvCouponListExpireDuration.text = offerData.expireIn!!.trim()
            /*Offer Max Value*/
            holder.binding!!.tvCouponListMaxOfferPrice.apply {
                visibility = if (offerData.maxDiscount!! == 0) View.INVISIBLE else View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text = Html.fromHtml(
                        context.getString(
                            R.string.coupon_max_offer_price,
                            offerData.maxDiscount!!.toString()
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    @Suppress("DEPRECATION")
                    text = Html.fromHtml(
                        context.getString(
                            R.string.coupon_max_offer_price,
                            offerData.maxDiscount!!.toString()
                        )
                    )
                }
            }
            //region Offer Favorite
            holder.binding!!.ivCouponListFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    if (offerData.isFavourite!! == 1) R.drawable.ic_like_selected else
                        R.drawable.ic_favorite_unselected
                )
            )
            holder.binding!!.ivCouponListFavorite.setOnClickListener {
                favoriteListener.onClickListener(holder.itemView, position, offerData)
            }
            //endregion

            /*Offer View Click*/
            holder.view.setOnClickListener {
                Config.isCouponDetailOpeningFrom = isOpeningFrom

                val newOfferListData = NewOfferListData()
                /*Offer id*/
                newOfferListData.id = offerData.id!!
                /*Image*/
                newOfferListData.image = offerData.image!!.trim()
                /*Favorite*/
                newOfferListData.isFavourite = offerData.isFavourite
                /*Restaurant Id*/
                newOfferListData.serviceDetails!!.id =
                    if (isOpeningFrom == Config.isCouponDetailOpeningFromOverview) serviceId
                    else offerData.serviceDetails!!.id!!
                /*Restaurant Title*/
                newOfferListData.serviceDetails!!.title =
                    if (isOpeningFrom == Config.isCouponDetailOpeningFromOverview) serviceTitle
                    else offerData.serviceDetails!!.title!!.trim()
                /*Restaurant Address*/
                newOfferListData.serviceDetails!!.address =
                    if (isOpeningFrom == Config.isCouponDetailOpeningFromOverview) serviceAddress
                    else offerData.serviceDetails!!.address!!.trim()
                /*Restaurant Category Id*/
                newOfferListData.serviceDetails!!.categoryId =
                    offerData.serviceDetails!!.categoryId!!
                /*Offer Title*/
                newOfferListData.title = offerData.title!!.trim()
                /*Offer Discount amount*/
                newOfferListData.discountAmount = offerData.discountAmount!!
                /*Offer Buy Qty*/
                newOfferListData.buyQty = offerData.buyQty!!
                /*Offer Description*/
                newOfferListData.description = offerData.description!!.trim()
                /*Offer Terms & Condition*/
                newOfferListData.terms = offerData.terms!!.trim()
                /*Offer Coupon Code*/
                newOfferListData.couponCode = offerData.couponCode!!.trim()
                /*Offer Expiry Duration*/
                newOfferListData.expireIn = offerData.expireIn!!.trim()
                /*Offer Minimum Value*/
                newOfferListData.min_offer_val = offerData.min_offer_val!!
                /*Offer Maximum Discount*/
                newOfferListData.maxDiscount = offerData.maxDiscount!!
                /*Offer Start Time*/
                newOfferListData.startTime = offerData.startTime!!.trim()
                /*Offer End Time*/
                newOfferListData.endTime = offerData.endTime!!.trim()

                val myRoomDatabase = MyRoomDatabase.getDB(context)!!
                val gson = GsonBuilder().serializeNulls().create()

                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbOfferListResRowData)
                    insertConfigTable(
                        TableConfig(
                            Config.dbOfferListResRowData,
                            gson.toJson(newOfferListData)
                        )
                    )
                }

                Config.isCouponOpeningFromOverview =
                    isOpeningFrom == Config.isCouponDetailOpeningFromOverview
                /*context.startActivity(
                    Intent(context, CouponDetailActivity::class.java)
                )*/
                rowListener.onClickListener(holder.view, position, offerData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFavoriteMark(position: Int, isFavorite: Int) {
        data[position].isFavourite = isFavorite
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowCouponNewBinding? = null
        val view = itemView

        init {
            binding = DataBindingUtil.bind<RowCouponNewBinding>(itemView)

        }
    }

}