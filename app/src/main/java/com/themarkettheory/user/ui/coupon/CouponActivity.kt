package com.themarkettheory.user.ui.coupon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.coupons.NewOfferListData
import com.themarkettheory.user.newmodels.coupons.NewOfferListRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_coupon.*
import kotlinx.android.synthetic.main.activity_restaurant_list.*
import java.util.*
import kotlin.collections.ArrayList

class CouponActivity : BaseActivity(), View.OnClickListener {
    /*View Model*/
    private lateinit var offerViewModel: OfferViewModel
    private lateinit var offerListRes: NewOfferListRes

    /*General*/
    private var serviceId = "0"
    private var selectedType = ""
    private var favoritePosition = 0
    private var isFavoriteSelected = false
    private var bookingId = "0"

    /*Adapter*/
    private lateinit var couponAdapter: CouponAdapter
    private var offerList = ArrayList<NewOfferListData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)
        try {
            /*Initialize View Model */
            offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

            /*Set Click Listener*/
            ivCouponFavoriteCategory.setOnClickListener(this)
            tvCouponRegularCategory.setOnClickListener(this)
            tvCouponBuzzerCategory.setOnClickListener(this)
            tvCouponCrossPromotionCategory.setOnClickListener(this)
            ivCouponToolbarBack.setOnClickListener(this)

            /*Bottom Bar Click Listener*/
            tvCouponApply.setOnClickListener(this)
            lylCouponHomeBottomBar.setOnClickListener(this)
            lylCouponFavoriteBottomBar.setOnClickListener(this)
            ivCouponLocationBottomBar.setOnClickListener(this)
            lylCouponEventBottomBar.setOnClickListener(this)
            lylCouponMenuBottomBar.setOnClickListener(this)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                etCouponApply.clearFocus()
            }
            this.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            PubFun.hideKeyboard(this@CouponActivity)

            lylCouponBottomBar.visibility =
                if (Config.isMyCouponClickedFromHome) View.GONE else View.VISIBLE
            ivCouponLocationBottomBar.visibility =
                if (Config.isMyCouponClickedFromHome) View.GONE else View.VISIBLE
            ivCouponToolbarBack.visibility =
                if (Config.isMyCouponClickedFromHome) View.VISIBLE else View.GONE
            /*Default visibility gone*/
            tvCouponApplyResponse.visibility = View.GONE

            lylCouponApply.visibility =
                if (Config.isEventBottomBarClicked) View.GONE else View.VISIBLE

            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        //etCouponApply.clearFocus()
        //PubFun.hideKeyboard(this@CouponActivity)
        try {
            Config.isEventMoveToback = false
            if (Config.isCouponRedeem) {
                Config.isCouponRedeem = false
                etCouponApply.setText(Config.getSelectedCouponCode)
            } else {
//                Config.isCouponRedeem = false
//                Config.getSelectedCouponCode = ""
//                Config.isCouponApplied = false
//                Config.isCouponDiscountType = 0
//                Config.isCouponBuyQty = 0
//                Config.isCouponGetQty = 0
//                Config.isCouponMenuId = 0
//                Config.isCouponBuyGetSelected = false
//                etCouponApply.setText("")
            }

            /*Get Offer List Service Id*/
            serviceId =
                PubFun.isNull(
                    myRoomDatabase.daoConfig()
                        .selectConfigTableByField(Config.dbOfferListServiceId), "0"
                )

            /*Call Offer list byDefault will be 'Regular' */
            tvCouponRegularCategory.performClick()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (PubFun.isInternetConnection(this@CouponActivity)) {

            Config.isCouponRedeemButtonVisible = false
            if (Config.isMyCouponClickedFromHome) {
                Config.isMyCouponClickedFromHome = false
                if (Config.isMyCouponClickedFromProfileMenu) {
                    Config.isMyCouponClickedFromProfileMenu = false
                    lylCouponMenuBottomBar.performClick()
                }
                finish()
            } else if (Config.isEventBottomBarClicked) {
                Config.isEventBottomBarClicked = false
                // lylCouponHomeBottomBar.performClick()
                Config.isEventMoveToback = true
                this.moveTaskToBack(true)
            } else if (!Config.isEventMoveToback) {

                super.onBackPressed()
                finish()
            }
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    override fun onClick(v: View?) {
        try {
            isFavoriteSelected = false
            when (v) {
                ivCouponToolbarBack -> onBackPressed()

                lylCouponHomeBottomBar -> {
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.homeBottomBarClicked
                    onBackPressed()
                }
                lylCouponFavoriteBottomBar -> {
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.favoriteBottomBarClick
                    onBackPressed()
                }
                ivCouponLocationBottomBar -> {
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.locationBottomBarClick
                    onBackPressed()
                }
                lylCouponEventBottomBar -> {
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.eventBottomBarClick
                    onBackPressed()
                }
                lylCouponMenuBottomBar -> {
                    Config.isEventBottomBarClicked = false
                    Config.bottomBarClickedName = Config.menuBottomBarClick
                    onBackPressed()
                }

                /*Favorite Click*/
                ivCouponFavoriteCategory -> {
                    /*Favorite Selected*/
                    setFavoriteCategorySelection(true)

                    /*Regular Unselected*/
                    setRegularCategorySelection(false)

                    /*Buzzer Unselected*/
                    setBuzzerCategorySelection(false)

                    /*Cross Promotion Unselected*/
                    setCrossPromotionCategorySelection(false)

                    /*Show Only Favorite List*/
                    isFavoriteSelected = true
                    populateOfferList(offerListRes)
                }

                /*Regular Click*/
                tvCouponRegularCategory -> {
                    /*Favorite Unselected*/
                    setFavoriteCategorySelection(false)

                    /*Regular Selected*/
                    setRegularCategorySelection(true)

                    /*Buzzer Unselected*/
                    setBuzzerCategorySelection(false)

                    /*Cross Promotion Unselected*/
                    setCrossPromotionCategorySelection(false)

                    selectedType = Constants.regular
                    callOfferList(serviceId, "")
                }

                /*Buzzer Click*/
                tvCouponBuzzerCategory -> {
                    /*Favorite Unselected*/
                    setFavoriteCategorySelection(false)

                    /*Regular Unselected*/
                    setRegularCategorySelection(false)

                    /*Buzzer Selected*/
                    setBuzzerCategorySelection(true)

                    /*Cross Promotions Unselected*/
                    setCrossPromotionCategorySelection(false)

                    selectedType = Constants.buzzer
                    populateOfferList(offerListRes)
                }

                /*Cross Promotion Click*/
                tvCouponCrossPromotionCategory -> {
                    /*Favorite Unselected*/
                    setFavoriteCategorySelection(false)

                    /*Regular Unselected*/
                    setRegularCategorySelection(false)

                    /*Buzzer Unselected*/
                    setBuzzerCategorySelection(false)

                    /*Cross Promotions Selected*/
                    setCrossPromotionCategorySelection(true)

                    selectedType = Constants.cross_promotion
                    populateOfferList(offerListRes)
                }

                tvCouponApply -> {
                    PubFun.hideKeyboard(this@CouponActivity)
                    if (etCouponApply.text.toString().isNotEmpty()) {
                        callCheckPromoCode(etCouponApply.text.toString())
                    } else {
                        showMsgDialogAndProceed("Please enter coupon code")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setFavoriteCategorySelection(isFavoriteSelected: Boolean) {
        try {
            ivCouponFavoriteCategory.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@CouponActivity,
                        if (isFavoriteSelected) R.drawable.ic_heart_fill else R.drawable.ic_heart_empty
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRegularCategorySelection(isRegularSelected: Boolean) {
        try {
            tvCouponRegularCategory.apply {
                setTextColor(
                    ContextCompat.getColor(
                        this@CouponActivity,
                        if (isRegularSelected) R.color.white else R.color.slate_gray
                    )
                )
                background = ContextCompat.getDrawable(
                    this@CouponActivity,
                    if (isRegularSelected) R.drawable.bg_my_robbins_egg_blue_circle else
                        R.drawable.bg_my_valhalla_circle
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBuzzerCategorySelection(isBuzzerSelected: Boolean) {
        try {
            tvCouponBuzzerCategory.apply {
                setTextColor(
                    ContextCompat.getColor(
                        this@CouponActivity,
                        if (isBuzzerSelected) R.color.white else R.color.slate_gray
                    )
                )
                background = ContextCompat.getDrawable(
                    this@CouponActivity,
                    if (isBuzzerSelected) R.drawable.bg_my_robbins_egg_blue_circle else
                        R.drawable.bg_my_valhalla_circle
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCrossPromotionCategorySelection(isCrossPromotionSelected: Boolean) {
        try {
            tvCouponCrossPromotionCategory.apply {
                setTextColor(
                    ContextCompat.getColor(
                        this@CouponActivity,
                        if (isCrossPromotionSelected) R.color.white else R.color.slate_gray
                    )
                )
                background = ContextCompat.getDrawable(
                    this@CouponActivity,
                    if (isCrossPromotionSelected) R.drawable.bg_my_robbins_egg_blue_circle else
                        R.drawable.bg_my_valhalla_circle
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            /*Get Booking Id*/
            if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
                bookingId =
                    myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbTableBookingId)!!
            } else {
                bookingId = "0"
            }

            //region Progress Bar response
            offerViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@CouponActivity)
                    } else {
                        Utils.hideProgress(this@CouponActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Offer List Response
            offerViewModel.responseOffers.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> populateOfferList(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Offer Favorite Coupon
            offerViewModel.responseFavoriteCoupon.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> couponAdapter.addFavoriteToCoupon(
                            favoritePosition,
                            if (it.message!!.trim()
                                    .lowercase(Locale.getDefault()) == "added"
                            ) 1 else 0
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Check Promo Code Coupon Response
            offerViewModel.responseCheckPromoCodeNew.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> {
                            Config.isCouponRedeem = false
                            Config.isCouponApplied = false
                            Config.getSelectedCouponCode = ""
                            Config.isCouponDiscountType = 0
                            Config.isCouponBuyQty = 0
                            Config.isCouponGetQty = 0
                            Config.isCouponMenuId = 0
                            showMsgDialogAndProceed(it.message!!.trim())
                        }
                        1 -> {
                            tvCouponApplyResponse.text = it.message!!.trim()
                            Config.isCouponApplied = it.isValid!! == 1
                            Log.e("CheckNowCoupon", Config.isCouponApplied.toString())
                            if (Config.isCouponOpeningFromBucket) {
                                onBackPressed()
                            }
                        }
                    }
                    tvCouponApplyResponse.visibility =
                        if (it.isValid!! == 1) View.VISIBLE else View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callOfferList(id: String, type: String) {
        try {
            if (PubFun.isInternetConnection(this@CouponActivity)) {
                offerViewModel.offer_list(id, type)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callOfferFavoriteCoupon(offerId: String) {
        try {
            if (PubFun.isInternetConnection(this@CouponActivity)) {
                offerViewModel.offerFavoriteCoupon(offerId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callCheckPromoCode(promoCode: String) {
        try {
            if (PubFun.isInternetConnection(this@CouponActivity)) {
                offerViewModel.check_promo_code_new(
                    serviceId,
                    promoCode,
                    bookingId
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateOfferList(res: NewOfferListRes) {
        try {
            if (res.data!!.isEmpty()) {
                rvOfferList.visibility = View.GONE
            } else {
                offerListRes = res
                val listener = object : ListClickListener {
                    override fun onClickListener(view: View, pos: Int, objects: Any) {
                        if (PubFun.isInternetConnection(this@CouponActivity)) {
                            val data = objects as NewOfferListData
                            myRoomDatabase.daoConfig().apply {
                                deleteConfigTableByField(Config.dbOfferListResRowData)
                                insertConfigTable(
                                    TableConfig(
                                        Config.dbOfferListResRowData,
                                        gson.toJson(data)
                                    )
                                )
                            }
                            if (Config.isCouponOpeningFromBucket) {
                                Config.isCouponRedeem = false
                                Config.getSelectedCouponCode = data.couponCode!!.trim()
                                Config.isCouponDiscountType = data.discountType!!
                                Config.isCouponBuyQty = data.buyQty!!
                                Config.isCouponGetQty = data.getQty!!
                                Config.isCouponMenuId = data.menuId!!

                                etCouponApply.setText(Config.getSelectedCouponCode)
                                tvCouponApply.performClick()
                            } else {
                                Config.isCouponDetailComingFromCouponActivity = true
                                startActivity(
                                    Intent(
                                        this@CouponActivity,
                                        CouponDetailActivity::class.java
                                    )
                                )
                                finish()
                            }
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    }
                }
                val favoriteListener = object : ListClickListener {
                    override fun onClickListener(view: View, pos: Int, objects: Any) {
                        val data = objects as NewOfferListData
                        favoritePosition = pos
                        callOfferFavoriteCoupon(data.id!!.toString().trim())
                    }
                }

                couponAdapter = CouponAdapter(this@CouponActivity, listener, favoriteListener)
                rvOfferList.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(this@CouponActivity)
                    adapter = couponAdapter
                }
                offerList = res.data as ArrayList<NewOfferListData>
                if (isFavoriteSelected) {
                    val alFavoriteList = ArrayList<NewOfferListData>()
                    for (i in offerList.indices) {
                        if (offerList[i].isFavourite == 1) {
                            alFavoriteList.add(offerList[i])
                        }
                    }
                    if (alFavoriteList.isNotEmpty()) {
                        rvOfferList.visibility = View.VISIBLE
                        couponAdapter.addOfferList(alFavoriteList)
                    } else {
                        rvOfferList.visibility = View.GONE
                    }
                } else {
                    val finalOfferListRes = ArrayList<NewOfferListData>()
                    for (i in offerList.indices) {
                        if (offerList[i].type!!.trim()
                                .lowercase(Locale.getDefault()) == selectedType
                        ) {
                            finalOfferListRes.add(offerList[i])
                        }
                    }
                    rvOfferList.visibility = if (finalOfferListRes.isEmpty())
                        View.GONE else View.VISIBLE
                    couponAdapter.addOfferList(finalOfferListRes)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@CouponActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Discounted Coupons"
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