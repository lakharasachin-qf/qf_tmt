package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
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
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingData
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingNewRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.MyTableBookingAdapter
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_my_table_bookings.*
import kotlinx.android.synthetic.main.toolbar.*

class MyTableBookingsActivity : BaseActivity(), View.OnClickListener {
    //View Model
    private lateinit var menuViewModel: MenuViewModel

    //Adapter
    private lateinit var adapterTableBooking: MyTableBookingAdapter

    //Components
    private lateinit var viewToolBarMyBooking: View
    private lateinit var viewToolBarBackButton: ShapeableImageView
    private lateinit var viewToolBarTitle: MaterialTextView
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_table_bookings)
        try {
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
            callMyTableBookingApi()
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
            callMyTableBookingApi()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@MyTableBookingsActivity)) {
//                if (Config.isMyBookingClickedFromHome) {
//                    Config.isMyBookingClickedFromHome = false
//                } else {
//                    super.onBackPressed()
//                    Config.bottomBarClickedName = Config.menuBottomBarClick
//                }
//                if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
//                    Config.isMenuFragmentComingFrom = ""
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
        try {
            when (v) {
                viewToolBarBackButton -> onBackPressed()
                refreshPage -> if (PubFun.isInternetConnection(this@MyTableBookingsActivity)) {
                    menuViewModel.myTableBooking()
                    //getResponse()

                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            //Initialize Toolbar Views
            viewToolBarMyBooking =
                findViewById(R.id.toolbarMyTableBooking)
            viewToolBarBackButton =
                viewToolBarMyBooking.findViewById(R.id.ivBack)
            viewToolBarTitle = viewToolBarMyBooking.findViewById(R.id.tvTitle)

           // refreshPage.visibility = View.VISIBLE

            //Initialize View Model
            menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

            //Toolbar Title
            viewToolBarTitle.text = getString(R.string.my_booking)

            //Toolbar Back Button
            viewToolBarBackButton.setOnClickListener(this)
            refreshPage.setOnClickListener(this)

            getResponse()

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

        //My Table Booking Response
        menuViewModel.responseTableBookings.observe(this, {
            try {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> populateMyTableBooking(it)
                }
            } catch (e: Exception) {

                e.printStackTrace()
            }
        })
    }

    private fun callMyTableBookingApi() {
        try {
            if (PubFun.isInternetConnection(this@MyTableBookingsActivity)) {
                menuViewModel.myTableBooking()
                //getResponse()

            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateMyTableBooking(res: MyTableBookingNewRes) {
        try {
            if (res.`data` != null) {
                Log.e("My TableBookingList:", res.toString())
                val tableBookingList = ArrayList<MyTableBookingData>()
                for (i in res.`data`!!.indices) {
                    tableBookingList.add(res.`data`!![i])
                }
                if (tableBookingList.isNotEmpty()) {
                    //Adapter Listener
                    val listener = object : ListClickListener {
                        override fun onClickListener(view: View, pos: Int, objects: Any) {
                            if (PubFun.isInternetConnection(this@MyTableBookingsActivity)) {
                                val dataRow = objects as MyTableBookingData
                                if (dataRow.order_details!!.id!! != 0) {
                                    redirectToOrderDetail(dataRow)
                                } else {
                                    Config.isBookingDetailOpeningFrom =
                                        Config.isBookingDetailOpeningFromMyBookingList
                                    redirectToMyTableBookingDetail(dataRow)
                                }
                            } else {
                                showMsgDialogAndProceed(Config.msgToastForInternet)
                            }
                        }
                    }
                    //Adapter Initialization & add data
                    adapterTableBooking = MyTableBookingAdapter(listener)
                    adapterTableBooking.addTableBookingList(tableBookingList)
                    //RecyclerView
                    rvMyTableBooking.apply {
                        layoutManager = LinearLayoutManager(this@MyTableBookingsActivity)
                        adapter = adapterTableBooking
                    }
                }
            }
        } catch (e: Exception) {
            menuViewModel.isLoading.value = false
            e.printStackTrace()
        }
    }

    private fun redirectToMyTableBookingDetail(dataRow: MyTableBookingData) {
        try {
            myRoomDatabase.daoConfig().apply {
                deleteConfigTableByField(Config.dbTableBookingId)
                insertConfigTable(
                    TableConfig(
                        Config.dbTableBookingId,
                        dataRow.id!!.toString()
                    )
                )
            }
            startActivity(
                Intent(
                    this@MyTableBookingsActivity,
                    MyTableBookingDetailActivity::class.java
                ).putExtra("Booking", gson.toJson(dataRow))
            )
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun redirectToOrderDetail(dataRow: MyTableBookingData) {
        try {
            myRoomDatabase.daoConfig().apply {
                deleteConfigTableByField(Config.dbOrderId)
                insertConfigTable(
                    TableConfig(
                        Config.dbOrderId,
                        dataRow.order_details!!.id!!.toString().trim()
                    )
                )
            }
            startActivity(
                Intent(
                    this@MyTableBookingsActivity,
                    OrderDetailActivity::class.java
                ).putExtra("data", gson.toJson(dataRow))
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@MyTableBookingsActivity)
            val myDialog = DialogToast(this@MyTableBookingsActivity)
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