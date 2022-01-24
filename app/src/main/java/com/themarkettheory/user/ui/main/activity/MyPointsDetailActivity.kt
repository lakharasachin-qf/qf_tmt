package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.mypoints.NewMyPointData
import com.themarkettheory.user.newmodels.mypoints.ServiceDetails
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_points_details.*
import kotlinx.android.synthetic.main.activity_order_detail.*

class MyPointsDetailActivity : BaseActivity(), View.OnClickListener {
    /*View Model*/
    private lateinit var vendorDetailViewModel: VendorDetailViewModel
    private lateinit var newMyPointsData: NewMyPointData
    private lateinit var serviceDetail: ServiceDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_my_points_details)
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@MyPointsDetailActivity)) {
                super.onBackPressed()
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
                ivPointsDetailsToolbarBackArrow -> onBackPressed()
                btnPointsDetailsRedeemPoints -> {
                    addMenuCart()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        try {
            /*Initialize View Model*/
            vendorDetailViewModel =
                ViewModelProvider(this@MyPointsDetailActivity).get(VendorDetailViewModel::class.java)

            /*Initialize newMyPointsData*/
            newMyPointsData = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewPointsDataRow),
                NewMyPointData::class.java
            )

            /*Initialize Service Details*/
            serviceDetail = gson.fromJson(
                myRoomDatabase.daoConfig()
                    .selectConfigTableByField(Config.dbNewPointsDataRowServiceDetails),
                ServiceDetails::class.java
            )

            /*Set on click listener*/
            ivPointsDetailsToolbarBackArrow.setOnClickListener(this)
            btnPointsDetailsRedeemPoints.setOnClickListener(this)

            /*Vendor View Model Progress Bar*/
            vendorDetailViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@MyPointsDetailActivity)
                    } else {
                        Utils.hideProgress(this@MyPointsDetailActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            /*Add menu cart response*/
            vendorDetailViewModel.responseMenuAddCart.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        if (newMyPointsData.isRedeem == 1) {
                            btnPointsDetailsRedeemPoints.apply {
                                text = "Redeem Now"
                                setTextColor(
                                    ContextCompat.getColor(
                                        this@MyPointsDetailActivity,
                                        R.color.myRobinsEggBlue
                                    )
                                )
                                background =
                                    ContextCompat.getDrawable(
                                        this@MyPointsDetailActivity,
                                        R.drawable.bg_button_black_pearl
                                    )
                            }
                        } else {
                            btnPointsDetailsRedeemPoints.apply {
                                text = "Redeem Points"
                                setTextColor(
                                    ContextCompat.getColor(
                                        this@MyPointsDetailActivity,
                                        R.color.white
                                    )
                                )
                                background =
                                    ContextCompat.getDrawable(
                                        this@MyPointsDetailActivity,
                                        R.drawable.ic_button_bg
                                    )
                            }
                        }
                    }
                }
            })
            populatePointsDetail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populatePointsDetail() {
        try {
            /*Dish Image*/
            if (!newMyPointsData.image.isNullOrEmpty()) {
                Picasso.get().load(newMyPointsData.image)
                    .error(R.drawable.ic_camera)
                    .into(ivPointsDetailDishImage)
            } else {
                ivPointsDetailDishImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MyPointsDetailActivity,
                        R.drawable.ic_camera
                    )
                )
            }

            /*Restaurant Name*/
            tvPointsDetailRestaurantName.text = serviceDetail.title!!.trim()

            /*Dish Name*/
            tvPointsDetailDishName.text = newMyPointsData.title!!.trim()

            /*Dish Category*/
            tvPointsDetailDishCategory.text = newMyPointsData.categoryName!!.trim()

            /*Dish Points*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvPointsDetailDishPoints.setText(
                    Html.fromHtml(
                        getString(
                            R.string.points_text,
                            newMyPointsData.redeemPoints!!.toString().trim()
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                tvPointsDetailDishPoints.setText(
                    Html.fromHtml(
                        getString(
                            R.string.points_text,
                            newMyPointsData.redeemPoints!!.toString().trim()
                        )
                    )
                )
            }

            /*Dish isVeg*/
            ivPointsDetailIsVeg.setImageDrawable(
                when (newMyPointsData.foodType!!) {
                    1 -> ContextCompat.getDrawable(
                        this@MyPointsDetailActivity,
                        R.drawable.ic_veg_icon
                    )
                    2 -> ContextCompat.getDrawable(
                        this@MyPointsDetailActivity,
                        R.drawable.ic_non_veg_icon
                    )
                    else -> ContextCompat.getDrawable(
                        this@MyPointsDetailActivity,
                        R.drawable.food_type_egg
                    )
                }
            )

            /*Dish isSpicy*/
            ivPointsDetailDishSpicy.setImageDrawable(
                if (newMyPointsData.isSpicy == 1) ContextCompat.getDrawable(
                    this@MyPointsDetailActivity,
                    R.drawable.ic_spicy
                )
                else ContextCompat.getDrawable(this@MyPointsDetailActivity, R.drawable.ic_not_spicy)
            )

            /*Dish Ingredients*/
            tvPointsDetailDescription.text = newMyPointsData.ingredients!!.trim()

            /*Button Redeem Points*/
            if (newMyPointsData.isRedeem == 1) {
                btnPointsDetailsRedeemPoints.apply {
                    text = "Redeem Points"
                    setTextColor(
                        ContextCompat.getColor(
                            this@MyPointsDetailActivity,
                            R.color.white
                        )
                    )
                    background =
                        ContextCompat.getDrawable(
                            this@MyPointsDetailActivity,
                            R.drawable.ic_button_bg
                        )
                }
            } else {
                btnPointsDetailsRedeemPoints.apply {
                    text = "Redeem Now"
                    setTextColor(
                        ContextCompat.getColor(
                            this@MyPointsDetailActivity,
                            R.color.myRobinsEggBlue
                        )
                    )
                    background =
                        ContextCompat.getDrawable(
                            this@MyPointsDetailActivity,
                            R.drawable.bg_button_black_pearl
                        )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMenuCart() {
        try {
            if (PubFun.isInternetConnection(this@MyPointsDetailActivity)) {
                vendorDetailViewModel.menu_add_cart(
                    myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbServiceId),
                    newMyPointsData.id!!.toString().trim(),
                    "1",
                    if (newMyPointsData.isRedeem!! == 1) "0" else "1",
                    "0",
                    0,
                    0
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@MyPointsDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_points)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}