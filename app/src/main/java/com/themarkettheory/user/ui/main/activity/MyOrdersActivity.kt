package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.myorders.MyOrderNewData
import com.themarkettheory.user.newmodels.myorders.MyOrdersNewRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.OrderListAdapter
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.toolbar.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.themarkettheory.user.ui.coupon.CouponActivity


class MyOrdersActivity : BaseActivity(), View.OnClickListener {

    //ToolBar Components
    private lateinit var viewOrderToolbar: View
    private lateinit var ivBackOrderToolbar: ShapeableImageView
    private lateinit var tvTitleOrderToolbar: MaterialTextView
    private lateinit var swipeContainer: SwipeRefreshLayout

    //General
    private val myOrderNewDataList = ArrayList<MyOrderNewData>()

    //adapter declaration
    private lateinit var adapterMyOrderList: OrderListAdapter

    lateinit var menuViewModel: MenuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        try {
            //Toolbar initialization
            viewOrderToolbar = findViewById(R.id.myOrdersToolbar)
            ivBackOrderToolbar = viewOrderToolbar.findViewById(R.id.ivBack)
            tvTitleOrderToolbar = viewOrderToolbar.findViewById(R.id.tvTitle)
            iniRefreshListener()
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun iniRefreshListener() {
        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { // This method gets called when user pull for refresh,
            // You can make your API call here,
            callMyOrderApi()
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false)
                }
            }, 1500)
        })
    }

    override fun onResume() {

        super.onResume()
        try {
            //calling api for my orders
            if (PubFun.isInternetConnection(this@MyOrdersActivity)) {
                menuViewModel.myOrders()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun init() = try {// setting title bar
        tvTitleOrderToolbar.text = getString(R.string.my_orders)

        //initializing model
        menuViewModel = ViewModelProvider(this@MyOrdersActivity).get(MenuViewModel::class.java)
        getResponse()
        //refreshPage.visibility = View.VISIBLE
        //onclicklistener on back button
        ivBackOrderToolbar.setOnClickListener(this)
        refreshPage.setOnClickListener(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    private fun callMyOrderApi() {
        try {
            if (PubFun.isInternetConnection(this@MyOrdersActivity)) {
                menuViewModel.myOrders()
                getResponse()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponse() {

        //ProgressBar
        menuViewModel.isLoading.observe(this, {
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

        //getting response for api
        menuViewModel.responseOrdersNew.observe(this, Observer {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> populateData(it)
            }
        })
    }

    private fun populateData(res: MyOrdersNewRes) {
        Utils.hideProgress(this)
        myOrderNewDataList.clear()

        for (i in res.data!!.indices) {
            myOrderNewDataList.add(res.data!![i])
        }
        Log.e("My OrderList:", myOrderNewDataList.toString())
        if (myOrderNewDataList.isNotEmpty()) {

            Log.e("My OrderList:", myOrderNewDataList.toString())
            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        if (PubFun.isInternetConnection(this@MyOrdersActivity)) {
                            val dataRow = objects as MyOrderNewData
                            myRoomDatabase.daoConfig().apply {
                                deleteConfigTableByField(Config.dbOrderId)
                                insertConfigTable(
                                    TableConfig(
                                        Config.dbOrderId,
                                        dataRow.orderDetails!!.id!!.toString().trim()
                                    )
                                )
                                deleteConfigTableByField(Config.dbMyOrderListRecyclerViewPosition)
                                insertConfigTable(
                                    TableConfig(
                                        Config.dbMyOrderListRecyclerViewPosition,
                                        pos.toString()
                                    )
                                )
                            }
                            startActivity(
                                Intent(
                                    this@MyOrdersActivity,
                                    OrderDetailActivity::class.java
                                )
                            )
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            adapterMyOrderList = OrderListAdapter(this, listener)
            adapterMyOrderList.addMyOrderDataList(myOrderNewDataList)

            // recyclerview
            val selectedPosition = myRoomDatabase.daoConfig()
                .selectConfigTableByField(Config.dbMyOrderListRecyclerViewPosition)
            rvMyOrders.apply {
                layoutManager = LinearLayoutManager(this@MyOrdersActivity)
                adapter = adapterMyOrderList
                smoothScrollToPosition(selectedPosition!!.toInt())
            }
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@MyOrdersActivity)) {

//                if (!Config.isMyPointClickedFromHome) {
//                    Config.bottomBarClickedName = Config.menuBottomBarClick
//                }
//                if (intent.hasExtra("notification")) {
//                    Config.bottomBarClickedName = Config.homeBottomBarClicked
//                }
                Config.bottomBarClickedName = Config.menuBottomBarClick
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBackOrderToolbar -> onBackPressed()
            refreshPage ->
                if (PubFun.isInternetConnection(this@MyOrdersActivity)) {
                    menuViewModel.myOrders()
                    // getResponse()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@MyOrdersActivity)
            val myDialog = DialogToast(this@MyOrdersActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_orders)
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