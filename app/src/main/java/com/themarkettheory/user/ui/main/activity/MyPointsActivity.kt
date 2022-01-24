package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.mypoints.NewMyPointData
import com.themarkettheory.user.newmodels.mypoints.NewMyPointsRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.NewMyPointListAdapter
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.PointsViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_my_points.*
import kotlinx.android.synthetic.main.activity_my_points_new.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*

class MyPointsActivity : BaseActivity(), View.OnClickListener {
    /*Adapter*/
    private lateinit var myPointListAdapter: NewMyPointListAdapter
    private lateinit var myPointsList: ArrayList<NewMyPointData>
    private lateinit var newPointsDataRow: NewMyPointData

    /*View Model*/
    private lateinit var pointsViewModel: PointsViewModel
    private lateinit var vendorDetailViewModel: VendorDetailViewModel

    /*General*/
    var isRedeemButtonPosition = 0
    var serviceId: String? = ""
    var totalPoints = 0
    var totalRedeemPoints = 0
    var totalRedeemItems = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_my_points_new)

            if (intent != null) {
                serviceId = intent.getStringExtra("serviceId")
            }
            pointsViewModel = ViewModelProvider(this).get(PointsViewModel::class.java)
            vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

            tvTitle.text = getString(R.string.my_points)
            ivFavorite.visibility = View.GONE
            ivShare.visibility = View.GONE
            rlMyPointsFooter.visibility = View.GONE

            //enabling filter option on Total Points
            ivFilter.visibility = if (Config.enablePointsFilter) View.VISIBLE else View.GONE

            getPointsResponse()

            ivBack.setOnClickListener(this)
            tvMyPointsNext.setOnClickListener(this)
            ivFilter.setOnClickListener(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            totalPoints = 0
            totalRedeemPoints = 0
            totalRedeemItems = 0
            callMyPointsApi()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@MyPointsActivity)) {
                if (Config.isMyPointsActivityComingFromOverviewFragment) {
                    Config.isVideoPlay = false
                    startActivity(
                        Intent(this@MyPointsActivity, VendorDetailActivity::class.java)
                            .putExtra("category", "1")
                            .putExtra("serviceId", serviceId)
                            .putExtra("vendorTitle", Config.vendorDetailServiceName)
                            .putExtra("selectPosition", 0)
                    )
                } else {
                    startActivity(Intent(this@MyPointsActivity, TotalPointsActivity::class.java))
                }
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callMyPointsApi() {
        try {
            if (PubFun.isInternetConnection(this@MyPointsActivity)) {
                pointsViewModel.myPoints(serviceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddCartApi(dishId: String, qty: String) {
        try {
            if (PubFun.isInternetConnection(this@MyPointsActivity)) {
                vendorDetailViewModel.menu_add_cart(
                    Config.vendorDetailServiceId,
                    dishId,
                    "1",
                    qty,
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

    private fun getPointsResponse() {
        pointsViewModel.isLoading.observe(this, Observer {
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

        vendorDetailViewModel.responseMenuAddCart.observe(this, {
            try {
                when (it.status) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> {
                        myPointListAdapter.isRedeemButton(
                            isRedeemButtonPosition,
                            if (newPointsDataRow.isRedeem!! == 0) 1 else 0
                        )

                        /*Calculate Footer part*/
                        myPointsList[isRedeemButtonPosition].isRedeem =
                            if (newPointsDataRow.isRedeem!! == 1) 1 else 0
                        calculateFooterPart(isRedeemButtonPosition)

                        if (newPointsDataRow.isRedeem!! == 1) {
                            onResume()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        pointsViewModel.responseMyPoints.observe(this, Observer {
            try {
                when (it.status) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> populateData(it!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun populateData(res: NewMyPointsRes) {
        // setting up the total points
        try {
            if (res.serviceDetails!!.id!! == 0 && res.serviceDetails!!.title!!.trim().isEmpty()) {
                Config.menuServiceId = ""
                Config.menuServiceName = ""
            } else {
                Config.menuServiceId = res.serviceDetails!!.id!!.toString().trim()
                Config.menuServiceName = res.serviceDetails!!.title!!.trim()
            }

            tvMyPointsNew.text = res.totalPoints!!.toString().trim()
            totalPoints = res.totalPoints!!

            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        val newPointsData = objects as NewMyPointData
                        myRoomDatabase.daoConfig().apply {
                            /*Service Details*/
                            deleteConfigTableByField(Config.dbNewPointsDataRowServiceDetails)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbNewPointsDataRowServiceDetails,
                                    gson.toJson(res.serviceDetails!!)
                                )
                            )

                            /*NewMyPointsData*/
                            deleteConfigTableByField(Config.dbNewPointsDataRow)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbNewPointsDataRow,
                                    gson.toJson(newPointsData)
                                )
                            )
                        }
                        startActivity(
                            Intent(
                                this@MyPointsActivity,
                                MyPointsDetailActivity::class.java
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val redeemClickListener = object : ListClickListener {
                @SuppressLint("SetTextI18n")
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        newPointsDataRow = objects as NewMyPointData
                        isRedeemButtonPosition = pos

                        if (newPointsDataRow.isRedeem == 0 && totalPoints - newPointsDataRow.redeemPoints!! < 0) {
                            showMsgDialogAndProceed("You didn't have enough points")
                        } else {
                            if (Config.menuServiceId.isEmpty() || Config.menuServiceId == Config.vendorDetailServiceId) {
                                callAddCartApi(
                                    newPointsDataRow.id!!.toString().trim(),
                                    if (newPointsDataRow.isRedeem!! == 0) "1" else "0",
                                )
                            } else {
                                val dialogPoints = DialogToast(this@MyPointsActivity)
                                dialogPoints.show()
                                dialogPoints.holder!!.let {
                                    it.tvTitle.text = getString(R.string.my_points)
                                    it.tvMessage.text =
                                        "Are you sure you want to delete current cart points for ${Config.menuServiceName}"
                                    it.btnDialogCancel.text = "No"
                                    it.btnDialogLogout.text = "Yes"
                                    it.btnDialogLogout.setOnClickListener {
                                        dialogPoints.dismiss()
                                        Config.menuServiceId = Config.vendorDetailServiceId
                                        Config.menuServiceName = Config.vendorDetailServiceName
                                        callAddCartApi(
                                            newPointsDataRow.id!!.toString().trim(),
                                            "1"
                                        )
                                    }
                                    it.btnDialogCancel.setOnClickListener {
                                        dialogPoints.dismiss()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            myPointListAdapter =
                NewMyPointListAdapter(this@MyPointsActivity, redeemClickListener, listener)
            rvMyPoints.apply {
                layoutManager = LinearLayoutManager(this@MyPointsActivity)
                adapter = myPointListAdapter
            }
            if (res.data!!.isNotEmpty()) {
                myPointsList = res.data!! as ArrayList<NewMyPointData>
                rvMyPoints.visibility = View.VISIBLE
                clMyPointsHeader.visibility = View.VISIBLE
                myPointListAdapter.addTotalPointList(myPointsList)

                /*Calculate Footer Part*/
                calculateFooterPart(-1)
            } else {
                emptyDataTxt.visibility = View.VISIBLE
                rvMyPoints.visibility = View.GONE
                clMyPointsHeader.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
            //  rlMyPointsFooter -> startActivity(Intent(this, MyBucketActivity::class.java))
            tvMyPointsNext -> {
                if (PubFun.isInternetConnection(this@MyPointsActivity)) {
                    if (Config.isBucketAddMoreClicked) {
                        Config.isBucketAddMoreClicked = false
                    }
                    startActivity(Intent(this, MyPointBucketActivity::class.java))
                    finish()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
            ivFilter -> {
                startActivity(
                    Intent(this@MyPointsActivity, PointsHistoryActivity::class.java)
                        .putExtra("serviceid", serviceId)
                )
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateFooterPart(position: Int) {
        try {
            if (myPointsList.isNotEmpty()) {
                for (i in myPointsList.indices) {
                    if (position >= 0) {
                        if (myPointsList[i].isRedeem == 1 && i == position) {
                            totalPoints -= myPointsList[i].redeemPoints!!
                            totalRedeemItems += 1
                            totalRedeemPoints += myPointsList[i].redeemPoints!!
                        } else if (myPointsList[i].isRedeem == 0 && i == position) {
                            totalPoints += myPointsList[i].redeemPoints!!
                            totalRedeemItems -= 1
                            totalRedeemPoints -= myPointsList[i].redeemPoints!!
                        }
                    } else {
                        if (myPointsList[i].isRedeem == 1) {
                            totalRedeemItems += 1
                            totalRedeemPoints += myPointsList[i].redeemPoints!!
                        }
                    }
                }
            }

            /*Total Points - Header*/
            tvMyPointsNew.text = "$totalPoints"

            /*Footer Layout visibility*/
            rlMyPointsFooter.visibility = if (totalRedeemItems > 0) View.VISIBLE else View.GONE

            /*Reset isRedeemed Service Id*/
            /*if (totalRedeemItems == 0 && position != -1) {
                myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbIsRedeemedServiceId)
            }*/

            /*Footer In Bucket */
            tvMyPointsNoOfItems.text = "$totalRedeemItems"

            /*Footer Total Redeem Points*/
            tvMyPointsTotalPoints.text =
                "Redeem Points: ${if (totalRedeemPoints < 0) "0" else totalRedeemPoints}"

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@MyPointsActivity)
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