package com.themarkettheory.user.ui.restaurant

import RangeTimePickerDialog
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.razorpay.*
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListenerCart
import com.themarkettheory.user.newmodels.booking.bookingdetails.NewBookingDetailsRes
import com.themarkettheory.user.newmodels.bucketcart.GetCartNewRes
import com.themarkettheory.user.ui.coupon.CouponActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_bucket.*
import kotlinx.android.synthetic.main.activity_my_bucket.rvMyCart
import kotlinx.android.synthetic.main.activity_my_bucket.tvAddMore
import kotlinx.android.synthetic.main.activity_my_bucket.tvAddress
import kotlinx.android.synthetic.main.activity_my_bucket.tvMyCart
import kotlinx.android.synthetic.main.activity_my_bucket.tvSubtotal
import kotlinx.android.synthetic.main.activity_my_bucket.tvVendorTitle
import kotlinx.android.synthetic.main.activity_my_bucket.tvtax
import kotlinx.android.synthetic.main.activity_mybucket_new.*
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyBucketActivity : BaseActivity(), View.OnClickListener, PaymentResultWithDataListener {
    // View Model cart
    private lateinit var cartViewModel: CartViewModel
    private lateinit var vendorDetailViewModel: VendorDetailViewModel

    //Recycler View adapter object
    private lateinit var bucketAdapter: BucketAdapter
    var bucketDataList = ArrayList<MyBucketCartRes>()

    //general
    var isLoadedFirstTime = true
    var totalAmt = 0.0
    val numberFormat: NumberFormat = DecimalFormat("#0.00")
    var subTotal = 0.0
    var totalTax = 0.0
    var discountCouponTotal = 0.0
    var totalPoints = 0
    var bookingId = "0"
    var serviceId = ""
    var serviceName = ""
    val pickupNowType = "pickup now"
    private var isDiningInSelected = false

    //discounted coupon
    private val couponPercentage = 1
    private val couponFlat = 2
    private val couponBuyGet = 3
    private var lastClickTime = 0L

    // edit text delay
    val delay: Long = 3000
    var lastEditText: Long = 0
    val handlerEditText = Handler(Looper.getMainLooper())

    // for time
    var formatHours = SimpleDateFormat(Config.defaultTimeFormat, Locale.getDefault())

    var radioGroup: RadioGroup? = null
    var pickupNowsss: RadioButton? = null

    private lateinit var tvCouponCode: MaterialTextView
    private lateinit var tvDiningIn: MaterialTextView

    var selectedIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mybucket_new)
        try {
            // setting keyboard adjustment
            window
                .setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                )

            tvCouponCode = findViewById(R.id.tvCouponCode)
            tvDiningIn = findViewById(R.id.tvDiningIn)
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
           // init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        /*super.onBackPressed()*/
        if (PubFun.isInternetConnection(this@MyBucketActivity)) {
            if (Config.isHomeGoCartClicked && !Config.isBucketAddMoreClicked) {
                Config.isHomeGoCartClicked = false
                finish()
            } else {
                if (Config.isMenuFragmentComingFrom.isEmpty()) {
                    Config.isMenuFragmentComingFrom = Config.isMenuFragmentComingFromBucketCart
                }
                Config.isVideoPlay = false
                startActivity(
                    Intent(this@MyBucketActivity, VendorDetailActivity::class.java)
                        .putExtra("category", "1")
                        .putExtra("serviceId", serviceId)
                        .putExtra("vendorTitle", serviceName)
                        .putExtra("selectPosition", 2)
                )
            }
            if (Config.isCouponComingFromMainActivity) {
                Config.isCouponComingFromMainActivity = false
                Config.isEventBottomBarClicked = true
            }
            if (Config.isCouponComingFromAllFragment) {
                Config.isCouponComingFromAllFragment = false
                Config.isCouponOpeningFromBucket = false
                Config.isEventBottomBarClicked = true
                Config.isMyCouponClickedFromHome = true
            }
            if (Config.isCouponComingFromOverviewViewAll) {
                Config.isCouponComingFromOverviewViewAll = false
                Config.isCouponOpeningFromBucket = false
                Config.isEventBottomBarClicked = true
                Config.isMyCouponClickedFromHome = true
            }
            if (Config.isCouponComingFromMenuFragment) {
                Config.isCouponComingFromMenuFragment = false
                Config.isCouponOpeningFromBucket = false
                Config.isEventBottomBarClicked = true
                Config.isMyCouponClickedFromHome = true
                Config.isMyCouponClickedFromProfileMenu = true
            }
            finish()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun callGetCart(bookingID: Int) {
        if (PubFun.isInternetConnection(this)) {
            cartViewModel.get_cart(bookingID, 0, 0, 0)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun callServiceDetail() {
        try {
            if (PubFun.isInternetConnection(this@MyBucketActivity)) {
                if (!Config.isPreOrder) { //if pre order is false then only call this API
                    vendorDetailViewModel.service_details(Config.vendorDetailServiceId)
                } else {
                    tvDiningIn.visibility = View.GONE
                }
            } else {
                tvDiningIn.visibility = View.GONE
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun init() = try {
        //view model intialization
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        // toolbar title
        tvTitle.text = getString(R.string.my_bucket)


        clMyBucketTable.visibility = View.GONE
        constraintPickUp.visibility = View.VISIBLE
//
//            if (Config.isCouponApplied) {
//                tvCouponCode.text = Config.getSelectedCouponCode
//            }

        applyCouponChanges()

        //region For Booking Table for X person with date & time
        if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
            val newBookingDetailsRes =
                gson.fromJson(
                    myRoomDatabase.daoConfig()
                        .selectConfigTableByField(Config.dbNewBookingDetailRes)!!,
                    NewBookingDetailsRes::class.java
                )
            Log.e("TableBBB0",gson.toJson(newBookingDetailsRes.data))
            //Getting booking Id
            bookingId = newBookingDetailsRes.data!!.order_id!!.toString().trim()

            //doing table section hidden
            clMyBucketTable.visibility =
                if (newBookingDetailsRes.data!!.adult!! > 0) View.VISIBLE else View.GONE
            constraintPickUp.visibility =
                if (newBookingDetailsRes.data!!.adult!! > 0) View.GONE else View.VISIBLE

            //Booking Table for X People or Persons
            tvMyBucketTable.text = "Table for ${newBookingDetailsRes.data!!.total_person} " +
                    if (newBookingDetailsRes.data!!.total_person!! == 1) "People" else "Persons"

            //Booking Date
            tvMyBucketDateText.text = PubFun.parseDate(
                newBookingDetailsRes.data!!.booking_date,
                Config.requestDateFormat,
                Config.defaultDateFormat
            )

            //Booking Time
            tvOrderConfirmationTextValue.text = newBookingDetailsRes.data!!.booking_time!!.trim()
        }
        //endregion

        // requesting api for cart detail
        callGetCart(bookingId.toInt())

        //api response for cart details
        cartViewModel.responseGetCartNew.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> populateCartDetails(it!!)
            }
        })

        // api response for order confirmation
        cartViewModel.responseConfirmOrder.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> {
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbOrderId)
                    myRoomDatabase.daoConfig().insertConfigTable(
                        TableConfig(
                            Config.dbOrderId,
                            it.orderId!!.toString().trim()
                        )
                    )
                    startActivity(
                        Intent(
                            this@MyBucketActivity,
                            OrderConfirmationActivity::class.java
                        )
                    )
                    finish()
                }
            }
        })

        //Check Restaurant Time API response
        vendorDetailViewModel.responseCheckRestaurantTime.observe(this, {
            try {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> startRazorPayment()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        //Click listener
        ivBack.setOnClickListener(this)
        tvAddMore.setOnClickListener(this)
        tvMyBucketPickUpTime.setOnClickListener(this)
        clBucketPromoCode.setOnClickListener(this)
        ivCouponCross.setOnClickListener(this)


        // setting up the text on picktime text
        radioGroup = findViewById(R.id.radioGroup)

        with(radioGroup) {
            this?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbSchedulePickup -> {
//                        if (isDiningInSelected) {
//                            tvDiningIn.performClick()
//                        }
                        ivMyBucketClock.visibility = View.VISIBLE
                        tvMyBucketPickUpTime.visibility = View.VISIBLE
                        tvMyBucketPickUpTime.text = "Select Schedule Time"
                        selectedIndex = 0
                        callApiForPickUpType("SCHEDULE_PICKUP", "")

                    }
                    R.id.rbPickupNow -> {
//                        if (isDiningInSelected) {
//                            tvDiningIn.performClick()
//                        }
                        ivMyBucketClock.visibility = View.GONE
                        tvMyBucketPickUpTime.visibility = View.GONE
                        selectedIndex = 1

                        callApiForPickUpType("PICKUP_NOW", "")
                    }
//                    // else -> {
                }
            })
        }

        vendorDetailViewModel.responseServiceDetail.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> {
                    tvDiningIn.visibility = if (it.data!!.isTableBooking!! == 1)
                        View.VISIBLE else View.GONE
                }
            }
        })

        tvDiningIn.setOnClickListener {
//            if (!isDiningInSelected) {
//                with(radioGroup) { this?.clearCheck() }
//            }

            tvDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (isDiningInSelected) R.drawable.ic_radio_button_unchecked else
                    R.drawable.ic_radio_button_checked,
                0,
                0,
                0
            )


            isDiningInSelected = !isDiningInSelected
//            if(isDiningInSelected){
//                selectedIndex = 2
//
//            }

        }


        // setting onclick listener to confirm your order button
        btnMyBucketCartConfirmYourOrder.setOnClickListener(this)

        val runnableEditText = Runnable {
            if (System.currentTimeMillis() > ((lastEditText + delay) - 500)) {
                callApiForSpecialInstruction(
                    "0",
                    edMyBucketSpecialInstaruction.text.toString().trim()
                )
            }
        }
        edMyBucketSpecialInstaruction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerEditText.removeCallbacks(runnableEditText)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty()) {
                    lastEditText = System.currentTimeMillis()
                    handlerEditText.postDelayed(runnableEditText, delay)
                }
            }

        })
    } catch (e: Exception) {
        e.printStackTrace()
    }

    private fun applyCouponChanges() {
        try {/*Apply Coupon*/

            ivCouponCross.visibility = if (Config.isCouponApplied) View.VISIBLE else View.GONE
            tvMyBucketCouponDiscountText.visibility =
                if (Config.isCouponApplied) View.VISIBLE else View.GONE
            tvCouponDiscount.visibility = if (Config.isCouponApplied) View.VISIBLE else View.GONE

            tvCouponCode.apply {
                text =
                    if (Config.isCouponApplied) Config.getSelectedCouponCode else getString(R.string.apply_promo_code)
                setTextColor(
                    if (Config.isCouponApplied)
                        ContextCompat.getColor(
                            this@MyBucketActivity,
                            R.color.myRobinsEggBlue
                        ) else ContextCompat.getColor(
                        this@MyBucketActivity,
                        R.color.myWhite
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callCheckRestaurantTimeApi(id: Int, time: String) {
        vendorDetailViewModel.checkRestaurantTime(id, time)
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this)
            val myDialog = DialogToast(this)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_bucket)
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

    private fun callApiForSpecialInstruction(bookingID: String, specialInstructionMsg: String) {
        try {
            if (PubFun.isInternetConnection(this)) {
                cartViewModel.special_request(bookingID, specialInstructionMsg, "0", 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForPickUpType(type: String, scheduleTime: String) {
        Log.e(type, scheduleTime)
        try {
            if (PubFun.isInternetConnection(this)) {
                cartViewModel.pickup_type(type, scheduleTime, 0, 0, 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForConfirmOrder(paymentData: PaymentData) {
        try {
            val menuArray = JSONArray()
            for (i in bucketDataList.indices) {
                val menuObject = JSONObject()
                menuObject.put("menu_id", bucketDataList[i].menuID.toString())
                menuObject.put("qty", bucketDataList[i].qty.toString())
                menuObject.put("price", bucketDataList[i].finalPrice.toString())
                menuObject.put("points", bucketDataList[i].point.toString())
                menuObject.put("is_redeem", "")
                menuArray.put(menuObject)
            }
            if (PubFun.isInternetConnection(this)) {

                val currentTimess: String =
                    SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
                if (selectedIndex == 1) {

                    cartViewModel.confirmOrder(
                        bucketDataList[0].serviceId.toString(),
                        menuArray,
                        subTotal.toString(),
                        totalAmt.toString(),
                        "",
                        paymentData.paymentId.toString(),
                        totalPoints.toString(),
                        edMyBucketSpecialInstaruction.text.toString().trim(),
                        currentTimess,
                        totalTax.toString(),
                        bookingId
                    )
                    return
                }
                cartViewModel.confirmOrder(
                    bucketDataList[0].serviceId.toString(),
                    menuArray,
                    subTotal.toString(),
                    totalAmt.toString(),
                    "",
                    paymentData.paymentId.toString(),
                    totalPoints.toString(),
                    edMyBucketSpecialInstaruction.text.toString().trim(),
                    tvMyBucketPickUpTime.text.toString().trim(),
                    totalTax.toString(),
                    bookingId
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateCartDetails(res: GetCartNewRes) {
        try {

            Log.e("Cart Data", gson.toJson(res.data))
            //restaurant id
            Config.vendorDetailServiceId = res.data!!.serviceDetails!!.id!!.toString()
            //restaurant name
            Config.vendorDetailServiceName = res.data!!.serviceDetails!!.title!!
            //restaurant title
            tvVendorTitle.text = res.data!!.serviceDetails!!.title!!
            // retaurant address
            tvAddress.text = res.data!!.serviceDetails!!.address!!
            // cart size
            val cartSize = res.data!!.list!!.size
            tvMyCart.text = "My Cart (${cartSize} ${if (cartSize == 1) "Item" else "Items"})"
            //Service ID
            serviceId = res.data!!.serviceDetails!!.id.toString().trim()
            //Service Name
            serviceName = res.data!!.serviceDetails!!.title!!.trim()

            // setting up special instruction
            edMyBucketSpecialInstaruction.setText(
                res.data!!.booking!!.specialInstruction!!.toString().trim()
            )
            // setting up radio group selection
            radioGroup?.apply {
                check(
                    getChildAt(
                        if (res.data!!.booking!!.type!!.toString()
                                .lowercase(Locale.getDefault()) == pickupNowType
                        ) 1 else 0
                    ).id
                )
            }
            // setting time +30 if pickup type is pickup now
            if (res.data!!.booking!!.type!!.toString()
                    .lowercase(Locale.getDefault()) == pickupNowType
            ) {
                //add30MinutesToCurrentTime()
                selectedIndex = 1
                callApiForPickUpType("PICKUP_NOW", "")

            } else {
                // setting up booking time
                if (res.data!!.booking!!.bookingTime!!.isNotEmpty()) {
                    tvMyBucketPickUpTime.text = PubFun.parseDate(
                        res.data!!.booking!!.bookingTime!!,
                        Config.requestTimeFormat,
                        Config.defaultTimeFormat
                    )
                    callApiForPickUpType(
                        "SCHEDULE_PICKUP", PubFun.parseDate(
                            tvMyBucketPickUpTime.text.toString(),
                            Config.defaultTimeFormat,
                            Config.requestTimeFormat
                        ).toString()
                    )
                    // tvMyBucketPickUpTime.text.toString().trim()

                } else {
                    callApiForPickUpType(
                        "SCHEDULE_PICKUP",
                        ""
                    )
                }
                selectedIndex = 0


            }


            //recycler view
            val listener = object : ListClickListenerCart {
                override fun onClickListener(
                    view: View,
                    pos: Int,
                    objects: Any,
                    isItemAdded: Boolean
                ) {
                    if (!isLoadedFirstTime) {
                        val bucketData = objects as MyBucketCartRes
                        bucketDataList[pos].qty = bucketData.qty
                        bucketAdapter.notifyDataSetChanged()
                        calculateFooterSection(bucketDataList)

                        //calling api for adding and removing items
                        if (bucketData.qty > 0) {
                            vendorDetailViewModel.menu_add_cart(
                                bucketData.serviceId.toString(),
                                bucketData.menuID.toString(),
                                "0",
                                bucketData.qty.toString(),
                                "0",
                                0,
                                if (isDiningInSelected) 1 else 0
                            )

                            //region Coupon Logic for Bug 1 Get 1 free
                            val totalQty = Config.isCouponBuyQty + Config.isCouponGetQty
                            if (Config.isCouponApplied && Config.isCouponDiscountType == couponBuyGet
                                && Config.isCouponMenuId == bucketData.menuID
                            ) {
                                Config.isCouponBuyGetSelected = true
                                if (bucketData.qty == totalQty) {
                                    //Applied coupon discount
                                    coupon@ for (i in bucketDataList.indices) {
                                        if (Config.isCouponMenuId == bucketDataList[i].menuID) {
                                            discountCouponTotal = bucketDataList[i].finalPrice
                                            break@coupon
                                        }
                                    }
                                } else if (bucketData.qty < totalQty) {
                                    //remove coupon discount
                                    discountCouponTotal = 0.0
                                }
                                calculateFooterSection(bucketDataList)
                            }
                            //endregion
                        } else {
                            vendorDetailViewModel.menu_add_cart(
                                bucketData.serviceId.toString(),
                                bucketData.menuID.toString(),
                                "0",
                                "0",
                                "0",
                                0,
                                if (isDiningInSelected) 1 else 0
                            )
                            bucketAdapter.removeItem(pos)
                            bucketDataList.removeAt(pos)
                            // cart size
                            val cartSize = bucketDataList.size
                            tvMyCart.text =
                                "My Cart (${cartSize} ${if (cartSize == 1) "Item" else "Items"})"
                            // calling onbackpress when cart size is 0
                            if (cartSize < 1) {
                                onBackPressed()
                            }
                            calculateFooterSection(bucketDataList)

                            if (Config.isCouponApplied && Config.isCouponDiscountType == couponBuyGet
                                && Config.isCouponMenuId == bucketData.menuID
                            ) {
                                ivCouponCross.performClick()
                            }
                        }
                    }
                }
            }
            bucketAdapter = BucketAdapter(listener)
            bucketDataList.clear()
            for (i in res.data!!.list!!.indices) {
                val bucketCartRes = MyBucketCartRes(
                    res.data!!.serviceDetails!!.id!!,
                    res.data!!.serviceDetails!!.title!!,
                    res.data!!.list!![i].menu!!.foodType!!,
                    res.data!!.list!![i].menu!!.isSpicy!!,
                    res.data!!.list!![i].menu!!.id!!,
                    res.data!!.list!![i].menu!!.title!!,
                    res.data!!.list!![i].menu!!.categoryName!!,
                    res.data!!.list!![i].menu!!.finalPrice!!,
                    res.data!!.list!![i].menu!!.actualPrice!!,
                    res.data!!.list!![i].menu!!.point!!,
                    res.data!!.list!![i].menu!!.preparingTime!!,
                    res.data!!.list!![i].qty!!,
                    res.data!!.list!![i].menu!!.tax!!.toDouble(),
                    res.data!!.list!![i].menu!!.currency!!,
                    res.data!!.serviceDetails!!.currencyStr!!,
                    res.data!!.serviceDetails!!.offers!![i].menuId!!,
                    res.data!!.serviceDetails!!.offers!![i].couponCode!!.trim(),
                    res.data!!.serviceDetails!!.offers!![i].discountType!!,
                    res.data!!.serviceDetails!!.offers!![i].discountAmount!!,
                    res.data!!.serviceDetails!!.offers!![i].buyQty!!,
                    res.data!!.serviceDetails!!.offers!![i].getQty!!,
                )
                bucketDataList.add(bucketCartRes)
            }
            bucketAdapter.setBucketData(bucketDataList)
            rvMyCart.apply {
                layoutManager = LinearLayoutManager(this@MyBucketActivity)
                adapter = bucketAdapter
            }
            calculateFooterSection(bucketDataList)

            if (SystemClock.elapsedRealtime() - lastClickTime < 10000) return
            lastClickTime = SystemClock.elapsedRealtime()
            if (Config.isCouponApplied && Config.isCouponDiscountType == couponBuyGet
                && !Config.isCouponBuyGetSelected
            ) {
                val dialogCoupon = DialogToast(this@MyBucketActivity)
                dialogCoupon.show()
                dialogCoupon.holder!!.let {
                    it.tvTitle.text = "The Market Theory"
                    it.tvMessage.text = "You have applied for buy ${Config.isCouponBuyQty} and " +
                            "get ${Config.isCouponGetQty}. So do you want to add more qty?"
                    it.btnDialogCancel.text = "No"
                    it.btnDialogLogout.text = "Yes"
                    it.btnDialogCancel.setOnClickListener {
                        dialogCoupon.dismiss()
                    }
                    it.btnDialogLogout.setOnClickListener {
                        try {
                            dialogCoupon.dismiss()
                            Config.isCouponBuyGetSelected = true
                            val totalQty = Config.isCouponBuyQty + Config.isCouponGetQty
                            coupon@ for (i in bucketDataList.indices) {
                                if (Config.isCouponMenuId == bucketDataList[i].menuID) {
                                    bucketDataList[i].qty = totalQty
                                    discountCouponTotal = bucketDataList[i].finalPrice
                                    bucketAdapter.updatedQty(i, totalQty)
                                    break@coupon
                                }
                            }
                            calculateFooterSection(bucketDataList)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            callServiceDetail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun add30MinutesToCurrentTime() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 30)
        tvMyBucketPickUpTime.text = PubFun.parseDate(
            cal.time.toString(),
            "EEE MMM dd HH:mm:ss",
            Config.defaultTimeFormat
        )
        callApiForPickUpType(
            "PICKUP_NOW",
            ""
        )
    }

    @SuppressLint("SetTextI18n")
    private fun calculateFooterSection(bucketDataList: ArrayList<MyBucketCartRes>) {
        try {
            subTotal = 0.0
            totalTax = 0.0
            totalPoints = 0
            totalAmt = 0.0

            for (i in bucketDataList.indices) {
                //Calculate Sub Total
                subTotal += bucketDataList[i].qty * bucketDataList[i].finalPrice

                //Calculate Total Tax
                //totalTax += (subTotal * bucketDataList[i].tax) / 100

                //Calculate Total Points
                totalPoints += bucketDataList[i].qty * bucketDataList[i].point
            }

            totalTax += (subTotal * bucketDataList[0].tax) / 100

            //region Check for discounted coupon if any
            if (Config.isCouponApplied) {
                coupon@ for (i in bucketDataList.indices) {

//                    val discountAmt = bucketDataList[i].offerDiscountAmount
//                    Log.e("coupon discount", "{$discountAmt}")
                    if (bucketDataList[i].offerCouponCode.lowercase(Locale.getDefault()) == Config.getSelectedCouponCode.lowercase(Locale.getDefault()))
                    {
                        val discountAmt = bucketDataList[i].offerDiscountAmount
                        Log.e("coupon discount", "{$discountAmt}")
                        discountCouponTotal = when (bucketDataList[i].offerDiscountType) {
                            couponPercentage -> (subTotal * discountAmt) / 100
                            couponFlat -> discountAmt.toDouble()
                            else -> if (Config.isCouponBuyGetSelected) discountCouponTotal else 0.0
                        }
                        break@coupon
                    }
                }
            }
            //endregion

            //Setting Sub Total Amount
            tvSubtotal.text =
                bucketDataList[0].currency + if (subTotal == 0.0) "0.00" else numberFormat.format(
                    subTotal
                )

            //Discount Coupon
            tvCouponDiscount.text = "-${bucketDataList[0].currency}" +
                    numberFormat.format(discountCouponTotal)

            //Setting Tax Amount
            tvtax.text =
                bucketDataList[0].currency + if (totalTax == 0.0) "0.00" else numberFormat.format(
                    totalTax
                )

            //Setting Total Amount = Sub Total Amount + Tax Amount
            totalAmt = subTotal - discountCouponTotal + totalTax
            tvMyBucketTotalAmount.text = "${bucketDataList[0].currency}${
                if (totalAmt == 0.0) "0.00" else numberFormat.format(totalAmt)
            }"

            //Setting Total Item Point
            tvMyBucketTotalOrderPoints.text = totalPoints.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadedFirstTime = false
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivBack -> onBackPressed()
                tvAddMore -> {
                    if (PubFun.isInternetConnection(this@MyBucketActivity)) {
                        if (Config.isHomeGoCartClicked) {
                            Config.isBucketAddMoreClicked = true
                        }
                        onBackPressed()
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
                tvMyBucketPickUpTime -> {
                    openRangeTimePickerDialog()
                }
                btnMyBucketCartConfirmYourOrder -> {
                    if (PubFun.isInternetConnection(this)) {
                        if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
                            startRazorPayment()
                        } else {
                            // is selected type pick up now
                            val currentTimess: String =
                                SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
                            if (selectedIndex == 1) {
                                callCheckRestaurantTimeApi(
                                    serviceId.toInt(),
                                    currentTimess
                                )
                                return
                            }

                            callCheckRestaurantTimeApi(
                                serviceId.toInt(),
                                PubFun.parseDate(
                                    tvMyBucketPickUpTime.text.toString().trim(),
                                    Config.defaultTimeFormat,
                                    Config.requestTimeFormat
                                )!!

                            )
                        }
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
                clBucketPromoCode -> {
                    if (PubFun.isInternetConnection(this@MyBucketActivity)) {
                        Config.isCouponRedeemButtonVisible = true
                        myRoomDatabase.daoConfig().apply {
                            deleteConfigTableByField(Config.dbOfferListServiceId)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbOfferListServiceId,
                                    serviceId
                                )
                            )
                        }
                        Config.isCouponOpeningFromBucket = true
                        Config.isEventBottomBarClicked = false
                        Config.isMyCouponClickedFromHome = true
                        startActivity(Intent(this@MyBucketActivity, CouponActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
                ivCouponCross -> {
                    Config.isCouponApplied = false
                    Config.isCouponRedeem = false
                    Config.getSelectedCouponCode = ""
                    Config.isCouponDiscountType = 0
                    Config.isCouponBuyQty = 0
                    Config.isCouponGetQty = 0
                    Config.isCouponMenuId = 0
                    Config.isCouponBuyGetSelected = false
                    discountCouponTotal = 0.0
                    applyCouponChanges()
                    calculateFooterSection(bucketDataList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openRangeTimePickerDialog() {
        try {
            val mCurrentTime = Calendar.getInstance()
            mCurrentTime.add(Calendar.MINUTE, 30)
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = RangeTimePickerDialog(
                this@MyBucketActivity,
                2,
                { timePicker, selectedHour, selectedMinute ->
                    val mySelectedHrs: String = selectedHour.toString()
                    val mySelectedMin: String =
                        if (selectedMinute.toString().length == 1) "0$selectedMinute" else selectedMinute.toString()
                    val mySelectedTime: String = "$mySelectedHrs:$mySelectedMin"
                    tvMyBucketPickUpTime.text =
                        PubFun.parseDate(mySelectedTime, "HH:mm", Config.defaultTimeFormat)
//                    callApiForPickUpType(
//                        "SCHEDULE_PICKUP",
//                        tvMyBucketPickUpTime.text.toString().trim()
//                    )
                    callApiForPickUpType(
                        "SCHEDULE_PICKUP", PubFun.parseDate(
                            tvMyBucketPickUpTime.text.toString(),
                            Config.defaultTimeFormat,
                            Config.requestTimeFormat
                        ).toString()
                    )
                }, hour, minute, false, true
            ) //true = 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.setMin(hour, minute)
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*razor pay implementation*/
    fun startRazorPayment() {

        val activity = this

        val co = Checkout()
        co.setKeyID(getString(R.string.razorpay_live_key))
        val currencyStr = bucketDataList[0].currencyStr

        try {
            val totalAmount = (totalAmt * 100).toInt()
            val options = JSONObject()
            options.put("currency", currencyStr)
            options.put("amount", totalAmount)
            options.put("name", getString(R.string.app_name))

            val preFill = JSONObject()
            preFill.put("email", prefs.getLoginModel().email)
            preFill.put("contact", prefs.getLoginModel().mobile)
            preFill.put("name", prefs.getLoginModel().name)
            options.put("prefill", preFill)

            val retryObj = JSONObject()
            retryObj.put("enabled", false)
            options.put("retry", retryObj)
            co.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        callApiForConfirmOrder(p1!!)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        // Toast.makeText(this, "in payment faliour", Toast.LENGTH_LONG).show()
    }


}