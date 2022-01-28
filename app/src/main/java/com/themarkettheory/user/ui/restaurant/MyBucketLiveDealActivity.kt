package com.themarkettheory.user.ui.restaurant

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
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListenerCart
import com.themarkettheory.user.newmodels.bucketcart.CartList
import com.themarkettheory.user.newmodels.bucketcart.GetCartNewRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_bucket_live_deal.*
import kotlinx.android.synthetic.main.activity_mybucket_new.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MyBucketLiveDealActivity : BaseActivity(), View.OnClickListener,
    PaymentResultWithDataListener {
    /*View Model*/
    private lateinit var cartViewModel: CartViewModel
    private lateinit var vendorDetailViewModel: VendorDetailViewModel

    /*Adapter*/
    private lateinit var liveDealBucketNewAdapter: LiveDealBucketNewAdapter
    private var cartArrayList = ArrayList<CartList>()
    private lateinit var cartListRowData: CartList
    private var cartListRowPosition = 0

    /*General*/
    private val pickupNowType = "pickup now"
    private val pickUpNow = "PICKUP_NOW"
    private val schedulePickUp = "SCHEDULE_PICKUP"
    private var subTotal = 0.0
    private var totalTax = 0.0
    private var totalPoints = 0
    private var totalAmt = 0.0
    private val numberFormat: NumberFormat = DecimalFormat("#0.00")
    private var razorPayCurrentStr = ""
    private var isLiveDealDiningInSelected = false

    /*For Special Instruction Edittext*/
    private val delay: Long = 3000
    private var lastEditText: Long = 0
    private val handlerEditText = Handler(Looper.getMainLooper())
    var selectedIndex: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bucket_live_deal)
        /*Initialize View Model*/
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

        /*Set On Click Listener*/
        ivLiveDealBucketBack.setOnClickListener(this)
        tvLiveDealBucketAddMore.setOnClickListener(this)
        tvLiveDealBucketPickUpTime.setOnClickListener(this)
        btnLiveDealBucketConfirmYourOrder.setOnClickListener(this)
        tvLiveDealDiningIn.setOnClickListener(this)

        init()
    }

    override fun onResume() {
        super.onResume()
        try {
            callGetCart()
            callServiceDetail() //Added by Milan on 28-11-2021 for dining in option.
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                startActivity(Intent(this@MyBucketLiveDealActivity, LiveDealsActivity::class.java))
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callGetCart() {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                cartViewModel.get_cart(0, 0, 1, 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callServiceDetail() {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                vendorDetailViewModel.service_details(Config.vendorDetailServiceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForPickUpType(type: String, scheduleTime: String) {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                cartViewModel.pickup_type(type, scheduleTime, 0, 1, 0)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForSpecialInstruction(specialInstructionMsg: String) {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                cartViewModel.special_request("0", specialInstructionMsg, "0", 1)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddMenuCart(qty: String) {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                vendorDetailViewModel.menu_add_cart(
                    cartListRowData.serviceId.toString(),
                    cartListRowData.menuId.toString(),
                    "0",
                    qty,
                    "0",
                    1,
                    if (isLiveDealDiningInSelected) 1 else 0
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callCheckRestaurantTimeApi(id: Int, time: String) {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                vendorDetailViewModel.checkRestaurantTime(id, time)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiForConfirmOrder(paymentData: PaymentData) {
        try {
            if (PubFun.isInternetConnection(this@MyBucketLiveDealActivity)) {
                val menuArray = JSONArray()
                for (i in cartArrayList.indices) {
                    val menuObject = JSONObject()
                    menuObject.put("menu_id", cartArrayList[i].menu!!.id!!.toString())
                    menuObject.put("qty", cartArrayList[i].qty!!.toString())
                    menuObject.put("price", cartArrayList[i].menu!!.finalPrice!!.toString())
                    menuObject.put("points", "0")
                    menuObject.put("is_redeem", "0")
                    menuArray.put(menuObject)
                }

                val currentTimess: String = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
                if (selectedIndex == 1) {
                    cartViewModel.confirmOrder(
                        Config.vendorDetailServiceId,
                        menuArray,
                        subTotal.toString(),
                        totalAmt.toString(),
                        "0",
                        paymentData.paymentId.toString(),
                        "0",
                        edLiveDealBucketSpecialInstruction.text.toString().trim(),
                        currentTimess,
                        totalTax.toString(),
                        "0"
                    )
                    return
                }
                cartViewModel.confirmOrder(
                    Config.vendorDetailServiceId,
                    menuArray,
                    subTotal.toString(),
                    totalAmt.toString(),
                    "0",
                    paymentData.paymentId.toString(),
                    "0",
                    edLiveDealBucketSpecialInstruction.text.toString().trim(),
                    tvLiveDealBucketPickUpTime.text.toString().trim(),
                    totalTax.toString(),
                    "0"
                )
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
                ivLiveDealBucketBack -> onBackPressed()
                tvLiveDealBucketAddMore -> {
                    onBackPressed()
                }
                tvLiveDealBucketPickUpTime -> openRangeTimePickerDialog()
                btnLiveDealBucketConfirmYourOrder -> {
                    val currentTimess: String = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())

                    if (selectedIndex == 1) {
                        callCheckRestaurantTimeApi(
                            Config.vendorDetailServiceId.toInt(),
                            currentTimess
                        )
                        return
                    }

                    callCheckRestaurantTimeApi(
                        Config.vendorDetailServiceId.toInt(),
                        PubFun.parseDate(
                            tvLiveDealBucketPickUpTime.text.toString().trim(),
                            Config.defaultTimeFormat,
                            Config.requestTimeFormat
                        )!!
                    )
                }
                tvLiveDealDiningIn -> {
                    tvLiveDealDiningIn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (isLiveDealDiningInSelected) R.drawable.ic_radio_button_unchecked
                        else R.drawable.ic_radio_button_checked,
                        0,
                        0,
                        0
                    )
                    isLiveDealDiningInSelected = !isLiveDealDiningInSelected
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            //region Progress Bar Loading for Cart View Model
            cartViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@MyBucketLiveDealActivity)
                    } else {
                        Utils.hideProgress(this@MyBucketLiveDealActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Get Cart Response
            cartViewModel.responseGetCartNew.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> populateLiveDealBucket(it!!)
                }
            })
            //endregion

            //region Order Confirmation Response
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
                                this@MyBucketLiveDealActivity,
                                OrderConfirmationActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            })
            //endregion

            //region Progress Bar Loading for Vendor Detail View Model
            vendorDetailViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@MyBucketLiveDealActivity)
                    } else {
                        Utils.hideProgress(this@MyBucketLiveDealActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Service Detail for dining option
            vendorDetailViewModel.responseServiceDetail.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        tvLiveDealDiningIn.visibility = if (it.data!!.isTableBooking!! == 1)
                            View.VISIBLE else View.GONE
                    }
                }
            })
            //endregion

            //region Add / Update Cart Response
            vendorDetailViewModel.responseMenuAddCart.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> {
                            cartArrayList.removeAt(cartListRowPosition)
                            if (cartListRowData.qty!! > 0) {
                                cartArrayList.add(cartListRowData)
                            } else {
                                onBackPressed()
                            }
                            calculateFooterSection()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region Check Restaurant Time before Confirm Order Response
            vendorDetailViewModel.responseCheckRestaurantTime.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> startRazorPayment()
                }
            })
            //endregion

            //region Special Instructions
            val runnableSpecialInstruction = Runnable {
                if (System.currentTimeMillis() > ((lastEditText + delay) - 500)) {
                    callApiForSpecialInstruction(
                        edLiveDealBucketSpecialInstruction.text.toString().trim()
                    )
                }
            }
            edLiveDealBucketSpecialInstruction.addTextChangedListener {
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        handlerEditText.removeCallbacks(runnableSpecialInstruction)
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (s.toString().trim().isNotEmpty()) {
                            lastEditText = System.currentTimeMillis()
                            handlerEditText.postDelayed(runnableSpecialInstruction, delay)
                        }
                    }

                }
            }
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateLiveDealBucket(res: GetCartNewRes) {
        try {
       //     Log.e("API-DATa", gson.toJson(res))
            /*Razorpay Currency String*/
            if (res.data != null) {

                razorPayCurrentStr = res.data!!.serviceDetails!!.currencyStr!!.trim()

                /*Vendor Title*/
                tvLiveDealBucketVendorTitle.text = res.data!!.serviceDetails!!.title!!.trim()

                /*Vendor Address*/
                tvLiveDealBucketVendorAddress.text = res.data!!.serviceDetails!!.address!!.trim()

                /*My Cart Count*/
                tvLiveDealBucketCartItemCount.text =
                    "My Cart (${res.data!!.list!!.size} ${if (res.data!!.list!!.size == 1) "Item" else "Items"})"

                //region RecyclerView and Adapter
                val listener = object : ListClickListenerCart {
                    override fun onClickListener(
                        view: View,
                        pos: Int,
                        objects: Any,
                        isItemAdded: Boolean
                    ) {
                        try {
                            cartListRowData = objects as CartList
                            cartListRowPosition = pos

                            if (cartListRowData.qty!! > 0) {
                                callAddMenuCart(cartListRowData.qty.toString().trim())
                            } else {
                                callAddMenuCart("0")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                liveDealBucketNewAdapter =
                    LiveDealBucketNewAdapter(this@MyBucketLiveDealActivity, listener)
                rvLiveDealBucket.apply {
                    layoutManager = LinearLayoutManager(this@MyBucketLiveDealActivity)
                    adapter = liveDealBucketNewAdapter
                }
                cartArrayList = res.data!!.list!! as ArrayList<CartList>
                liveDealBucketNewAdapter.addLiveDealCart(cartArrayList)
                calculateFooterSection()
                //endregion

                //region Radio Group Selection
                with(radioGroupLiveDealBucket) {
                    this.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.rbLiveDealBucketSchedulePickup -> {
                                ivLiveDealBucketClock.visibility = View.VISIBLE
                                tvLiveDealBucketPickUpTime.visibility = View.VISIBLE
                                tvLiveDealBucketPickUpTime.text = "Select Schedule Time"
                                selectedIndex = 0

                                if (tvLiveDealBucketPickUpTime.text.toString() != "Select Schedule Time")
                                    callApiForPickUpType("SCHEDULE_PICKUP", "")
                                else
                                    callApiForPickUpType(
                                        "SCHEDULE_PICKUP",
                                        tvLiveDealBucketPickUpTime.text.toString().trim()
                                    )
                            }
                            else -> {
                                selectedIndex = 1

                                ivLiveDealBucketClock.visibility = View.GONE
                                tvLiveDealBucketPickUpTime.visibility = View.GONE
                                callApiForPickUpType(pickUpNow, "")
                            }
                        }
                    }
                }

                radioGroupLiveDealBucket.apply {
                    check(
                        getChildAt(
                            if (res.data!!.booking!!.type!!.lowercase(Locale.getDefault()) == pickupNowType
                                || res.data!!.booking!!.type!!.isEmpty()
                            )
                                1 else 0
                        ).id
                    )
                }
                //endregion

                /*Adding 30 min+ if radio type selection is pickup now */
                if (res.data!!.booking!!.type!!.lowercase(Locale.getDefault()) == pickupNowType) {
                    // add30MinutesToCurrentTime()
                    callApiForPickUpType("PICKUP_NOW", "")
                } else {
                    if (res.data!!.booking!!.bookingTime!!.isNotEmpty()) {
                        tvLiveDealBucketPickUpTime.text =
                            PubFun.parseDate(
                                res.data!!.booking!!.bookingTime!!.trim(),
                                Config.requestTimeFormat,
                                Config.defaultTimeFormat
                            )
                        callApiForPickUpType(
                            schedulePickUp,
                            tvLiveDealBucketPickUpTime.text.toString().trim()
                        )
                    } else {

                    }
                }

                /*Special Instruction*/
                edLiveDealBucketSpecialInstruction.setText(
                    res.data!!.booking!!.specialInstruction!!.trim()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateFooterSection() {
        try {
            subTotal = 0.0
            totalTax = 0.0
            totalAmt = 0.0
            totalPoints = 0


            for (i in cartArrayList.indices) {
                /*Sub Total*/
                subTotal += cartArrayList[i].qty!!.toDouble() * cartArrayList[i].menu!!.finalPrice!!

                /*Total Tax*/
                //  totalTax += (subTotal * cartArrayList[i].menu!!.tax!!.toDouble())
                totalPoints += cartArrayList[i].qty!! * cartArrayList[i].menu!!.point!!

            }
            totalTax += (subTotal * cartArrayList[0].menu!!.tax!!.toDouble()) / 100


            /*Sub Total*/
            tvLiveDealBucketSubTotal.text = cartArrayList[0].menu!!.currency!!.trim() +
                    if (subTotal == 0.0) "0.00" else numberFormat.format(subTotal)

            /*Total Tax*/
            tvLiveDealBucketTax.text = cartArrayList[0].menu!!.currency!!.trim() +
                    if (totalTax == 0.0) "0.00" else numberFormat.format(totalTax)

            tvLiveBucketTotalOrderPoints.text = totalPoints.toString()

            /*Total Amount*/
            totalAmt = subTotal + totalTax
            tvLiveDealBucketTotal.text = cartArrayList[0].menu!!.currency!!.trim() +
                    if (totalAmt == 0.0) "0.00" else numberFormat.format(totalAmt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun add30MinutesToCurrentTime() {
        try {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, 30)
            tvLiveDealBucketPickUpTime.text = PubFun.parseDate(
                cal.time.toString(),
                "EEE MMM dd HH:mm:ss",
                Config.defaultTimeFormat
            )
            callApiForPickUpType(pickUpNow, "")
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
                this@MyBucketLiveDealActivity,
                2,
                { timePicker, selectedHour, selectedMinute ->
                    val mySelectedHrs: String = selectedHour.toString()
                    val mySelectedMin: String =
                        if (selectedMinute.toString().length == 1) "0$selectedMinute" else selectedMinute.toString()
                    val mySelectedTime = "$mySelectedHrs:$mySelectedMin"
                    tvLiveDealBucketPickUpTime.text =
                        PubFun.parseDate(mySelectedTime, "HH:mm", Config.defaultTimeFormat)
                    callApiForPickUpType(
                        schedulePickUp,
                        tvLiveDealBucketPickUpTime.text.toString().trim()
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

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@MyBucketLiveDealActivity)
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

    fun startRazorPayment() {

        val activity = this

        val co = Checkout()
        co.setKeyID(getString(R.string.razorpay_live_key))
        val currencyStr = razorPayCurrentStr

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
        TODO("Not yet implemented")
    }
}