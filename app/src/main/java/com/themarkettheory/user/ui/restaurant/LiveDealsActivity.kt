package com.themarkettheory.user.ui.restaurant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.livedeals.LiveDealList
import com.themarkettheory.user.newmodels.livedeals.NewLiveDealRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_live_deals.*
import kotlinx.android.synthetic.main.activity_live_deals_new.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*
import java.text.SimpleDateFormat
import java.util.*

class LiveDealsActivity : BaseActivity(), View.OnClickListener {
    //General
    private var serviceAddress: String? = ""
    private var liveDealRowPosition = 0
    private lateinit var liveDealList: LiveDealList
    private var addedItemsList = ArrayList<LiveDealList>()

    /*Live Deal Adapter*/
    private lateinit var liveDealAdapter: LiveDealNewAdapter

    //View Models
    private lateinit var vendorDetailViewModel: VendorDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_deals_new)
        try {
            /*Initialize View Model*/
            vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

            /*SetOnClickListener*/
            ivLiveDealBack.setOnClickListener(this)
            ivLiveDealShare.setOnClickListener(this)
            tvLiveDealNext.setOnClickListener(this)

            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            addedItemsList.clear()
            if (PubFun.isInternetConnection(this@LiveDealsActivity)) {
                vendorDetailViewModel.live_deals(Config.vendorDetailServiceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivLiveDealBack -> onBackPressed()
                ivLiveDealShare -> {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "Find this service on The Market Theory | ${Config.vendorDetailServiceName}\n${serviceAddress}"
                    )
                    startActivity(Intent.createChooser(share, "Share"))
                }
                tvLiveDealNext -> {
                    if (PubFun.isInternetConnection(this@LiveDealsActivity)) {
                        if (Config.isBucketAddMoreClicked) {
                            Config.isBucketAddMoreClicked = false
                        }
                        startActivity(
                            Intent(
                                this@LiveDealsActivity,
                                MyBucketLiveDealActivity::class.java
                            )
                        )
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
            /*Footer Visibility*/
            rlLiveDealFooter.visibility = View.GONE

            //region Progress Loading bar response
            vendorDetailViewModel.isLoading.observe(this, {
                if (it!!) {
                    Utils.showProgress(this@LiveDealsActivity)
                } else {
                    Utils.hideProgress(this@LiveDealsActivity)
                }
            })
            //endregion

            //region Live Deal response
            vendorDetailViewModel.responseLiveDealMenu.observe(this, {
                when (it.status!!) {
                    0 -> {
                        showMsgDialogAndProceed(it.message!!.trim())
                        clLiveDeal.visibility = View.GONE
                    }
                    1 -> populateLiveDeals(it!!)
                }
            })
            //endregion

            //region Live Deal Menu Cart Response
            vendorDetailViewModel.responseMenuAddCart.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        liveDealAdapter.isInCartButton(
                            liveDealRowPosition,
                            if (liveDealList.menu!!.inCart == 0) 1 else 0
                        )

                        if (liveDealList.menu!!.inCart == 1) {
                            addedItemsList.add(liveDealList)
                        } else {
                            liveDeal@ for (i in addedItemsList.indices) {
                                if (addedItemsList[i].serviceId == liveDealList.serviceId) {
                                    addedItemsList.removeAt(i)
                                    break@liveDeal
                                }
                            }
                        }
                        calculateFooterPart()
                    }
                }
            })
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateLiveDeals(res: NewLiveDealRes) {
        Log.e("populateLive",gson.toJson(res))
        try {
            if (res.data!!.list.isNullOrEmpty()) {
                clLiveDeal.visibility = View.GONE
            } else {

                /*Live Deal Page Visibility*/
                clLiveDeal.visibility = View.VISIBLE

                /*Total Live Deals Counts*/
                tvLiveDealsPoint.text = "${res.data!!.list!!.size}"

                //region Expiry Time Reverse Countdown Timer
                var futureMinDate = Date()
                val sdf = SimpleDateFormat(
                    "${Config.requestDateFormat} ${Config.requestTimeFormat}",
                    Locale.getDefault()
                )
                val currentDate = SimpleDateFormat(
                    Config.requestDateFormat,
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
                val liveDealTime = res.data!!.list!![0].endTime!!.trim()
                futureMinDate = sdf.parse("$currentDate $liveDealTime")!!
                object : CountDownTimer(futureMinDate.time - System.currentTimeMillis(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        try {
                            val sec = (millisUntilFinished / 1000) % 60
                            val min = (millisUntilFinished / (1000 * 60)) % 60
                            val hr = (millisUntilFinished / (1000 * 60 * 60)) % 24
                            val day = ((millisUntilFinished / (1000 * 60 * 60)) / 24).toInt()
                            val formattedTimeStr = if (day > 1) "$day days $hr : $min : $sec"
                            else "$day day $hr : $min : $sec"

                            tvLiveDealsHours.text = "$hr"
                            tvLiveDealsMins.text = "$min"
                            tvLiveDealsSecs.text = "$sec"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onFinish() {
                        try {
                            tvLiveDealsHours.text = "00"
                            tvLiveDealsMins.text = "00"
                            tvLiveDealsSecs.text = "00"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }.start()
                //endregion

                //region Live deal RecyclerView & Adapter
                val listener = object : ListClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onClickListener(view: View, pos: Int, objects: Any) {
                        try {
                            liveDealRowPosition = pos
                            liveDealList = objects as LiveDealList

                            if (liveDealList.serviceId!!.toString()
                                    .trim() == Config.vendorDetailServiceId
                            ) {
                                addMenuCartLiveDeal(liveDealList)
                            } else {
                                val dialogLiveDeal = DialogToast(this@LiveDealsActivity)
                                dialogLiveDeal.show()
                                dialogLiveDeal.holder!!.let {
                                    it.tvTitle.text = getString(R.string.live_deals)
                                    it.tvMessage.text =
                                        "Are you sure you want to delete current cart live deal for ${Config.menuServiceName}"
                                    it.btnDialogCancel.text = "No"
                                    it.btnDialogLogout.text = "Yes"
                                    it.btnDialogCancel.setOnClickListener {
                                        dialogLiveDeal.dismiss()
                                    }
                                    it.btnDialogLogout.setOnClickListener {
                                        dialogLiveDeal.dismiss()
                                        addMenuCartLiveDeal(liveDealList)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                liveDealAdapter = LiveDealNewAdapter(this@LiveDealsActivity, listener)
                rvLiveDeal.apply {
                    layoutManager = LinearLayoutManager(this@LiveDealsActivity)
                    adapter = liveDealAdapter
                }
                liveDealAdapter.addLiveDealData(res.data!!.list as ArrayList<LiveDealList>)
                Config.menuServiceId = res.data!!.serviceDetails!!.id!!.toString().trim()
                Config.menuServiceName = res.data!!.serviceDetails!!.title!!.toString().trim()
                //endregion

                //region Calculate Footer Count & Visibility
                for (i in res.data!!.list!!.indices) {
                    if (res.data!!.list!![i].menu!!.inCart!! == 1) {
                        addedItemsList.add(res.data!!.list!![i])
                    }
                }
                calculateFooterPart()
                //endregion
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMenuCartLiveDeal(liveDealData: LiveDealList) {
        try {
            if (PubFun.isInternetConnection(this@LiveDealsActivity)) {
                vendorDetailViewModel.menu_add_cart(
                    liveDealData.serviceId!!.toString().trim(),
                    liveDealData.menu!!.id!!.toString().trim(),
                    "0",
                    if (liveDealData.menu!!.inCart!! == 0) "1" else "0",
                    "0",
                    1,
                    0
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateFooterPart() {
        try {
            rlLiveDealFooter.visibility = if (addedItemsList.isEmpty()) View.GONE else View.VISIBLE
            tvLiveDealNoOfItems.text = addedItemsList.size.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@LiveDealsActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.live_deal)
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