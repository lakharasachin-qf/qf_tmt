package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.booking.bookingdetails.NewBookingDetailsRes
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingData
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_my_table_booking_detail.*
import kotlinx.android.synthetic.main.activity_order_confirmation_new.*

class MyTableBookingDetailActivity : BaseActivity(), View.OnClickListener {
    //View Model
    private lateinit var menuViewModel: MenuViewModel

    //Components
    private lateinit var viewToolBarMyBookingDetail: View
    private lateinit var viewToolBarMyBookingDetailBackButton: ShapeableImageView
    private lateinit var viewToolMyBookingDetailBarTitle: MaterialTextView

    //    private lateinit var eventTitle: TextView
//    private lateinit var eventMessage: TextView
//    var eventTitleName: String? = ""
//    var eventMessages: String? = ""


    //Firebase Notification
    private lateinit var mMessageReceiver: BroadcastReceiver

    //General
    var bookingId = ""
    var serviceId = ""
    var serviceName = ""
    var eventName = ""
    var eventMessage = ""
    lateinit var tableBookingData: MyTableBookingData

    //Booking Status Type
    private val newOrderType = 0
    private val approvedType = 1
    private val declineType = 2
    private val completedType = 3
    private val cancelledType = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_table_booking_detail)
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {

        if (PubFun.isInternetConnection(this@MyTableBookingDetailActivity)) {

            if (intent.hasExtra("bookFragment")) {

                startActivity(Intent(this@MyTableBookingDetailActivity, MainActivity::class.java))
                finishAffinity()
                return
            }
            if (Config.isBookingDetailOpeningFrom == Config.isBookingDetailOpeningFromMyBookingList) {

                startActivity(
                    Intent(
                        this@MyTableBookingDetailActivity,
                        MyTableBookingsActivity::class.java
                    )
                )
            } else {

                Config.isMenuFragmentComingFrom = Config.isMenuFragmentComingFromBucketCart
                startActivity(
                    Intent(this@MyTableBookingDetailActivity, VendorDetailActivity::class.java)
                        .putExtra("category", "1")
                        .putExtra("serviceId", serviceId)
                        .putExtra("vendorTitle", serviceName)
                        .putExtra("selectPosition", 1)
                )
            }

            Config.isPreOrder = false
            finish()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                viewToolBarMyBookingDetailBackButton -> onBackPressed()
                btnMyTableBookingDetailPreOrder -> navigateToMenuFragment()
                btnMyTableBookingDetailCancelBooking -> callCancelTableApi()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            //ToolBar
            viewToolBarMyBookingDetail = findViewById(R.id.toolBarMyTableBookingDetail)
            viewToolBarMyBookingDetailBackButton =
                viewToolBarMyBookingDetail.findViewById(R.id.ivBack)
            viewToolMyBookingDetailBarTitle = viewToolBarMyBookingDetail.findViewById(R.id.tvTitle)


            //Toolbar Title
            viewToolMyBookingDetailBarTitle.text = getString(R.string.my_booking)

            //Firebase Notification

            mMessageReceiver = object : BroadcastReceiver() {
                @SuppressLint("LongLogTag")
                override fun onReceive(context: Context?, intent: Intent) {

                    val message = intent.getStringExtra("message")
                    val notification_type = intent.getStringExtra("notification_type")
                    Log.e("MyTableBookingActivity notification_type:", notification_type.toString())
                    Log.e("ON Activity message:", message.toString())
                    if (message != null) {
                        if (intent.getStringExtra("notification_type")!! == "8") {
                            // BOOKING_ACCEPT
                            tvMyTableBookingDetailStatus.text = "Approved"
                            tvMyTableBookingDetailStatus.setTextColor(
                                ContextCompat.getColor(
                                    this@MyTableBookingDetailActivity,
                                    R.color.myGreen
                                )
                            )
                            btnMyTableBookingDetailPreOrder.visibility = View.VISIBLE

                        }
                        if (intent.getStringExtra("notification_type")!! == "4") {
                            //  BOOKING_REJECT
                            tvMyTableBookingDetailStatus.text = "Declined"
                            tvMyTableBookingDetailStatus.setTextColor(
                                ContextCompat.getColor(
                                    this@MyTableBookingDetailActivity,
                                    R.color.mySunsetOrange
                                )
                            )
                            showMsgDialogAndProceedForNotification(true, message)

                        }
                    }
                }
            }
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, IntentFilter("requests_count"))


            // PreOrder Button
            btnMyTableBookingDetailPreOrder.visibility = View.GONE

            //Button Click Listener
            viewToolBarMyBookingDetailBackButton.setOnClickListener(this)
            btnMyTableBookingDetailPreOrder.setOnClickListener(this)
            btnMyTableBookingDetailCancelBooking.setOnClickListener(this)

            //Initial View Model
            menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

            //Getting Booking ID
            bookingId =
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbTableBookingId)!!

            Log.e("book Id", bookingId)

            callMyBookingDetailApi()

            //ProgressBar
            menuViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@MyTableBookingDetailActivity)
                    } else {
                        Utils.hideProgress(this@MyTableBookingDetailActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            //My Booking Detail Response
            menuViewModel.responseBookingDetail.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> populateTableBookingDetails(it)
                    }
                } catch (e: Exception) {
                    menuViewModel.isLoading.value = false
                    e.printStackTrace()
                }
            })

            //Cancel Table Booking Response
            menuViewModel.responseCancelOrder.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> onBackPressed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            menuViewModel.isLoading.value = false
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            //if (mMessageReceiver != null)
            unregisterReceiver(mMessageReceiver)
        } catch (e: java.lang.Exception) {
            Log.e(" Releasing receivers-", "Data " + e.message)
        }
    }

    private fun showMsgDialogAndProceedForNotification(isOrderAccepted: Boolean, msg: String) {
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
                                if (isOrderAccepted) {
                                    btnMyTableBookingDetailPreOrder.visibility = View.GONE
                                    btnMyTableBookingDetailCancelBooking.visibility = View.GONE
                                    startActivity(
                                        Intent(
                                            this@MyTableBookingDetailActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finishAffinity()
                                    return
                                }
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

    private fun dataLoad() {
        //ProgressBar
        menuViewModel.isLoading.observe(this, {
            try {
                if (it!!) {
                    Utils.showProgress(this@MyTableBookingDetailActivity)
                } else {
                    Utils.hideProgress(this@MyTableBookingDetailActivity)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        //My Booking Detail Response
        menuViewModel.responseBookingDetail.observe(this, {
            try {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> populateTableBookingDetails(it)
                }
            } catch (e: Exception) {
                menuViewModel.isLoading.value = false
                e.printStackTrace()
            }
        })
    }

    private fun callMyBookingDetailApi() {
        menuViewModel.mybooking_details(bookingId)
    }

    @SuppressLint("SetTextI18n")
    private fun callCancelTableApi() {
        try {
            val dialogConfirmation = DialogToast(this@MyTableBookingDetailActivity)
            dialogConfirmation.apply {
                show()
                holder!!.tvMessage.text = "Are you sure want to cancel this booking ?"
                holder!!.tvTitle.text = "Confirmation"
                holder!!.btnDialogLogout.text = "Yes"
                holder!!.btnDialogCancel.text = "No"
                holder!!.btnDialogLogout.setOnClickListener {
                    this.dismiss()
                    if (PubFun.isInternetConnection(this@MyTableBookingDetailActivity)) {
                        menuViewModel.cancel_table(bookingId)
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
                holder!!.btnDialogCancel.setOnClickListener { this.dismiss() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToMenuFragment() {
        try {
            if (PubFun.isInternetConnection(this@MyTableBookingDetailActivity)) {
                Config.isMenuFragmentComingFrom = Config.isMenuFragmentComingFromBookingTable
                Config.isVideoPlay = false
                Config.isPreOrder = true
                startActivity(
                    Intent(this@MyTableBookingDetailActivity, VendorDetailActivity::class.java)
                        .putExtra("category", "1")
                        .putExtra("serviceId", serviceId)
                        .putExtra("vendorTitle", serviceName)
                        .putExtra("selectPosition", 2)
                )
                finish()

                Log.e("category", "1")
                Log.e("serviceId", serviceId)
                Log.e("vendorTitle", serviceName)
                Log.e("selectPosition", "2")
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateTableBookingDetails(res: NewBookingDetailsRes) {
        try {
            if (res.data != null) {
                //Capturing Booking Response into roomDB
                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbNewBookingDetailRes)
                    insertConfigTable(
                        TableConfig(
                            Config.dbNewBookingDetailRes,
                            gson.toJson(res)
                        )
                    )
                }

                //  Log.e("occasion_id:", gson.toJson(res.data!!))


                if (res.data!!.occasion_name!!.toString().trim().isNotEmpty()) {
                    occationTitle.visibility = View.VISIBLE
                    occationData.visibility = View.VISIBLE
                    view1.visibility = View.VISIBLE
                    eventName = res.data!!.occasion_name!!.toString().trim()
                    occationData.text = eventName
                } else {
                    occationData.visibility = View.GONE
                    occationTitle.visibility = View.GONE
                    view1.visibility = View.GONE
                }

                if (res.data!!.special_request!!.toString().trim().isNotEmpty()) {
                    eventMessage = res.data!!.special_request!!.toString().trim()
                    eventRequestMessage.text = eventMessage
                    requestMessage.visibility = View.VISIBLE
                    eventRequestMessage.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                } else {
                    requestMessage.visibility = View.GONE
                    eventRequestMessage.visibility = View.GONE
                    view.visibility = View.GONE
                }

//                if (res.data!!.occasion_name!!.toString().trim()
//                        .isEmpty() && res.data!!.special_request!!.toString().trim().isEmpty()
//                ) {
////                    view.visibility = View.GONE
////                    view1.visibility = View.GONE
//                }

                //Service ID
                serviceId = res.data!!.service_id!!.toString().trim()
                //Service Name
                serviceName = res.data!!.title!!.trim()
                //Restaurant Title
                tvMyTableBookingDetailServiceTitle.text =
                    serviceName
                //Restaurant Address
                tvMyTableBookingDetailServiceAddress.text =
                    res.data!!.address!!.trim()
                //Table for X person
                tvMyTableBookingDetailTable.text =
                    "Table for ${res.data!!.total_person} ${if (res.data!!.total_person == 1) "People" else "Persons"}"
                //Booking Date
                tvMyTableBookingDetailDate.text =
                    PubFun.parseDate(
                        res.data!!.booking_date!!.trim(),
                        Config.requestDateFormat,
                        Config.defaultDateFormat
                    )
                //Booking Time
                tvMyTableBookingDetailTime.text =
                    res.data!!.booking_time!!.trim()

                //region Booking Status
                tvMyTableBookingDetailStatus.text =
                    when (res.data!!.status!!) {
                        newOrderType -> "Pending" //0
                        approvedType -> "Approved" //1
                        declineType -> "Declined" //2
                        completedType -> "Completed" //3
                        else -> "Cancelled" //4
                    }
                tvMyTableBookingDetailStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyTableBookingDetailActivity,
                        when (res.data!!.status!!) {
                            newOrderType -> R.color.myPumpkin
                            approvedType -> R.color.myGreen
                            completedType -> R.color.myRobinsEggBlue
                            else -> R.color.mySunsetOrange
                        }
                    )
                )
                //endregion

                //region Pre Order Button Visibility
                btnMyTableBookingDetailPreOrder.visibility =
                    if (res.data!!.status!! == 1) View.VISIBLE else View.GONE
                //endregion

                //region Cancel Booking Visibility
                btnMyTableBookingDetailCancelBooking.visibility =
                    when (res.data!!.status!!) {
                        0, 1 -> View.VISIBLE
                        else -> View.GONE
                    }
                //endregion
            }
        } catch (e: Exception) {
            menuViewModel.isLoading.value = false
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@MyTableBookingDetailActivity)
            val myDialog = DialogToast(this@MyTableBookingDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_booking)
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
}