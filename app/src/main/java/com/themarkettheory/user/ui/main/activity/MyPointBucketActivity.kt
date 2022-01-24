package com.themarkettheory.user.ui.main.activity

import RangeTimePickerDialog
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.razorpay.*
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListenerCart
import com.themarkettheory.user.newmodels.booking.bookingdetails.NewBookingDetailsRes
import com.themarkettheory.user.newmodels.bucketcart.GetCartNewRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.BucketMyPointAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketCartRes
import com.themarkettheory.user.ui.restaurant.OrderConfirmationActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_bucket_mypoints.*
import kotlinx.android.synthetic.main.activity_mybucket_new.*
import kotlinx.android.synthetic.main.activity_mybucket_new.clMyBucketTable
import kotlinx.android.synthetic.main.activity_mybucket_new.constraintPickUp
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyPointBucketActivity : BaseActivity(), View.OnClickListener {
    // View Model cart
    private lateinit var cartViewModel: CartViewModel

    //Recycler View adapter object
    private lateinit var bucketMyPointAdapter: BucketMyPointAdapter
    var bucketDataList = ArrayList<MyBucketCartRes>()
    lateinit var vendorDetailViewModel: VendorDetailViewModel

    //general
    var isLoadedFirstTime = true
    var totalAmt = 0.0
    val numberFormat: NumberFormat = DecimalFormat("#0.00")
    var subTotal = 0.0
    var totalTax = 0.0
    var totalPoints = 0
    var bookingId = "0"
    var serviceId = ""
    var serviceName = ""
    val pickupNowType = "pickup now"
    var cartTotal = 0
    private var isMyPointDiningInSelected = false

    // edit text delay
    val delay: Long = 3000
    var lastEditText: Long = 0
    val handlerEditText = Handler(Looper.getMainLooper())

    // for time
    var formatHours = SimpleDateFormat(Config.defaultTimeFormat, Locale.getDefault())

    var radioGroupMyPoints: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bucket_mypoints)
        try {
            // setting keyboard adjustment
            window
                .setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                )
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        /*super.onBackPressed()*/
        if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
            /*if (Config.isMenuFragmentComingFrom.isEmpty()) {
                Config.isMenuFragmentComingFrom = Config.isMenuFragmentComingFromBucketCart
            }*/
            startActivity(
                Intent(this@MyPointBucketActivity, MyPointsActivity::class.java)
                    .putExtra("category", "1")
                    .putExtra("serviceId", serviceId)
                    .putExtra("vendorTitle", serviceName)
                    .putExtra("selectPosition", 2)
            )
            finish()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun callGetCart(bookingID: Int) {
        if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
            cartViewModel.get_cart(bookingID, 1, 0, 0)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
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

        //region For Booking Table for X person with date & time
        if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
            val newBookingDetailsRes =
                gson.fromJson(
                    myRoomDatabase.daoConfig()
                        .selectConfigTableByField(Config.dbNewBookingDetailRes)!!,
                    NewBookingDetailsRes::class.java
                )

            //Getting booking Id
            bookingId = newBookingDetailsRes.data!!.order_id!!.toString().trim()

            //doing table section hidden
            clMyBucketTable.visibility =
                if (newBookingDetailsRes.data!!.adult!! > 0) View.VISIBLE else View.GONE
            constraintPickUp.visibility =
                if (newBookingDetailsRes.data!!.adult!! > 0) View.GONE else View.VISIBLE

            //Booking Table for X People or Persons
//            tvMyBucketTable.text = "Table for ${newBookingDetailsRes.data!!.total_person} " +
//                    if (newBookingDetailsRes.data!!.total_person!! == 1) "People" else "Persons"

            //Booking Date
            tvBucketPointDateText.text = PubFun.parseDate(
                newBookingDetailsRes.data!!.booking_date,
                Config.requestDateFormat,
                Config.defaultDateFormat
            )

            //Booking Time
            tvBucketPointOrderConfirmationTextValue.text =
                newBookingDetailsRes.data!!.booking_time!!.trim()
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
                    /*Reset isRedeemed Service Id*/
                    myRoomDatabase.daoConfig()
                        .deleteConfigTableByField(Config.dbIsRedeemedServiceId)

                    startActivity(
                        Intent(
                            this@MyPointBucketActivity,
                            OrderConfirmationActivity::class.java
                        )
                    )
                    finish()
                }
            }
        })

        //region for service details for dining in option
        if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
            vendorDetailViewModel.service_details(Config.vendorDetailServiceId)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }

        vendorDetailViewModel.responseServiceDetail.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> {
                    tvMyPointsDiningIn.visibility = if (it.data!!.isTableBooking!! == 1)
                        View.VISIBLE else View.GONE
                }
            }
        })
        //endregion

        //Check Restaurant Time API response
        vendorDetailViewModel.responseCheckRestaurantTime.observe(this, {
            try {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> callApiForConfirmOrder()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        //Click listener
        ivBack.setOnClickListener(this)
        tvBucketPointAddMore.setOnClickListener(this)
        tvBucketPointPickUpTime.setOnClickListener(this)
        tvMyPointsDiningIn.setOnClickListener(this)

        // setting up the text on picktime text
        radioGroupMyPoints = findViewById(R.id.radioGroupMyPoints)

        with(radioGroupMyPoints) {
            this?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbBucketPointSchedulePickup -> {
                        ivBucketPointClock.visibility = View.VISIBLE
                        tvBucketPointPickUpTime.visibility = View.VISIBLE
                    }
                    else -> {
                        ivBucketPointClock.visibility = View.GONE
                        tvBucketPointPickUpTime.visibility = View.GONE
                        callApiForPickUpType("PICKUP_NOW", "")
                    }
                }
            })
        }

        // setting onclick listener to confirm your order button
        btnBucketPointCartConfirmYourOrder.setOnClickListener(this)

        val runnableEditText = Runnable {
            if (System.currentTimeMillis() > ((lastEditText + delay) - 500)) {
                callApiForSpecialInstruction(
                    "0",
                    edBucketPointSpecialInstaruction.text.toString().trim()
                )
            }
        }
        edBucketPointSpecialInstaruction.addTextChangedListener(object : TextWatcher {
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

        //Schedule Pick-up Time Text Change Listener
        tvBucketPointPickUpTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    callApiForPickUpType(
                        "SCHEDULE_PICKUP",
                        s.toString().trim()
                    )
                }
            }

        })
    } catch (e: Exception) {
        e.printStackTrace()
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

    private fun callApiForSpecialInstruction(bookingID: String, specialInstructionMsg: String) {
        try {
            if (PubFun.isInternetConnection(this)) {
                cartViewModel.special_request(bookingID, specialInstructionMsg, "1", 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForPickUpType(type: String, scheduleTime: String) {
        try {
            if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
                cartViewModel.pickup_type(type, scheduleTime, 1, 0, 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForConfirmOrder() {
        try {
            if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
                val menuArray = JSONArray()
                for (i in bucketDataList.indices) {
                    val menuObject = JSONObject()
                    menuObject.put("menu_id", bucketDataList[i].menuID.toString())
                    menuObject.put("qty", bucketDataList[i].qty.toString())
                    menuObject.put("price", bucketDataList[i].finalPrice.toString())
                    menuObject.put("points", bucketDataList[i].point.toString())
                    menuObject.put("is_redeem", "1")
                    menuArray.put(menuObject)
                }
                if (PubFun.isInternetConnection(this)) {
                    cartViewModel.confirmOrder(
                        bucketDataList[0].serviceId.toString(),
                        menuArray,
                        subTotal.toString(),
                        totalAmt.toString(),
                        "",
                        "",
                        totalPoints.toString(),
                        edBucketPointSpecialInstaruction.text.toString().trim(),
                        tvBucketPointPickUpTime.text.toString().trim(),
                        totalTax.toString(),
                        bookingId
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }

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
            //setting up the total points
            tvBucketPointPoints.text = res.data!!.serviceDetails!!.points!!.toString().trim()
            cartTotal = res.data!!.serviceDetails!!.points!!

            //restaurant title
            tvBucketPointVendorTitle.text = res.data!!.serviceDetails!!.title!!
            // retaurant address
            tvBucketPointAddress.text = res.data!!.serviceDetails!!.address!!
            // cart size
            val cartSize = res.data!!.list!!.size
            tvBucketPointMyCart.text =
                "My Cart (${cartSize} ${if (cartSize == 1) "Item" else "Items"})"

            //Service ID
            serviceId = res.data!!.serviceDetails!!.id.toString().trim()
            //Service Name
            serviceName = res.data!!.serviceDetails!!.title!!.trim()

            // setting up special instruction
            edBucketPointSpecialInstaruction.setText(
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
                add30MinutesToCurrentTime()
            } else {
                // setting up booking time
                tvBucketPointPickUpTime.text = PubFun.parseDate(
                    res.data!!.booking!!.bookingTime!!,
                    Config.requestTimeFormat,
                    Config.defaultTimeFormat
                )
            }
//            else PubFun.addTimeInCurrentTime(formatHours, Calendar.MINUTE, 30)

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
                        //calling api for adding and removing items
                        if (bucketData.qty > 0) {
                            /*Validation with total points*/
                            if (cartTotal - bucketData.point < 0 && isItemAdded) {
                                bucketDataList[pos].qty = bucketData.qty - 1
                                bucketMyPointAdapter.notifyDataSetChanged()
                                showMsgDialogAndProceed("You don't have enough points")
                            } else {
                                bucketDataList[pos].qty = bucketData.qty
                                bucketMyPointAdapter.notifyDataSetChanged()
                                calculateFooterSection(bucketDataList)

                                if (isItemAdded) {
                                    cartTotal -= bucketData.point
                                    tvBucketPointPoints.text = cartTotal.toString()
                                } else {
                                    cartTotal += bucketData.point
                                    tvBucketPointPoints.text = cartTotal.toString()
                                }

                                vendorDetailViewModel.menu_add_cart(
                                    bucketData.serviceId.toString(),
                                    bucketData.menuID.toString(),
                                    "1",
                                    bucketData.qty.toString(),
                                    "0",
                                    0,
                                    if (isMyPointDiningInSelected) 1 else 0
                                )
                            }
                        } else {
                            vendorDetailViewModel.menu_add_cart(
                                bucketData.serviceId.toString(),
                                bucketData.menuID.toString(),
                                "1",
                                "0",
                                "0",
                                0,
                                if (isMyPointDiningInSelected) 1 else 0
                            )
                            bucketMyPointAdapter.removeItem(pos)
                            bucketDataList.removeAt(pos)
                            // cart size
                            val cartSize = bucketDataList.size
                            tvBucketPointMyCart.text =
                                "My Cart (${cartSize} ${if (cartSize == 1) "Item" else "Items"})"
                            // calling backpress when cart size is 0
                            if (cartSize < 1) {
                                onBackPressed()
                            }
                            calculateFooterSection(bucketDataList)

                            cartTotal += bucketData.point
                            tvBucketPointPoints.text = cartTotal.toString()
                        }
                    }
                }
            }
            bucketMyPointAdapter = BucketMyPointAdapter(listener)
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
                    res.data!!.list!![i].menu!!.redeemPoints!!,
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
                    res.data!!.serviceDetails!!.offers!![i].getQty!!
                )
                bucketDataList.add(bucketCartRes)
            }
            bucketMyPointAdapter.setBucketData(bucketDataList)
            rvBucketPointMyCart.apply {
                layoutManager = LinearLayoutManager(this@MyPointBucketActivity)
                adapter = bucketMyPointAdapter
            }
            calculateFooterSection(bucketDataList)

            cartTotal -= totalPoints
            tvBucketPointPoints.text = cartTotal.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun add30MinutesToCurrentTime() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 30)
        tvBucketPointPickUpTime.text = PubFun.parseDate(
            cal.time.toString(),
            "EEE MMM dd HH:mm:ss",
            Config.defaultTimeFormat
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
                totalPoints += bucketDataList[i].qty * bucketDataList[i].point
            }

            //Setting Total Item Point
            tvBucketPointTotalOrderPoints.text = totalPoints.toString()
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
                tvBucketPointAddMore -> {
                    onBackPressed()
                }
                tvBucketPointPickUpTime -> {
                    openRangeTimePickerDialog()
                }
                btnBucketPointCartConfirmYourOrder -> {
                    callApiForConfirmOrder()
                }
                tvMyPointsDiningIn -> {
                    tvMyPointsDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (isMyPointDiningInSelected) R.drawable.ic_radio_button_unchecked
                        else R.drawable.ic_radio_button_checked,
                        0,
                        0,
                        0
                    )
                    isMyPointDiningInSelected = !isMyPointDiningInSelected
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openRangeTimePickerDialog() {
        try {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = RangeTimePickerDialog(
                this@MyPointBucketActivity,
                2,
                { timePicker, selectedHour, selectedMinute ->
                    val mySelectedHrs: String = selectedHour.toString()
                    val mySelectedMin: String =
                        if (selectedMinute.toString().length == 1) "0$selectedMinute" else selectedMinute.toString()
                    val mySelectedTime: String = "$mySelectedHrs:$mySelectedMin"
                    tvBucketPointPickUpTime.text =
                        PubFun.parseDate(mySelectedTime, "HH:mm", Config.defaultTimeFormat)
                }, hour, minute, false, true
            ) //true = 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.setMin(hour, minute)
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}