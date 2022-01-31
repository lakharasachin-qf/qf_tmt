package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.razorpay.*
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.viewmodel.EventsViewModel
import kotlinx.android.synthetic.main.activity_select_category.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject

class SelectCategoryActivity : BaseActivity(), View.OnClickListener, PaymentResultWithDataListener {
    /*private lateinit var eventPassListAdapter: EventPassListAdapter*/
    var eventId: String? = ""
    var discountType: String? = ""
    var discountAmount: String? = ""
    var paymentId: String? = ""
    var offerId: String? = ""
    var currency_str: String? = ""
    var discountedFinalAmount = 0.0f
    var total = 0.0f
    var tax = 0.0f
    var taxAmount = 0.0f
    var subTotal = 0.0f
    val ticket = JSONArray()
    var currency: String? = ""
    lateinit var eventsViewModel: EventsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_category)

        if (intent != null) {
            eventId = intent.getStringExtra("eventId")
        }
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        tvTitle.text = getString(R.string.select_no_of_guests)

        getEventResponse()

        eventsViewModel.get_event_details(eventId, "23.0501", "72.6315")

        ivBack.setOnClickListener(this)
        btnProceedToPay.setOnClickListener(this)
        rlApplyPromocode.setOnClickListener(this)
        ivAction.setOnClickListener(this)
    }

    private fun getEventResponse() {
        eventsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        eventsViewModel.responseEventDetail.observe(this, Observer {
            if (it.status!!) {
                nestedScroll.visibility = View.VISIBLE
                tvEventTitle.setText(it.data?.title)
                tvAddress.setText(it.data?.address)
                currency_str = it.data?.currency_str
                tvDate.setText(
                    Utils.formatDate(
                        it.data?.start!!,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM, yyyy"
                    )
                )
                val startStr = Utils.formatDate(it.data?.start!!, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
                val endStr = Utils.formatDate(it.data?.end!!, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
                tvTime.setText("$startStr - $endStr")

                /*eventPassListAdapter = EventPassListAdapter(this, it.data?.prices!!){
                    updateUI(it)
                }
                rvEventPass.adapter = eventPassListAdapter
                rvEventPass.layoutManager = LinearLayoutManager(this)
                tax =  it.data?.tax!![0].tax!!.toFloat()
                it.data?.prices?.let {
                    updateUI(it)
                }*/
            }
        })

        eventsViewModel.responseCreateEvent.observe(this, Observer {
            if (it.status!!) {
                Utils.showToast(this, "Ticket purchased successfully")
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }, 2000)
            } else {
                Utils.showSnackbar(this, it.message)
            }
        })
    }

    /*private fun updateUI(it: List<Price>) {
        var subTotal = 0.0f
        total = 0.0f
        taxAmount = 0.0f
        var points = 0

        it.mapIndexed { index, price ->
            subTotal += price.qty!!.times(price?.price!!.toFloat())
//            points += price.qty!!.toInt().times(price?.p!!)
            currency =  price.currency
        }
        tvSubTotal.setText(currency+subTotal)
        discountedFinalAmount = Utils.getDiscountAmount(discountType,discountAmount,subTotal)
        taxAmount = subTotal.times(tax).div(100)
        total = subTotal.plus(taxAmount).minus(discountedFinalAmount)
        tvTaxAmt.setText(currency+taxAmount)
        tvTotal.setText(currency+total)
        if(discountedFinalAmount == 0.0f){
            flDiscountedAmount.visibility = View.GONE
        }else {
            tvDiscount.setText(currency+discountedFinalAmount.toString())
            tvTotal.setText(currency+total.toString())
            flDiscountedAmount.visibility = View.VISIBLE
        }
    }*/

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnProceedToPay -> {
                /*if(::eventPassListAdapter.isInitialized) {
                    *//*val passData = eventPassListAdapter.getPassData()
//                    subTotal = 0.0f
                    passData.mapIndexed { index, price ->
                        if (price.qty!!.toInt() > 0) {
                            val jsonobject = JSONObject()
                            jsonobject.put("event_price_id", price.id)
                            jsonobject.put("qty", price.qty)
                            jsonobject.put("price", price.price)
                            ticket.put(jsonobject)

//                            subTotal += price.qty!!.toFloat().times(price.price!!.toFloat())
                        }
                    }*//*

                    *//*if (!discountAmount.isNullOrEmpty()) {
                        total = total - discountAmount!!.toFloat()
                    }*//*
                    if (ticket.length() == 0) {
                        Utils.showToast(this, getString(R.string.please_select_atleast_one_ticket))
                    } else {
                        startPayment()
//                    startActivity(Intent(this, PaymentMethodActivity::class.java))
                    }
                }*/
            }

            ivAction -> {
                ivAction.setImageResource(R.drawable.ic_arrow)
                tvCouponCode.setText(getString(R.string.apply_promo_code))
                discountType = ""
                discountAmount = ""
                offerId = ""
                total += discountedFinalAmount
                discountedFinalAmount = 0.0f
                tvTotal.setText(currency + total.toString())
                flDiscountedAmount.visibility = View.GONE
            }

            rlApplyPromocode -> {
                startActivityForResult(
                    Intent(this, PromoCodeActivity::class.java)
                        .putExtra("type", "event")
                        .putExtra("id", eventId), 2
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 2) {
                discountType = data.getStringExtra("discountType")
                discountAmount = data.getStringExtra("discountAmount")
                offerId = data.getStringExtra("offerId")
                val couponCode = data.getStringExtra("couponCode")
                tvCouponCode.setText("Applied Coupon : $couponCode")
                ivAction.setImageResource(R.drawable.close_button)
                /*discountedFinalAmount = Utils.getDiscountAmount(discountType,discountAmount,subTotal)
                total -= discountedFinalAmount
                tvCouponCode.setText("Applied Coupon : "+couponCode)
                ivAction.setImageResource(R.drawable.close_button)
                tvTotal.setText(currency + total.toString())
                if(discountedFinalAmount == 0.0f){
                    flDiscountedAmount.visibility = View.GONE
                }else {
                    tvDiscount.setText(currency+discountAmount.toString())
                    tvTotal.setText(currency+total.toString())
                    flDiscountedAmount.visibility = View.VISIBLE
                }*/
                /*updateUI(eventPassListAdapter.getPassData())*/
            }
        }
    }

    fun startPayment() {

        val activity = this

        val co = Checkout()
        co.setKeyID(getString(R.string.razorpay_live_key))

        try {
            val options = JSONObject()
            options.put("currency", currency_str)

            //val payment = needToDeposit

            var total1 = 0
            total1 = (total * 100).toInt()
            options.put("amount", total1)

            val preFill = JSONObject()
            preFill.put("email", prefs.getLoginModel().email)
            preFill.put("contact", prefs.getLoginModel().mobile)

            options.put("prefill", preFill)

//            val razorpay = RazorpayClient(
//                getString(R.string.razorpay_live_key), getString(
//                    R.string.razorpay_live_secret
//                )
//            )
            /*   GlobalScope.launch {
                   val jsonObject = JSONObject()
                   jsonObject.put("amount", total1)
                   jsonObject.put("currency", currency_str)
                   jsonObject.put("payment_capture", 1)
                   runOnUiThread {
                       btnProceedToPay.visibility = View.GONE
                       pb.visibility = View.VISIBLE
                   }
                   val order: FtsOptions.Order = razorpay.Orders.create(jsonObject)
                   runOnUiThread {
                       btnProceedToPay.visibility = View.VISIBLE
                       pb.visibility = View.GONE
                   }
                   val id = order.toJson().get("id")
                   options.put("order_id", order.toJson().get("id"))

               }*/
            co.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
//        Toast.makeText(this, "Payment successfully done! " + p0, Toast.LENGTH_SHORT).show()
        paymentId = p1?.paymentId
        eventsViewModel.participate(
            eventId,
            subTotal.toString(),
            taxAmount.toString(),
            tax.toString(),
            discountType,
            discountAmount,
            total.toString(),
            paymentId,
            offerId,
            ticket
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