package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.razorpay.*
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.AttireImageListAdapter
import com.themarkettheory.user.ui.main.adapter.AttireSizeListAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.RetailViewModel
import kotlinx.android.synthetic.main.activity_attire_detail.*
import kotlinx.android.synthetic.main.activity_attire_detail.ll
import kotlinx.android.synthetic.main.activity_attire_detail.rlFooter
import kotlinx.android.synthetic.main.activity_attire_detail.tvNoOfItems
import kotlinx.android.synthetic.main.activity_attire_detail.tvTotal
import kotlinx.android.synthetic.main.activity_attire_detail.tvTotalPoints
import kotlinx.android.synthetic.main.activity_select_category.*
import kotlinx.android.synthetic.main.button_progress.pb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class AttireDetailActivity : BaseActivity(), View.OnClickListener, PaymentResultWithDataListener {
    var id: String? = ""
    var serviceId: String? = ""
    var sizeId: String? = ""
    var discountType: String? = ""
    var discountAmount: String? = ""
    var totalPoints: Float? = 0.0f
    var paymentId: String? = ""
    var offerId: String? = ""
    var orders = JSONArray()
    val jsonObject = JSONObject()
    var total = 0.0f
    var subTotal = 0.0f
    lateinit var retailViewModel: RetailViewModel
    lateinit var generalViewModel: GeneralViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attire_detail)

        retailViewModel = ViewModelProvider(this).get(RetailViewModel::class.java)
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
        if (intent != null) {
            id = intent.getStringExtra("id")
            serviceId = intent.getStringExtra("serviceId")
        }

        retailViewModel.retailMenuDetail(id)
        generalViewModel.get_cart()

        getResponse()

        ivBack.setOnClickListener(this)
        btnAddToCart.setOnClickListener(this)
        rlFooter.setOnClickListener(this)
    }

    private fun getResponse() {
        retailViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        retailViewModel.responsePickup.observe(this, Observer {
            Utils.showSnackbar(this, it.message)
            if (it.status!!) {
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }, 2000)
            }
        })

        retailViewModel.responseAddCart.observe(this, Observer {
            if (it.status!!) {
//                Toast.makeText(this,"Item added to cart",Toast.LENGTH_LONG).show()
            }
        })

        retailViewModel.responseRetailMenuDetail.observe(this, Observer {
            if (it.status!!) {
                ll.visibility = View.VISIBLE
                orders = JSONArray()

                jsonObject.put("menu_id", id)
                jsonObject.put("qty", "1")
                if (it.data?.discountedPrice.isNullOrEmpty()) {
                    jsonObject.put("price", it.data?.price)
                } else {
                    jsonObject.put("price", it.data?.discountedPrice)
                }
                jsonObject.put("points", it.data?.point)

                totalPoints = it.data?.point!!.toFloat()
                tvDescr.setText(it.data?.description)
                tvItemName.setText(it.data?.title)
                if (it.data?.discountedPrice.isNullOrEmpty()) {
                    tvTotal.setText(it.data?.currency + it.data?.price.toString())
                } else {
                    tvTotal.setText(it.data?.currency + it.data?.discountedPrice.toString())
                }
                subTotal = it.data?.discountedPrice!!.toFloat()
                total = subTotal
                val list = ArrayList<String>()
                list.add(it.data?.image!!)
                val attireImageListAdapter = AttireImageListAdapter(this, list)
                viewpager_images.adapter = attireImageListAdapter
                indicator.setViewPager(viewpager_images)

                val attireSizeListAdapter = AttireSizeListAdapter(this, it.data?.sizes!!) {
                    sizeId = it.id.toString()
                }
                rvSize.adapter = attireSizeListAdapter
                rvSize.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                if (it.data?.in_cart == 1) {
                    btnAddToCart.setText(getString(R.string.added_to_cart))
                } else {
                    btnAddToCart.setText(getString(R.string.add_to_cart))
                }
            } else {
                Utils.showSnackbar(this, it.message)
            }
        })

        generalViewModel.responseGetCart.observe(this, Observer {
            if (it.status!!) {
                if (it.data?.list.isNullOrEmpty()) {
                    rlFooter.visibility = View.VISIBLE
                } else {
                    var count = 0
                    var points = 0
                    it.data?.list?.mapIndexed { index, cartList ->
                        count += 1
                        points += cartList.menu?.point!!
                    }
                    if (count == 0) {
                        rlFooter.visibility = View.GONE
                    } else {
                        rlFooter.visibility = View.VISIBLE
                        tvNoOfItems.setText(count.toString())
                        tvTotalPoints.setText("Total Points : " + points.toString())
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnAddToCart -> {
                if (btnAddToCart.text.toString() == getString(R.string.add_to_cart)) {
                    retailViewModel.add_cart(id, "1", serviceId)
                    btnAddToCart.setText(getString(R.string.added_to_cart))
                } else {

                }
            }

            rlFooter -> {
                startActivity(Intent(this, MyBucketActivity::class.java))
            }
        }
    }

    fun startPayment() {

        val activity = this

        val co = Checkout()

        try {
            val options = JSONObject()
//            options.put("name", "Sportzax")
//            options.put("description", "")
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", "http://18.222.61.180:3000/images/logo.png")
            options.put("currency", "INR")

            //val payment = needToDeposit

            var total1 = 0
            total1 = (total.toFloat() * 100).toInt()
            options.put("amount", total1)

            val preFill = JSONObject()
            preFill.put("email", prefs.getLoginModel().email)
            preFill.put("contact", prefs.getLoginModel().mobile)

            options.put("prefill", preFill)

//            val razorpay = RazorpayClient(
//                getString(R.string.razorpay_sandbox_key), getString(
//                    R.string.razorpay_sandbox_secret
//                )
//            )
            GlobalScope.launch {
                val jsonObject = JSONObject()
                jsonObject.put("amount", total1)
                jsonObject.put("currency", "INR")
                jsonObject.put("payment_capture", 1)
                runOnUiThread {
                    btnProceedToPay.visibility = View.GONE
                    pb.visibility = View.VISIBLE
                }
                /*    val order: FtsOptions.Order = razorpay.Orders.create(jsonObject)
                    runOnUiThread {
                        btnProceedToPay.visibility = View.VISIBLE
                        pb.visibility = View.GONE
                    }
                    val id = order.toJson().get("id")
                    options.put("order_id", order.toJson().get("id"))*/

                co.open(activity, options)
            }

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "Payment successfully done! " + p0, Toast.LENGTH_SHORT).show()
        paymentId = p1?.paymentId
        jsonObject.put("size_id", sizeId)
        orders = JSONArray()
        orders.put(jsonObject)
        retailViewModel.pickup(
            serviceId,
            subTotal.toString(),
            "0",
            "0",
            discountType,
            discountAmount,
            total.toString(),
            paymentId,
            offerId,
            orders,
            totalPoints.toString(),
            "",
            sizeId
        )
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            Toast.makeText(this, "Payment error please try again", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e)
        }
    }
}