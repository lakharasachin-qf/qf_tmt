package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.newmodels.totalpoints.NewTotalPointData
import com.themarkettheory.user.newmodels.totalpoints.NewTotalPointRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.HomeCategoriesAdapter
import com.themarkettheory.user.ui.main.adapter.NewTotalPointAdapter
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.PointsViewModel
import kotlinx.android.synthetic.main.activity_my_orders.*
import kotlinx.android.synthetic.main.activity_restaurant_list.*
import kotlinx.android.synthetic.main.activity_total_points_new.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*
import kotlinx.android.synthetic.main.toolbar_restaurant.tvTitle
import java.util.*
import kotlin.collections.ArrayList

class TotalPointsActivity : BaseActivity(), View.OnClickListener {
    lateinit var pointsViewModel: PointsViewModel
    var title: String? = ""

    private var selectedFoodCategoryName = ""

    //adapter declaration
    private lateinit var adapterMyPoint: NewTotalPointAdapter

    //Food Categories Adapters
    private lateinit var foodCategoryAdapter: HomeCategoriesAdapter

    //Dashboard Res
    private lateinit var newHomeRes: NewHomeRes

    private var subCategoriesList = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_points_new)
        try {
            if (intent != null) {
                title = intent.getStringExtra("title")
            }
            // intitalizing model
            pointsViewModel = ViewModelProvider(this).get(PointsViewModel::class.java)
            ivAction.visibility = View.VISIBLE

            if (title.isNullOrEmpty())
                tvTitle.setText(getString(R.string.total_points))
            else
                tvTitle.setText(title)
            ivFavorite.visibility = View.GONE
            ivShare.visibility = View.GONE

            getPointsResponse()
            //calling api by default calling filter and service id blank to get all data
            if (PubFun.isInternetConnection(this@TotalPointsActivity)) {
                pointsViewModel.totalPoints("", "")
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
            newHomeRes = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
                NewHomeRes::class.java
            )
            fillHeaderData()

            ivBack.setOnClickListener(this)
            ivAction.setOnClickListener(this)

            etPointSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapterMyPoint.filter(s.toString().lowercase().trim())
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fillHeaderData() {
        //region Filling Food Category List
        for (i in newHomeRes.data!!.categories!![0].subcategory!!.indices) {
            val category = Category()
            category.id = newHomeRes.data!!.categories!![0].subcategory!![i].id
            category.name = newHomeRes.data!!.categories!![0].subcategory!![i].name

            /*if (category.name!! == Config.subCategory0IndexRestaurantOldName) {
                category.name = Config.subCategory0IndexRestaurantNewName
            }*/
            subCategoriesList.add(category)
        }
        if (subCategoriesList.isNotEmpty()) {
            foodCategoryAdapter =
                HomeCategoriesAdapter(this@TotalPointsActivity, subCategoriesList) {
                    try {
                        if (PubFun.isInternetConnection(this)) {
                            selectedFoodCategoryName = if (it.name!!.trim()
                                    .lowercase(Locale.getDefault()) ==
                                Config.subCategory0IndexRestaurantOldName.lowercase(Locale.getDefault())
                            ) "" else it.name!!.trim()
                            pointsViewModel.totalPoints(selectedFoodCategoryName, "")
                        } else {
                            foodCategoryAdapter.setPosition(Config.foodCategoryPreviousPosition)
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                        //  Toast.makeText(this,selectedFoodCategoryName,Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            //region  RecyclerView
            rvTotalPointsCategory.apply {
                layoutManager = LinearLayoutManager(
                    this@TotalPointsActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = foodCategoryAdapter
            }
            //endregion
        }
    }

    private fun getPointsResponse() {
        pointsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        pointsViewModel.responseTotalPoints.observe(this, Observer {
            when (it.status) {
                0 -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                1 -> populate(it)
            }
        })
    }

    private fun populate(res: NewTotalPointRes) {
        tvTotalPointsNew.text = res.totalPoints!!.toString().trim()

        val listener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                if (PubFun.isInternetConnection(this@TotalPointsActivity)) {
                    val data = objects as NewTotalPointData
                    /*  startActivity(
                          Intent(this@TotalPointsActivity, PointsHistoryActivity::class.java)
                              .putExtra("serviceid", data.serviceDetails!!.id!!.toString())
                      )*/
                    Config.enablePointsFilter = true
                    Config.isMyPointsActivityComingFromOverviewFragment = false
                    Config.vendorDetailServiceId = data.serviceDetails!!.id!!.toString().trim()
                    Config.vendorDetailServiceName = data.serviceDetails!!.title!!.trim()
                    startActivity(
                        Intent(this@TotalPointsActivity, MyPointsActivity::class.java)
                            .putExtra("serviceId", data.serviceDetails!!.id!!.toString().trim())
                    )
                    finish()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }

        val imageListener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                if (PubFun.isInternetConnection(this@TotalPointsActivity)) {
                    val data = objects as NewTotalPointData
                    Config.isMenuFragmentComingFrom = ""
                    Config.isBookingDetailOpeningFrom = Config.isBookingDetailOpeningFromTotalPoints
                    if (!Config.isMyPointClickedFromHome){
                        Config.isVendorDetailComingFromTotalPoints = true
                    }
                    startActivity(
                        Intent(this@TotalPointsActivity, VendorDetailActivity::class.java)
                            .putExtra("category", "1")
                            .putExtra("serviceId", data.serviceDetails!!.id!!.toString().trim())
                            .putExtra("vendorTitle", data.serviceDetails!!.title)
                            .putExtra("selectPosition", 0)
                    )
                    finish()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }

        val clMiddleLayoutClick = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                if (PubFun.isInternetConnection(this@TotalPointsActivity)) {
                    val data = objects as NewTotalPointData
                    /*   startActivity(
                           Intent(this@TotalPointsActivity, PointsHistoryActivity::class.java)
                               .putExtra("serviceid", data.serviceDetails!!.id!!.toString())
                       )*/
                    Config.enablePointsFilter = true
                    Config.isMyPointsActivityComingFromOverviewFragment = false
                    Config.vendorDetailServiceId = data.serviceDetails!!.id!!.toString().trim()
                    Config.vendorDetailServiceName = data.serviceDetails!!.title!!.trim()
                    startActivity(
                        Intent(this@TotalPointsActivity, MyPointsActivity::class.java)
                            .putExtra("serviceId", data.serviceDetails!!.id!!.toString().trim())
                    )
                    finish()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }

        adapterMyPoint = NewTotalPointAdapter(this, listener, imageListener, clMiddleLayoutClick)
        // recyclerview
        rvTotalPoints.apply {
            layoutManager = LinearLayoutManager(this@TotalPointsActivity)
            adapter = adapterMyPoint
        }
        if (res.data!!.isNotEmpty()) {
            rvTotalPoints.visibility = View.VISIBLE
            clTotalPointsHeader.visibility = View.VISIBLE
            adapterMyPoint.addTotalPointList(res.data!! as ArrayList<NewTotalPointData>)
        } else {
            rvTotalPoints.visibility = View.GONE
            clTotalPointsHeader.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@TotalPointsActivity)) {
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
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            ivAction -> {
                /*foodCategoryAdapter.setPosition(0)*/
                if (PubFun.isInternetConnection(this)) {
                    pointsViewModel.totalPoints(selectedFoodCategoryName, "")
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@TotalPointsActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Total Point"
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