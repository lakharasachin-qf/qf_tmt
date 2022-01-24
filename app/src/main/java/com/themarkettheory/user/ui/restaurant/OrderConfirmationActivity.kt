package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import android.provider.Telephony
import android.text.Html
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.orderconfirmation.GetNewOrderConfirmRes
import com.themarkettheory.user.newmodels.orderconfirmation.MenuDetail
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.dialog.dialogshare.DialogShare
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import kotlinx.android.synthetic.main.activity_mybucket_new.*
import kotlinx.android.synthetic.main.activity_order_confirmation.*
import kotlinx.android.synthetic.main.activity_order_confirmation_new.*
import kotlinx.android.synthetic.main.toolbar.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderConfirmationActivity : BaseActivity(), View.OnClickListener {
    private lateinit var cartViewModel: CartViewModel

    private lateinit var adapterOrderConfirmation: OrderConfirmationAdapter
    private val orderConfirmationList = ArrayList<MenuDetail>()
    private lateinit var dialogShare: DialogShare
    private lateinit var mMessageReceiver: BroadcastReceiver

    // General
    var orderId: String = ""
    private val numberFormat: NumberFormat = DecimalFormat("#0.00")
    var shareingStringData: String = ""
    var restarurantName: String = ""
    var address: String = ""
    var orderIdText: String = ""
    var orderType: String = ""
    var date: String = ""
    var time: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    //facebook
    private lateinit var shareDialog: ShareDialog
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation_new)
        try {
            init()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (PubFun.isInternetConnection(this@OrderConfirmationActivity)) {
                //Check if lateinit var is initialized if yes then & then only dismiss the dialog
                if (::dialogShare.isInitialized) dialogShare.dismiss()
                if (!Config.isMyPointClickedFromHome) {
                    Config.bottomBarClickedName = Config.menuBottomBarClick
                }
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //finish()
    }

    override fun onPause() {
        super.onPause()
        try {
            if (runnable != null) {
                handler.removeCallbacks(runnable)
                handler.removeCallbacksAndMessages(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            //Getting Order Id
            orderId = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbOrderId)!!
            //Initial View Model
            cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
            // calling api for getting orders
            if (PubFun.isInternetConnection(this)) {
                cartViewModel.my_order_details(orderId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

            // response
            getResponse()
            //Components Click Listner
            mMessageReceiver = object : BroadcastReceiver() {
                @SuppressLint("LongLogTag")
                override fun onReceive(context: Context?, intent: Intent) {

                    val message = intent.getStringExtra("message")
                    val notification_type = intent.getStringExtra("notification_type")
                    Log.e("ON Activity notification_type:", notification_type.toString())
                    Log.e("ON Activity message:", message.toString())
                    if (message != null) {
                        if (intent.getStringExtra("notification_type")
                                .equals("5")
                        ) { // order acccepted
                            showMsgDialogAndProceedForNotification(true, message)
                        }
                        if (intent.getStringExtra("notification_type")
                                .equals("6")
                        ) { // order acccepted
                            showMsgDialogAndProceedForNotification(false, message)
                        }
                    }
                }
            }
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, IntentFilter("requests_count"))

            ivOrderConfirmationCross.setOnClickListener(this)
            btnOrderInvite.setOnClickListener(this)
            //setting the progress bar value
            setProgressbar()

        } catch (e: Exception) {
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

    private fun setProgressbar() {
        try {
            var i = 0
            runnable = object : Runnable {
                override fun run() {
                    if (i == 120) {
                        handler.removeCallbacks(this)
                        handler.removeCallbacksAndMessages(null)
                    } else {
                        i++
                        pbOrderConfirmation.progress = i
                        handler.postDelayed(this, 1000)
                    }
                }
            }
            handler.postDelayed(runnable, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponse() {
        cartViewModel.responseOrderDetail.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> populateResDetails(it!!)
            }
        })
    }

    // populating and storing data
    @SuppressLint("SetTextI18n")
    private fun populateResDetails(res: GetNewOrderConfirmRes) {
        try {
            if (res.data != null) {

                Log.e("Order Confirmation Data", gson.toJson(res))
                val currency = res.data!!.menuDetails!![0].menu!!.currency

                // setting orderno
                tvOrderConfirmationOrderNo.text =
                    "Order #${res.data!!.orderNumber!!.trim()}"

                // setting Pickuptype
                tvOrderConfirmationType.text = PubFun.toCamelCase(res.data!!.orderType!!)

                //setting date
                tvOrderConfirmationDate.text =
                    PubFun.parseDate(
                        res.data!!.date,
                        Config.requestDateFormat,
                        Config.defaultDateFormat
                    )

                //setting time
                tvOrderConfirmationTime.text =
                    PubFun.parseDate(
                        res.data!!.time!!,
                        Config.requestTimeFormat,
                        Config.defaultTimeFormat
                    )!!
                        .lowercase(Locale.getDefault())

                //setting subtotal
                tvOrderConfirmationSubTotal.text =
                    "${currency}${
                        numberFormat.format(
                            res.data!!.orderDetails!!.subTotal!!.replace(",", "").toDouble()
                        )
                    }"

                //setting tax
                tvOrderConfirmationTaxTotal.text =
                    "${currency}${
                        numberFormat.format(
                            res.data!!.orderDetails!!.tax!!.replace(",", "").toDouble()
                        )
                    }"

                //setting Total
                tvOrderConfirmationTotal.text =
                    "${currency}${
                        numberFormat.format(
                            res.data!!.orderDetails!!.total!!.replace(",", "").toDouble()
                        )
                    }"

                // setting points
                tvOrderConfirmationOrderPoint.text = res.data!!.points!!.toString().trim()


                for (i in res.data!!.menuDetails!!.indices) {
                    orderConfirmationList.add(res.data!!.menuDetails!![i])
                }
                if (orderConfirmationList.isNotEmpty()) {
                    // Adapter initialization
                    adapterOrderConfirmation = OrderConfirmationAdapter()
                    adapterOrderConfirmation.addConfirmationOrderDataList(
                        orderConfirmationList
                    )

                    //recyclerview
                    rvOrderConfirmationOrderList.apply {
                        layoutManager = LinearLayoutManager(this@OrderConfirmationActivity)
                        adapter = adapterOrderConfirmation
                    }
                }
                prepareDataToShare(res)
            }
        } catch (e: Exception) {
            e.toString()
        }
    }


    private fun prepareDataToShare(res: GetNewOrderConfirmRes) {
        try {
            restarurantName = res.data!!.serviceDetails!!.title!!
            address = res.data!!.serviceDetails!!.address!!
            orderId = res.data!!.id!!.toString().trim()
            orderType = res.data!!.orderType!!
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
                                    ivOrderConfirmationCross.visibility = View.VISIBLE
                                    btnOrderInvite.visibility = View.VISIBLE
                                    shareIcon.visibility = View.VISIBLE
                                    tvConfirmationText.visibility = View.GONE
                                    pbOrderConfirmation.visibility = View.GONE
                                } else {
                                    ivOrderConfirmationCross.visibility = View.VISIBLE
                                    tvConfirmationText.visibility = View.GONE
                                    pbOrderConfirmation.visibility = View.GONE
                                }
                            } else {
                                i--
                                postDelayed(this, 1500)
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

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivOrderConfirmationCross -> onBackPressed()
                btnOrderInvite -> openSharedDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSharedDialog() {
        try {
            dialogShare = DialogShare(this@OrderConfirmationActivity)
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
                "Restaurant Details: \nName: \n${restarurantName} \nAddress: \n${address} " +
                        "\nOrder Detail: \nOrder Id:\n${orderId} \nOrder Type:  ${orderType} \n" +
                        "Date ${date} \nTime \n${time}"

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
                "*Restaurant Details:* \n *Name:* \n ${restarurantName} \n *Address:* \n ${address} " +
                        "\n *Order Detail:* \n *Order Id:* \n ${orderId} \n *Order Type:*  ${orderType} \n " +
                        "*Date* ${date} \n *Time* \n ${time}"
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
                "<b>Restaurant Details:</b><br/> Name:<br/> ${restarurantName} <br/><br/>Address:<br/> ${address} " +
                        "<br/><br/>Order Detail:<br/><br/>Order Id:<br/> ${orderId} <br/><br/>Order Type:<br/>${orderType}<br/><br/>" +
                        "Date<br/>${date} <br/><br/>Time<br/> ${time}"

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
            {
                shareingStringData =
                    "Restaurant Details: \nName: \n${restarurantName} \nAddress: \n${address} " +
                            "\nOrder Detail: \nOrder Id:\n${orderId} \nOrder Type:  ${orderType} \n" +
                            "Date ${date} \nTime \n${time}"
                val defaultSmsPackageName =
                    Telephony.Sms.getDefaultSmsPackage(this)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareingStringData.trim())
                if (defaultSmsPackageName != null) {
                    sendIntent.setPackage(defaultSmsPackageName)
                }
                startActivity(sendIntent)
            } else {
                val smsIntent = Intent(Intent.ACTION_VIEW)
                smsIntent.type = "vnd.android-dir/mms-sms"
                smsIntent.putExtra("address", "")
                smsIntent.putExtra("sms_body", shareingStringData)
                startActivity(smsIntent)
            }
            dialogShare.dismiss()
//            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

