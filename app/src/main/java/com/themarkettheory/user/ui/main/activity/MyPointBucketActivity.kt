package com.themarkettheory.user.ui.main.activity

import RangeTimePickerDialog
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatEditText
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
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.BucketMyPointAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketCartRes
import com.themarkettheory.user.ui.restaurant.OrderConfirmationActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_bucket_live_deal.*
import kotlinx.android.synthetic.main.activity_my_bucket_mypoints.*
import kotlinx.android.synthetic.main.activity_my_bucket_mypoints.tvBucketPointDateText
import kotlinx.android.synthetic.main.activity_my_bucket_mypoints.tvBucketPointOrderConfirmationTextValue
import kotlinx.android.synthetic.main.activity_my_bucket_mypoints.tvBucketPointPoints
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
    private var isDiningInSelected = false
    private var isMyPointDiningInSelected = false


    // edit text delay
    val delay: Long = 3000
    var lastEditText: Long = 0
    val handlerEditText = Handler(Looper.getMainLooper())

    // for time
    var formatHours = SimpleDateFormat(Config.defaultTimeFormat, Locale.getDefault())

    var radioGroupMyPoints: RadioGroup? = null
    var selectedIndex: Int = 0
    private lateinit var MyDiningInTableNumber: AppCompatEditText
    private lateinit var tvDiningIn: MaterialTextView

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

            tvDiningIn = findViewById(R.id.tvMyPointsDiningIn)

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

        MyDiningInTableNumber = findViewById(R.id.MyDiningInTableNumber)

        // toolbar title
        tvTitle.text = getString(R.string.my_bucket)

        clMyBucketTable.visibility = View.GONE
        constraintPickUp.visibility = View.VISIBLE

        cartViewModel.isLoading.observe(this, {
            try {
                if (it!!) {
                    Utils.showProgress(this@MyPointBucketActivity)
                } else {
                    Utils.hideProgress(this@MyPointBucketActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        tvDiningIn.setOnClickListener {
            Log.e("data", "nothing")

            if (!isDiningInSelected) {
                with(radioGroupMyPoints) { this?.clearCheck() }
            }

            tvDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (isDiningInSelected) R.drawable.ic_radio_button_unchecked else
                    R.drawable.ic_radio_button_checked,
                0,
                0,
                0
            )

            isDiningInSelected = !isDiningInSelected
            if (isDiningInSelected) {

                MyDiningInTableNumber.visibility = View.VISIBLE
                ivBucketPointClock.visibility = View.GONE
                tvBucketPointPickUpTime.visibility = View.GONE
                selectedIndex = 2
                callApiForPickUpTypeDignin(
                    "DINING_IN",
                    ""
                )
                //callApiForPickUpType("DINING_IN", "")
            }
        }


        val runnableEditTextForDignin = Runnable {

            if (System.currentTimeMillis() > ((lastEditText + delay) - 500)) {
                callApiForPickUpTypeDignin(
                    "DINING_IN",
                    MyDiningInTableNumber.text.toString().trim()
                )
            }
        }
        MyDiningInTableNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handlerEditText.removeCallbacks(runnableEditTextForDignin)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty()) {
                    lastEditText = System.currentTimeMillis()
                    handlerEditText.postDelayed(runnableEditTextForDignin, delay)
                }
            }
        })


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

        // setting up the text on picktime text
        radioGroupMyPoints = findViewById(R.id.radioGroupMyPoints)


        with(radioGroupMyPoints) {
            this?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbBucketPointSchedulePickup -> {
                        if (isDiningInSelected) {
                            tvDiningIn.performClick()
                        }

                        MyDiningInTableNumber.visibility = View.GONE
                        ivBucketPointClock.visibility = View.VISIBLE
                        tvBucketPointPickUpTime.visibility = View.VISIBLE
                        tvBucketPointPickUpTime.text = "Select Schedule Time"
                        selectedIndex = 0

                        if (tvBucketPointPickUpTime.text.toString() != "Select Schedule Time")
                            callApiForPickUpType("SCHEDULE_PICKUP", "")
                        else
                            callApiForPickUpType("SCHEDULE_PICKUP", "")
                    }
                    R.id.rbBucketPointPickupNow -> {

                        if (isDiningInSelected) {
                            tvDiningIn.performClick()
                        }
                        selectedIndex = 1
                        MyDiningInTableNumber.visibility = View.GONE
                        ivBucketPointClock.visibility = View.GONE
                        tvBucketPointPickUpTime.visibility = View.GONE
                        callApiForPickUpType("PICKUP_NOW", "")
                    }
                }
            })
        }


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
                        ).putExtra("myPoint", "yes").putExtra("serviceId", serviceId)
                            .putExtra("vendorTitle", serviceName)
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


//        tvDiningIn.setOnClickListener {
//            Log.e("data", "nothing")
//
//            if (!isDiningInSelected) {
//                with(radioGroupMyPoints) { this?.clearCheck() }
//            }
//
//            tvDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                if (isDiningInSelected) R.drawable.ic_radio_button_unchecked else
//                    R.drawable.ic_radio_button_checked,
//                0,
//                0,
//                0
//            )
//
//            isDiningInSelected = !isDiningInSelected
//            if (isDiningInSelected) {
//
//                MyDiningInTableNumber.visibility = View.VISIBLE
//                ivBucketPointClock.visibility = View.GONE
//                tvBucketPointPickUpTime.visibility = View.GONE
//                selectedIndex = 2
//                callApiForPickUpTypeDignin(
//                    "DINING_IN",
//                    ""
//                )
//                //callApiForPickUpType("DINING_IN", "")
//            }
//        }


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

    private fun callApiForPickUpTypeDignin(type: String, tableNo: String) {
        Log.e(type, tableNo)
        Log.e("type", type)
        Log.e("tableNo", tableNo)
        try {
            if (PubFun.isInternetConnection(this@MyPointBucketActivity)) {
                cartViewModel.pickup_type_dignin(type, tableNo, 1, 0, 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
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
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 500)
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
                    val currentTimess: String =
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    if (selectedIndex == 1 || selectedIndex == 2) {
                        cartViewModel.confirmOrder(
                            bucketDataList[0].serviceId.toString(),
                            menuArray,
                            subTotal.toString(),
                            totalAmt.toString(),
                            "",
                            "",
                            totalPoints.toString(),
                            edBucketPointSpecialInstaruction.text.toString().trim(),
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
        Log.e("PopulateCart", gson.toJson(res))
        try {
            if (res.data != null) {
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

                Log.e("POINTT", res.data!!.booking!!.type!!.toString())

                if (res.data!!.booking!!.type!!.toString()
                        .lowercase(Locale.getDefault()) == "dining in"
                ) {
                    tvDiningIn.performClick()
                } else {
                    radioGroupMyPoints?.apply {
                        check(
                            getChildAt(
                                if (res.data!!.booking!!.type!!.toString()
                                        .lowercase(Locale.getDefault()) == pickupNowType || res.data!!.booking!!.type!!.isEmpty()
                                ) 1 else 0
                            ).id
                        )
                    }
                }

                MyDiningInTableNumber.setText(res.data!!.booking!!.table_no!!.toString())

                // setting time +30 if pickup type is pickup now
                if (res.data!!.booking!!.type!!.toString()
                        .lowercase(Locale.getDefault()) == pickupNowType
                ) {
                    selectedIndex = 1
                    callApiForPickUpType("PICKUP_NOW", "")
                } else if (res.data!!.booking!!.type!!.toString()
                        .lowercase(Locale.getDefault()) == "schedule pickup"
                ) {
                    // setting up booking time
                    if (res.data!!.booking!!.bookingTime!!.isNotEmpty()) {
                        tvBucketPointPickUpTime.text = PubFun.parseDate(
                            res.data!!.booking!!.bookingTime!!,
                            Config.requestTimeFormat,
                            Config.defaultTimeFormat
                        )
                        callApiForPickUpType(
                            "SCHEDULE_PICKUP", PubFun.parseDate(
                                tvBucketPointPickUpTime.text.toString(),
                                Config.defaultTimeFormat,
                                Config.requestTimeFormat
                            ).toString()
                        )

                    }
                    selectedIndex = 0

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

                                var total = 0
                                for (i in 0 until bucketDataList.size) {
                                    total += bucketDataList[i].qty!!
                                    Log.e("PrintCountss", total.toString())
                                }
                                tvBucketPointMyCart.text =
                                    "My Cart (${total} ${if (total == 1) "Item" else "Items"})"


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
                        res.data!!.serviceDetails!!.offers!![0].menuId!!,
                        res.data!!.serviceDetails!!.offers!![0].couponCode!!.trim(),
                        res.data!!.serviceDetails!!.offers!![0].discountType!!,
                        res.data!!.serviceDetails!!.offers!![0].discountAmount!!,
                        res.data!!.serviceDetails!!.offers!![0].buyQty!!,
                        res.data!!.serviceDetails!!.offers!![0].getQty!!,
                    )
                    bucketDataList.add(bucketCartRes)

                    // Log.e("bucket List", gson.toJson(bucketDataList))
                }
                bucketMyPointAdapter.setBucketData(bucketDataList)
                rvBucketPointMyCart.apply {
                    layoutManager = LinearLayoutManager(this@MyPointBucketActivity)
                    adapter = bucketMyPointAdapter
                }
                calculateFooterSection(bucketDataList)

                cartTotal -= totalPoints
                tvBucketPointPoints.text = cartTotal.toString()
            }
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


            if (bucketDataList.size != 0) {

                var total = 0
                for (i in 0 until bucketDataList.size) {
                    total += bucketDataList[i].qty!!
                    Log.e("PrintCountss", total.toString())
                }
                tvBucketPointMyCart.text =
                    "My Cart (${total} ${if (total == 1) "Item" else "Items"})"
            }

            /*Sub Total*/
            tvBucketPointSubtotal.text = bucketDataList[0].currency!!.trim() +
                    if (subTotal == 0.0) "0.00" else numberFormat.format(subTotal)
            /*Total Tax*/
            tvBucketPointTax.text = bucketDataList[0].currency!!.trim() +
                    if (totalTax == 0.0) "0.00" else numberFormat.format(totalTax)
            for (i in bucketDataList.indices) {
                totalPoints += bucketDataList[i].qty * bucketDataList[i].point
            }
            /*Total*/
            totalAmt = totalTax + subTotal
            tvBucketPointTotalAmount.text = bucketDataList[0].currency!!.trim() +
                    if (totalAmt == 0.0) "0.00" else numberFormat.format(totalAmt)

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
                    // is selected type pick up now
                    val currentTimess: String =
                        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    if (selectedIndex != 0) {
                        callCheckRestaurantTimeApi(
                            serviceId.toInt(),
                            currentTimess
                        )
                        return
                    }
                    callCheckRestaurantTimeApi(
                        serviceId.toInt(),
                        PubFun.parseDate(
                            tvBucketPointPickUpTime.text.toString().trim(),
                            Config.defaultTimeFormat,
                            Config.requestTimeFormat
                        )!!

                    )
                    //only if check resustar success
                    //callApiForConfirmOrder()
                }
                tvMyPointsDiningIn -> {
//                    tvMyPointsDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        if (isMyPointDiningInSelected) R.drawable.ic_radio_button_unchecked
//                        else R.drawable.ic_radio_button_checked,
//                        0,
//                        0,
//                        0
//                    )
//                    isMyPointDiningInSelected = !isMyPointDiningInSelected

                    if (!isDiningInSelected) {
                        with(radioGroupMyPoints) { this?.clearCheck() }
                    }

                    tvDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (isDiningInSelected) R.drawable.ic_radio_button_unchecked else
                            R.drawable.ic_radio_button_checked,
                        0,
                        0,
                        0
                    )

                    isDiningInSelected = !isDiningInSelected
                    if (isDiningInSelected) {

                        MyDiningInTableNumber.visibility = View.VISIBLE
                        ivBucketPointClock.visibility = View.GONE
                        tvBucketPointPickUpTime.visibility = View.GONE
                        selectedIndex = 2
                        callApiForPickUpTypeDignin(
                            "DINING_IN",
                            ""
                        )
                        //callApiForPickUpType("DINING_IN", "")
                    }

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
                this@MyPointBucketActivity,
                2,
                { timePicker, selectedHour, selectedMinute ->
                    val mySelectedHrs: String = selectedHour.toString()
                    val mySelectedMin: String =
                        if (selectedMinute.toString().length == 1) "0$selectedMinute" else selectedMinute.toString()
                    val mySelectedTime: String = "$mySelectedHrs:$mySelectedMin"
                    tvBucketPointPickUpTime.text =
                        PubFun.parseDate(mySelectedTime, "HH:mm", Config.defaultTimeFormat)

                    callApiForPickUpType(
                        "SCHEDULE_PICKUP", PubFun.parseDate(
                            tvBucketPointPickUpTime.text.toString(),
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

}