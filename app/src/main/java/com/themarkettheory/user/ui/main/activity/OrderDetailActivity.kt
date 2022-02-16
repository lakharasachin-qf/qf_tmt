package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.coupons.NewOfferListData
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingData
import com.themarkettheory.user.newmodels.orderconfirmation.GetNewOrderConfirmRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.dialog.dialogshare.DialogShare
import com.themarkettheory.user.ui.main.adapter.OrderDetailAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketCartRes
import com.themarkettheory.user.viewmodel.CartViewModel
import kotlinx.android.synthetic.main.activity_order_confirmation_new.*
import kotlinx.android.synthetic.main.activity_order_detail.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailActivity : BaseActivity(), View.OnClickListener {
    //General
    var orderId = ""

    //ToolBar Components
    private lateinit var viewToolBarOrderDetail: View
    private lateinit var ivBackToolBarOrderDetail: ShapeableImageView
    private lateinit var tvTitleToolBarOrderDetail: MaterialTextView

    private val couponPercentage = 1
    private val couponFlat = 2
    private val couponBuyGet = 3
    var bucketDataList = ArrayList<MyBucketCartRes>()

    //ViewModel
    lateinit var cartViewModel: CartViewModel
    lateinit var selectedOrder: MyTableBookingData

    //Booking Status Type
    private val newOrderType = 0
    private val approvedType = 1
    private val declineType = 2
    private val completedType = 3
    private val cancelledType = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        try {
            viewToolBarOrderDetail = findViewById(R.id.myOrderDetailToolBar)
            ivBackToolBarOrderDetail = viewToolBarOrderDetail.findViewById(R.id.ivBack)
            tvTitleToolBarOrderDetail = viewToolBarOrderDetail.findViewById(R.id.tvTitle)

            //Button SetOnClickListener
            ivBackToolBarOrderDetail.setOnClickListener(this)
            btnOrderDetailCancelOrder.setOnClickListener(this)
            btnOrderInviteDetail.setOnClickListener(this)
            if (intent.hasExtra("data")) {
                selectedOrder = gson.fromJson(
                    intent.getStringExtra("data"),
                    MyTableBookingData::class.java
                )
            }
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@OrderDetailActivity)) {
                super.onBackPressed()
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
                ivBackToolBarOrderDetail -> onBackPressed()
                btnOrderDetailCancelOrder -> openCancelDialogConfirmation()
                btnOrderInviteDetail -> openSharedDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            orderId = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbOrderId)!!
            if (PubFun.isInternetConnection(this@OrderDetailActivity)) {
                cartViewModel.my_order_details(orderId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            //ToolBar Title
            tvTitleToolBarOrderDetail.text = getString(R.string.order_detail)

            //Initialize View Model
            cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

            //ProgressBar
            cartViewModel.isLoading.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this)
                    } else {
                        Utils.hideProgress(this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            //My Order Details Response
            cartViewModel.responseOrderDetail.observe(this, {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> populateOrderDetails(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            //Cancel Order Response
            cartViewModel.responseCancelOrder.observe(this, {
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
            e.printStackTrace()
        }
    }


    var numFormatNew: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    @SuppressLint("SetTextI18n")
    private fun populateOrderDetails(res: GetNewOrderConfirmRes) {
        try {

            Log.e("Order Details:", res.toString())
            Log.e("Order orderType:", gson.toJson(res.data!!.orderType!!))
            Log.e("Order table_no:", gson.toJson(res.data!!.table_no!!))
            //Order Number
            tvOrderDetailOrderNumber.text = "#${res.data!!.orderNumber!!.trim()}"

            //Vendor Title
            tvOrderDetailVendorTitle.text = res.data!!.serviceDetails!!.title!!.trim()
            tvOrderDetailOrderTokenLAbel.text = res.data!!.orderToken!!.trim()

            //Vendor Address
            tvOrderDetailAddress.text = res.data!!.serviceDetails!!.address!!.trim()


            if (res.data!!.orderType!!.equals("DINING IN")) {
                if (res.data!!.table_no!!.isNotEmpty())
                    tvOrderConfirmationType.text =
                        "Dining In - Table No: " + res.data!!.table_no!!
                else
                    tvOrderConfirmationType.text =
                        "Dining In"// + res.data!!.table_no!!
                //tvOrderDetailOrderType.text = "Dining In - Table No: " + res.data!!.table_no!!
            }

            if (res.data!!.orderType!!.lowercase() == "schedule pickup" ||
                res.data!!.orderType!!.lowercase() == "pickup now" ||
                res.data!!.orderType!!.lowercase() == "invite user"
            ) {
                tvOrderDetailOrderType.text = PubFun.toCamelCase(res.data!!.orderType!!.trim())
            } else if (res.data!!.orderType!! == "QR") {
                tvOrderDetailOrderType.text =
                    res.data!!.orderType!!.trim() + " code Table No: " + res.data!!.table_no!!.trim()
            } else {
                tvOrderDetailOrderType.text =
                    "Table For " + selectedOrder.total_person + " Person"
                btnOrderInviteDetail.visibility = View.VISIBLE
                shareIconDetails.visibility = View.VISIBLE
            }

            //Order Date
            tvOrderDetailDateText.text = PubFun.parseDate(
                res.data!!.date!!,
                Config.requestDateFormat, Config.defaultDateFormat
            )

            Log.e("order_time", gson.toJson(res.data!!.time!!))
            //Order Time
            tvOrderDetailTextValue.text = PubFun.parseDate(
                res.data!!.time!!,
                Config.requestTimeFormat, Config.defaultTimeFormat
            )

            if (res.data!!.couponDetail != null && res.data!!.couponDetail!!.couponCode!!.isNotEmpty()) {
                tvOrderDetailMyBuckeCouponText.visibility = View.VISIBLE
                tvOrderDetailCouponDiscount.visibility = View.VISIBLE

                val subTotal = 0.0
                var totalTax = 0.0
                var totalPoints = 0
                var totalAmt = 0.0
                var discountCouponTotal = 0.0
                val discountAmt = res.data!!.couponDetail!!.discountAmount
                if (res.data!!.couponDetail!!.discountType == couponPercentage) {
                    discountCouponTotal = (subTotal * discountAmt!!) / 100
                    if (res.data!!.couponDetail!!.maxDiscount != 0 && discountCouponTotal > res.data!!.couponDetail!!.maxDiscount!!) {
                        discountCouponTotal = res.data!!.couponDetail!!.maxDiscount!!.toDouble()
                    }
                } else if (res.data!!.couponDetail!!.discountType == couponFlat) {
                    discountCouponTotal = discountAmt!!.toDouble()

                } else {
                    val offerData = Config.gson.fromJson(
                        myRoomDatabase.daoConfig()
                            .selectConfigTableByField(Config.dbOfferListResRowData),
                        NewOfferListData::class.java
                    )
                    Log.e("offerData", gson.toJson(offerData) + "ss")
                    for (i in res.data!!.menuDetails!!.indices) {
                        if (res.data!!.menuDetails!![i].menuId == offerData.menuId) {
                            val menuPrice = res.data!!.menuDetails!![i].price!!.toDouble()
                            discountCouponTotal = menuPrice * res.data!!.couponDetail!!.getQty!!
                        }
                    }
                }



                tvOrderDetailCouponDiscount.text =
                    "${
                        numFormatNew.format(
                            discountCouponTotal // res.data!!.couponDetail!!.discountAmount!!
                        )
                    }"
            } else {
                tvOrderDetailMyBuckeCouponText.visibility = View.GONE
                tvOrderDetailCouponDiscount.visibility = View.GONE
            }


            //Order Status
            tvOrderDetailStatus.text = when (res.data!!.status!!) {
                newOrderType -> "Pending" //0
                approvedType -> "Approved" //1
                declineType -> "Declined" //2
                completedType -> "Completed" //3
                else -> "Cancelled" //4
            }.uppercase(Locale.getDefault())
            tvOrderDetailStatus.background = ContextCompat.getDrawable(
                this@OrderDetailActivity,
                when (res.data!!.status!!) {
                    newOrderType -> R.drawable.bg_my_pumpkin_circle
                    approvedType -> R.drawable.bg_my_green_circle
                    completedType -> R.drawable.bg_my_robbins_egg_blue_circle
                    else -> R.drawable.bg_my_sunset_orange_circle
                }
            )

            if (bucketDataList.size != 0) {
                var total = 0
                for (i in 0 until bucketDataList.size) {
                    total += bucketDataList[i].qty
                    Log.e("bucketDataList", total.toString())

                    tvOrderDetailMyCart.text = "My Cart (${total} item)"

                }
            } else {
                //Total Cart Items
                tvOrderDetailMyCart.text = "My Cart (${res.data!!.menuDetails!!.size} item)"

            }

            //RecyclerView
            val menuList = ArrayList<MyBucketCartRes>()
            for (i in res.data!!.menuDetails!!.indices) {
                menuList.add(
                    MyBucketCartRes(
                        res.data!!.serviceDetails!!.id!!,
                        res.data!!.serviceDetails!!.title!!.trim(),
                        res.data!!.menuDetails!![i].menu!!.foodType!!,
                        res.data!!.menuDetails!![i].menu!!.isSpicy!!,
                        res.data!!.menuDetails!![i].menu!!.id!!,
                        res.data!!.menuDetails!![i].menu!!.title!!.trim(),
                        res.data!!.menuDetails!![i].menu!!.categoryName!!.trim(),
                        if (res.data!!.menuDetails!![i].menu!!.finalPrice!!.isEmpty())
                            "0.0".toDouble() else
                            res.data!!.menuDetails!![i].menu!!.finalPrice!!.toDouble(),
                        res.data!!.menuDetails!![i].menu!!.actualPrice!!.toDouble(),
                        if (res.data!!.paymentId!!.isEmpty())
                            res.data!!.menuDetails!![i].menu!!.redeemPoints!!
                        else res.data!!.menuDetails!![i].menu!!.point!!,
                        res.data!!.menuDetails!![i].menu!!.preparingTime!!.trim(),
                        res.data!!.menuDetails!![i].qty!!,
                        res.data!!.menuDetails!![i].menu!!.tax!!.toDouble(),
                        res.data!!.menuDetails!![i].menu!!.currency!!.trim(),
                        "",
                        0,
                        "",
                        0,
                        0,
                        0,
                        0
                    )
                )
            }
            if (menuList.isNotEmpty()) {
                //RecyclerView for Menu List
                val orderDetailAdapter = OrderDetailAdapter()
                orderDetailAdapter.setOrderRes(res)
                orderDetailAdapter.setBucketData(menuList)
                rvOrderDetailMyCart.apply {
                    layoutManager = LinearLayoutManager(this@OrderDetailActivity)
                    adapter = orderDetailAdapter
                }
            }

            //Currency Symbol
            val currencySymbol = res.data!!.serviceDetails!!.currency!!.trim()

            //SubTotal
            tvOrderDetailSubtotal.text = "${currencySymbol}${res.data!!.subtotal!!}"

            //Tax
            tvOrderDetailTax.text = "${currencySymbol}${res.data!!.tax!!}"

            //Total
            tvOrderDetailTotalAmount.text = "${currencySymbol}${res.data!!.total!!}"

            //Total Points
            tvOrderDetailTotalOrderPoints.text = res.data!!.points!!.toString().trim()



            if (res.data!!.subtotal!!.toString() == "0.00") {
                tvOrderDetailOrderPoint.text = "Order Redeem Points"
            } else {
                tvOrderDetailOrderPoint.text = "Order Points"
            }

            //Cancel Button Visibility
            btnOrderDetailCancelOrder.visibility =
                when (res.data!!.status!!) {
                    approvedType, cancelledType, completedType, declineType -> View.GONE
                    else -> View.VISIBLE
                }

            prepareDataToShare(res);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelOrder(id: String, reason: String) {
        if (PubFun.isInternetConnection(this)) {
            cartViewModel.cancelOrder(id, reason)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openCancelDialogConfirmation() {
        try {
            val cancelDialog = DialogToast(this@OrderDetailActivity)
            cancelDialog.show()
            cancelDialog.holder?.let {
                it.tvTitle.text = "Order Cancel Confirmation"
                it.tvMessage.text = "Are you sure you want to cancel this order ?"
                it.btnDialogLogout.text = "Yes"
                it.btnDialogCancel.text = "No"
                it.btnDialogLogout.setOnClickListener {
                    cancelDialog.dismiss()
                    cancelOrder(orderId, "")
                }
                it.btnDialogCancel.setOnClickListener {
                    cancelDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@OrderDetailActivity)
            val myDialog = DialogToast(this@OrderDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.order_detail)
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
                                postDelayed(this, 500)
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


    var shareingStringData: String = ""
    var restarurantName: String = ""
    var address: String = ""
    var token: String = ""
    var orderIdText: String = ""
    var orderType: String = ""
    var date: String = ""
    var time: String = ""

    //facebook
    private lateinit var dialogShare: DialogShare

    private lateinit var shareDialog: ShareDialog
    private lateinit var callbackManager: CallbackManager
    private fun prepareDataToShare(res: GetNewOrderConfirmRes) {
        try {
            restarurantName = res.data!!.serviceDetails!!.title!!
            address = res.data!!.serviceDetails!!.address!!
             orderId = res.data!!.id!!.toString().trim()
            orderId = "#${res.data!!.orderNumber!!.trim()}"
            token = res.data!!.orderToken!!
            orderType = PubFun.toCamelCase(res.data!!.orderType!!.trim())!!
            orderType = "Table For " + res.data!!.table_no!! + " Person"

            date = PubFun.parseDate(
                res.data!!.date,
                Config.requestDateFormat,
                Config.defaultDateFormat
            ).toString()
            time = PubFun.parseDate(
                res.data!!.time!!,
                Config.requestTimeFormat,
                Config.defaultTimeFormat
            ).toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSharedDialog() {
        try {
            dialogShare = DialogShare(this@OrderDetailActivity)
            dialogShare.show()

            // sharing on facebook
            dialogShare.holder!!.ivFacebook.setOnClickListener {
                shareOnFacebook()
            }
            //sharing on whatsapp
            dialogShare.holder!!.ivWhatsApp.setOnClickListener {
                shareOnWhatsapp()
            }
            // sharing on email
            dialogShare.holder!!.ivEmail.setOnClickListener {
                shareOnEmail()
            }
            //sharing on text message app
            dialogShare.holder!!.ivText.setOnClickListener {
                shareOnMessage()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareOnFacebook() {
        try {
            shareingStringData =
                "Restaurant Details:\nName: $restarurantName\nAddress: $address" +
                        "\n\nOrder Detail:\nToken Number: ${token}\n" +
                        "Order Number:  $orderId\nOrder Type:  $orderType\n" +
                        "Date: $date\nTime: $time"

            callbackManager = CallbackManager.Factory.create();
            shareDialog = ShareDialog(this);

            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .setQuote(shareingStringData)
                    .build()
                shareDialog.show(linkContent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun shareOnWhatsapp() {
        try {
            shareingStringData =
                "*Restaurant Details:*\n*Name:* $restarurantName\n*Address:* $address" +
                        "\n\n*Order Detail:*\n*Token Number:* ${token}\n*Order Number:* " +
                        " $orderId\n*Order Type:* $orderType\n" +
                        "*Date: * $date\n*Time: * $time"
            val sendIntent = Intent()
            sendIntent.setPackage("com.whatsapp")
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareingStringData)
            sendIntent.type = "text/html"
            startActivity(sendIntent)
            dialogShare.dismiss()
//                finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareOnEmail() {
        try {
            shareingStringData =
                "<b>Restaurant Details:</b><br/>Name: $restarurantName<br/>Address: $address" +
                        "<br/><br/>Order Detail:<br/>Token Number: $token<br/>Order Number: $orderId<br/>Order Type: ${orderType}<br/>" +
                        "Date: $date<br/>Time: $time"

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(StringBuilder().append(shareingStringData).toString())
            )
            sendIntent.type = "text/html"
            sendIntent.setType("message/rfc822")
            startActivity(Intent.createChooser(sendIntent, "Email:"))
            dialogShare.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun shareOnMessage() {
        try {
            shareingStringData =
                "Restaurant Details: \nName: ${restarurantName}\nAddress: $address" +
                        "\n\nOrder Detail: \nToken Number: ${token}\nOrder Number: " +
                        "$orderId\nOrder Type: $orderType\n" +
                        "Date: $date\nTime: $time"

            val defaultSmsPackageName =
                Telephony.Sms.getDefaultSmsPackage(this)
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareingStringData.trim())
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName)
            }
            startActivity(sendIntent)
            dialogShare.dismiss()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}