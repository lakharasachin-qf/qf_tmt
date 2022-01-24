package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.model.NotificationOfferList
import com.themarkettheory.user.newmodels.coupons.NewOfferListData
import com.themarkettheory.user.ui.coupon.CouponDetailActivity

class NotificationCouponCodesAdapter(
    val context: Context,
    val data: ArrayList<NotificationOfferList>,
    val isOpeningFrom: String,
    val serviceId: Int,
    val serviceTitle: String,
    val serviceAddress: String,
) :
    RecyclerView.Adapter<NotificationCouponCodesAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowNotificationCouponCodeBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.row_notification_coupon_code,
                p0,
                false
            )

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.binding?.obj = data[position]
        Glide.with(context).load(data[position].image).into(holder.binding!!.iv)
        //holder.binding!!.title.text = data[position].title

        /*Offer Title*/
        holder.binding!!.title.text = PubFun.getOfferTitle(
            context,
            data[position].title!!.trim(),
            data[position].discountAmount!!.toString().trim(),
            data[position].buyQty!!.toString().trim()
        )

        holder.binding!!.description.text = data[position].description
        holder.binding!!.expiredDays.text = data[position].expireIn.toString()
        holder.binding!!.nearBy.text = data[position].distance

        holder.binding!!.rl.setOnClickListener {
            Config.isCouponDetailOpeningFrom = isOpeningFrom

            val newOfferListData = NewOfferListData()
            /*Offer id*/
            newOfferListData.id = data[position].id!!
            /*Image*/
            newOfferListData.image = data[position].image!!.trim()
            /*Favorite*/
            newOfferListData.isFavourite = data[position].isFavourite
            /*Restaurant Id*/
            newOfferListData.serviceDetails!!.id = data[position].serviceDetails!!.id!!
            /*Restaurant Title*/
            newOfferListData.serviceDetails!!.title =
                data[position].serviceDetails!!.title!!.trim()
            /*Restaurant Address*/
            newOfferListData.serviceDetails!!.address =
                data[position].serviceDetails!!.address!!.trim()
            /*Restaurant Category Id*/
            newOfferListData.serviceDetails!!.categoryId =
                data[position].serviceDetails!!.categoryId!!
            /*Offer Title*/
            newOfferListData.title = data[position].title!!.trim()
            /*Offer Discount amount*/
            newOfferListData.discountAmount = data[position].discountAmount!!
            /*Offer Buy Qty*/
            newOfferListData.buyQty = data[position].buyQty!!
            /*Offer Description*/
            newOfferListData.description = data[position].description!!.trim()
            /*Offer Terms & Condition*/
            newOfferListData.terms = data[position].terms!!.trim()
            /*Offer Coupon Code*/
            newOfferListData.couponCode = data[position].couponCode!!.trim()
            /*Offer Expiry Duration*/
            newOfferListData.expireIn = data[position].expireIn!!.trim()
            /*Offer Minimum Value*/
            newOfferListData.min_offer_val = data[position].min_offer_val!!
            /*Offer Maximum Discount*/
            newOfferListData.maxDiscount = data[position].maxDiscount!!
            /*Offer Start Time*/
            newOfferListData.startTime = data[position].startTime!!.trim()
            /*Offer End Time*/
            newOfferListData.endTime = data[position].endTime!!.trim()

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
            context.startActivity(
                Intent(context, CouponDetailActivity::class.java)
                    .putExtra("data", data[position].offerFor)
            )
        }

//        holder.binding!!.rl.setOnClickListener {
//            context.startActivity(
//                Intent(context, CouponDetailActivity::class.java)
//                    .putExtra("data", data[position].offer)
//            )
//        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNotificationCouponCodeBinding? = null

        init {
            binding = DataBindingUtil.bind<RowNotificationCouponCodeBinding>(itemView)

        }
    }

}