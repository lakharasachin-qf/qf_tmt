package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.CartList
import com.themarkettheory.user.model.TotalPointDetailList
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.RestaurantPointDetailListAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.ui.restaurant.OrderConfirmationActivity
import com.themarkettheory.user.viewmodel.CartViewModel
import com.themarkettheory.user.viewmodel.PointsViewModel
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_point_detail.*
import kotlinx.android.synthetic.main.activity_point_detail.rlFooter
import kotlinx.android.synthetic.main.activity_point_detail.tvNoOfItems
import kotlinx.android.synthetic.main.activity_point_detail.tvTPoints
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class PointDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var restaurantPointDetailListAdapter: RestaurantPointDetailListAdapter
    var orderId: String? = ""
    var serviceId: String? = ""
    lateinit var pointsViewModel: PointsViewModel
    lateinit var cartViewModel: CartViewModel
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    var scheduleTime: String? = ""
    var orders = JSONArray()
    var cartList = ArrayList<CartList>()
    var totalPointList = ArrayList<TotalPointDetailList>()
    var totalPoints: Int = 0
    var totalCartPoints: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_detail)

        pointsViewModel = ViewModelProvider(this).get(PointsViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

        tvTitle.setText(getString(R.string.my_points))
        if (intent != null) {
            orderId = intent.getStringExtra("orderId")
            serviceId = intent.getStringExtra("serviceId")
        }

        getPointDetailResponse()

        ivBack.setOnClickListener(this)
        rlFooter.setOnClickListener(this)
        btnRedeemPoints.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (PubFun.isInternetConnection(this)) {
            cartViewModel.get_cart()
            pointsViewModel.total_points_details_new(serviceId)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun getPointDetailResponse() {
//        pointsViewModel.totalPoints()
        pointsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        /*pointsViewModel.responsePointsDetail.observe(this, Observer {
            if(it.status!!){
                nestedScroll.visibility = View.VISIBLE
                tvVendorName.setText(it.data?.service_details?.title)
                tvDesc.setText(it.data?.service_details?.description)

                val restaurantPointDetailListAdapter = RestaurantPointDetailListAdapter(this,it.data?.list!!)
                rvRestaurantPoints.adapter = restaurantPointDetailListAdapter
                rvRestaurantPoints.layoutManager = LinearLayoutManager(this)
            }
        })*/

        pointsViewModel.responseTotalPointsDetail.observe(this, Observer {
            if (it.status!!) {
                nestedScroll.visibility = View.VISIBLE
                tvVendorName.setText(it.data?.serviceDetails?.title)
                tvDesc.setText(it.data?.serviceDetails?.description)
                totalPoints = it.data?.totalPoints!!
                totalPointList.clear()
                totalPointList.addAll(it.data?.list!!)

                restaurantPointDetailListAdapter =
                    RestaurantPointDetailListAdapter(this, totalPointList) {
                        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            setDialog(it)
                        }*/
                        var totalCartPoints = 0
                        cartList.mapIndexed { index, cartList ->
                            totalCartPoints += cartList.menu?.redeem_points!!.toInt()
                        }
                        if (it.in_cart == 1) {
                            if (totalPoints > totalCartPoints + it.redeemPoints!!.toInt()) {
                                vendorDetailViewModel.add_cart(
                                    it.menuId.toString(),
                                    (it.in_cart).toString(), it.serviceId.toString(), "1"
                                )
                            } else {
                                it.in_cart = 0
                                totalPointList[it.adapterPosition].in_cart = 0
                                Toast.makeText(
                                    this,
                                    "Sorry! you did't have enough points",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            vendorDetailViewModel.add_cart(
                                it.menuId.toString(),
                                (it.in_cart).toString(), it.serviceId.toString(), "1"
                            )
                        }

                    }
                rvRestaurantPoints.adapter = restaurantPointDetailListAdapter
                rvRestaurantPoints.layoutManager = LinearLayoutManager(this)
            }
        })

        /* cartViewModel.isLoading.observe(this, Observer {
             if (it!!) {
                 Utils.showProgress(this)
             } else {
                 Utils.hideProgress(this)
             }
         })*/

        cartViewModel.responsePickup.observe(this, Observer {
            if (it.status!!) {
                startActivity(
                    Intent(this, OrderConfirmationActivity::class.java)
                        .putExtra("orderId", it.data?.id.toString())
                )
            }
        })

        cartViewModel.responseGetCart.observe(this, Observer {
            if (it.status!!) {
                if (it.data?.list.isNullOrEmpty()) {
                    rlFooter.visibility = View.GONE
                } else {
                    rlFooter.visibility = View.VISIBLE
                    tvNoOfItems.setText(it.data?.list!!.size.toString())
                    var points = 0
                    cartList.clear()
                    cartList.addAll(it.data?.list!!)
                    it.data?.list?.mapIndexed { index, cartList ->
                        points += cartList.qty!!.times(cartList.menu?.point!!)
                    }
                    tvTPoints.text = "Total Points : " + points.toString()
                    if (::restaurantPointDetailListAdapter.isInitialized) {
                        restaurantPointDetailListAdapter.updateData(totalPointList)
                    }
                }

            }
        })

        vendorDetailViewModel.responseAddCart.observe(this, Observer {
            if (it.status!!) {
                if (PubFun.isInternetConnection(this)) {
                    cartViewModel.get_cart()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }

//                Toast.makeText(requireActivity(),"Item added to cart",Toast.LENGTH_LONG).show()
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setDialog(totalPointDetailList: TotalPointDetailList) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_time);
        val window = dialog.getWindow();
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val timePicker = dialog.findViewById<TimePicker>(R.id.timePicker)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            dialog.dismiss()
            scheduleTime = Utils.convert24To12System(timePicker.hour, timePicker.minute)
            Utils.showDialog(
                this,
                "Alert",
                "Your order will be placed and you need to pickup your order at vendor's location. Are you sure?"
            ) {
                orders = JSONArray()
                val jsonObject = JSONObject()
                jsonObject.put("menu_id", totalPointDetailList.menuId)
                jsonObject.put("qty", "1")
                if (totalPointDetailList.discountedPrice.isNullOrEmpty()) {
                    jsonObject.put("price", totalPointDetailList.price.toString())
                } else {
                    jsonObject.put("price", totalPointDetailList.discountedPrice)
                }
                jsonObject.put("points", totalPointDetailList.redeemPoints)
                jsonObject.put("is_redeem", 1)
                orders.put(jsonObject)
                if (PubFun.isInternetConnection(this)) {
                    cartViewModel.pickup(
                        serviceId,
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "",
                        "0",
                        orders,
                        "0",
                        "",
                        scheduleTime
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnRedeemPoints -> onBackPressed()

            rlFooter -> {
                startActivity(Intent(this, MyBucketActivity::class.java))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@PointDetailActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Point Details"
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
}