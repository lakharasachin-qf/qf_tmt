package com.themarkettheory.user.ui.coupon

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.coupons.NewOfferListData
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_coupon_detail_new.*

class CouponDetailActivity : BaseActivity(), View.OnClickListener {
    /*View Model*/
    private lateinit var offerViewModel: OfferViewModel

    /*General*/
    private lateinit var newOfferListData: NewOfferListData
    private lateinit var viewBtnRedeem: View
    private lateinit var btnRedeemCoupon: AppCompatButton

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_detail_new)

        try {
            /*Initialize View Model*/
            offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

            /*Initialize View for Button Redeem Coupon*/
            viewBtnRedeem = findViewById(R.id.btnCouponDetailRedeemCoupon)
            btnRedeemCoupon = viewBtnRedeem.findViewById(R.id.btn)
            btnRedeemCoupon.text = "Redeem Coupon"

            /*Click Listener*/
            ivCouponDetailToolbarBack.setOnClickListener(this)
            ivCouponDetailToolbarFavorite.setOnClickListener(this)
            ivCouponDetailToolbarShare.setOnClickListener(this)
            btnRedeemCoupon.setOnClickListener(this)

            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@CouponDetailActivity)) {
                super.onBackPressed()
                if (Config.isCouponDetailOpeningFrom == Config.isCouponDetailOpeningFromAllFragment) {
                    Config.bottomBarClickedName = Config.homeBottomBarClicked
                }
                if (Config.isCouponDetailComingFromCouponActivity) {
                    Config.isCouponDetailComingFromCouponActivity = false
                    startActivity(Intent(this@CouponDetailActivity, CouponActivity::class.java))
                }
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivCouponDetailToolbarBack -> onBackPressed()
                ivCouponDetailToolbarFavorite -> callOfferFavoriteCoupon(
                    newOfferListData.id!!.toString().trim()
                )
                ivCouponDetailToolbarShare -> {
                    val shareContent =
                        "To avail this offer download The Market Theory " + Uri.parse(
                            "market://details?id=$packageName"
                        )
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        shareContent
                    )
                    startActivity(Intent.createChooser(share, "Share"))
                }
                btnRedeemCoupon -> {
                    /*Config.isCouponRedeem = true
                    Config.getSelectedCouponCode = newOfferListData.couponCode!!.trim()
                    Config.isCouponDiscountType = newOfferListData.discountType!!
                    Config.isCouponBuyQty = newOfferListData.buyQty!!
                    Config.isCouponGetQty = newOfferListData.getQty!!
                    Config.isCouponMenuId = newOfferListData.menuId!!
                    onBackPressed()*/

                    if (PubFun.isInternetConnection(this@CouponDetailActivity)) {
                        Config.isMenuFragmentComingFrom = ""
                        if (Config.isCouponOpeningFromOverview) {
                            Config.isCouponOpeningFromOverview = false
                        } else {
                            Config.isVendorComingFromCouponDetail = true
                            startActivity(
                                Intent(this@CouponDetailActivity, VendorDetailActivity::class.java)
                                    .putExtra(
                                        "category",
                                        newOfferListData.serviceDetails!!.categoryId.toString()
                                    )
                                    .putExtra(
                                        "serviceId",
                                        newOfferListData.serviceDetails!!.id.toString()
                                    )
                                    .putExtra("vendorTitle", newOfferListData.serviceDetails!!.title)
                            )
                        }
                        finish()
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            /*Get NewOfferListData from roomDb*/
            newOfferListData = gson.fromJson(
                myRoomDatabase
                    .daoConfig().selectConfigTableByField(Config.dbOfferListResRowData),
                NewOfferListData::class.java
            )

            /*Redeem Button Visibility*/
            /*viewBtnRedeem.visibility =
                if (Config.isCouponRedeemButtonVisible) View.VISIBLE else View.GONE*/

            //region Progress Bar Response
            offerViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@CouponDetailActivity)
                    } else {
                        Utils.hideProgress(this@CouponDetailActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Favorite API Response
            offerViewModel.responseFavoriteCoupon.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> {
                            ivCouponDetailToolbarFavorite.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@CouponDetailActivity,
                                    if (it.message!!.trim()
                                            .lowercase() == "added"
                                    ) R.drawable.ic_like_selected
                                    else R.drawable.ic_like_unselected
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            populateCouponDetail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callOfferFavoriteCoupon(offerId: String) {
        try {
            if (PubFun.isInternetConnection(this@CouponDetailActivity)) {
                offerViewModel.offerFavoriteCoupon(offerId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateCouponDetail() {
        try {
            /*Offer Image*/
            if (!newOfferListData.image.isNullOrEmpty()) {
                ivCouponDetailImage.scaleType = ImageView.ScaleType.CENTER_CROP
                if (newOfferListData.image.isNullOrEmpty()) {
                    ivCouponDetailImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@CouponDetailActivity,
                            R.drawable.ic_camera
                        )
                    )
                } else {
                    Picasso.get().load(newOfferListData.image!!.trim())
                        .error(R.drawable.ic_camera).into(ivCouponDetailImage)
                }
            } else {
                ivCouponDetailImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@CouponDetailActivity,
                        R.drawable.ic_camera
                    )
                )
                ivCouponDetailImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            /*Offer Favorite*/
            ivCouponDetailToolbarFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    this@CouponDetailActivity,
                    if (newOfferListData.isFavourite == 1) R.drawable.ic_like_selected
                    else R.drawable.ic_like_unselected
                )
            )

            /*Restaurant Title*/
            tvCouponDetailRestaurantTitle.text = newOfferListData.serviceDetails!!.title!!.trim()

            /*Restaurant Address*/
            tvCouponDetailRestaurantAddress.text =
                newOfferListData.serviceDetails!!.address!!.trim()

            /*Offer Title*/
            tvCouponDetailTitle.text = PubFun.getOfferTitle(
                this@CouponDetailActivity,
                newOfferListData.title!!.trim(),
                newOfferListData.discountAmount!!.toString().trim(),
                newOfferListData.buyQty!!.toString().trim()
            )

            /*Offer Description*/
            tvCouponDetailDescription.text = newOfferListData.description!!.trim()

            /*Offer Terms & Condition*/
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvCouponDetailTerms.text = Html.fromHtml(
                    newOfferListData.terms!!.trim(),
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                @Suppress("DEPRECATION")
                tvCouponDetailTerms.text = Html.fromHtml(
                    newOfferListData.terms!!.trim()
                )
            }*/
            /*Minimum Offer Value*/
            lylCouponDetailPoint1.visibility =
                if (newOfferListData.min_offer_val == 0) View.GONE else View.VISIBLE
            tvCouponDetailMinimumOrderValue.text = newOfferListData.min_offer_val.toString().trim()

            /*Maximum Discount*/
            lylCouponDetailPoint2.visibility =
                if (newOfferListData.maxDiscount == 0) View.GONE else View.VISIBLE
            tvCouponDetailMaximumOrderValue.text = newOfferListData.maxDiscount.toString().trim()

            /*Offer Valid from and to*/
            lylCouponDetailPoint3.visibility =
                if (newOfferListData.startTime!!.isEmpty() || newOfferListData.endTime!!.isEmpty())
                    View.GONE else View.VISIBLE
            if (newOfferListData.startTime!!.length > 10 && newOfferListData.endTime!!.length > 10) {
                val startYear = newOfferListData.startTime!!.substring(0, 4)
                val startMonth = newOfferListData.startTime!!.substring(5, 7)
                val startDay = newOfferListData.startTime!!.substring(8, 10)

                val endYear = newOfferListData.endTime!!.substring(0, 4)
                val endMonth = newOfferListData.endTime!!.substring(5, 7)
                val endDay = newOfferListData.endTime!!.substring(8, 10)

                tvCouponDetailOfferFrom.text =
                    "${PubFun.getReadableDate(startDay)} ${PubFun.getMonthInName(startMonth)}"
                tvCouponDetailPoint3_1.text = " $startYear to "
                tvCouponDetailOfferTo.text =
                    "${PubFun.getReadableDate(endDay)} ${PubFun.getMonthInName(endMonth)}"
                tvCouponDetailPoint3_2.text = " $endYear "
            }

            /*Offer Coupon Code*/
            tvCouponDetailCode.text = newOfferListData.couponCode!!.trim()

            /*Offer Expiry Duration*/
            tvCouponDetailExpiresDuration.text = newOfferListData.expireIn!!.trim()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@CouponDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Coupon Detail"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                            } else {
                                i--
                                postDelayed(this, 1000)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}