package com.themarkettheory.user.ui.salon

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_salon_order_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class SalonOrderDetailActivity : BaseActivity(), View.OnClickListener {
    lateinit var menuViewModel: MenuViewModel
    var orderId: String? = ""
    var bookingId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salon_order_detail)
        if (intent != null) {
            orderId = intent.getStringExtra("orderId")
        }
        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        tvTitle.setText("My Booking")

        menuViewModel.mybooking_details(orderId)
        getBookingDetailResponse()

        ivBack.setOnClickListener(this)
        btnCancelBooking.setOnClickListener(this)
    }

    private fun getBookingDetailResponse() {
        /* menuViewModel.responseBookingDetail.observe(this, Observer {
             if(it.status!!){
                 ll.visibility = View.VISIBLE
                 bookingId = it.data?.id.toString()
                 tvServiceName.setText(it.data?.title)
                 tvAddress.setText(it.data?.address)
                 edSpecialInstruction.setText(it.data?.specialRequest)
                 tvDateTime.setText(  Utils.formatDate(
                     it.data?.bookingDate!!, "yyyy-MM-dd", "dd MMMM, yyyy") +" | "+it.data?.bookingTime)
                 if (it.data?.specialist?.id == null){
                     llSpecialist.visibility = View.GONE
                 }else{
                     Glide.with(this).load(it.data?.specialist?.profilePic).into(ivSpecialistProfile)
                     tvSpecialistName.setText(it.data?.specialist?.name)
                 }
                 if (it.data?.status == 0) {
                     btnCancelBooking.visibility = View.VISIBLE
                 } else {
                     btnCancelBooking.visibility = View.GONE
                 }
                 val salonBookingServicesAdapter = SalonBookingServicesAdapter(this,it.data?.services!!)
                 rvSalonServices.adapter = salonBookingServicesAdapter
                 rvSalonServices.layoutManager = LinearLayoutManager(this)

             }else{
                 Utils.showSnackbar(this,it.message)
             }
         })

         menuViewModel.responseCancelOrder.observe(this, Observer {
             if (it.status!!) {
                 Utils.showToast(this, "Booking canceled")
                 Handler().postDelayed({
                     onBackPressed()
                 }, 2000)
             } else {
                 Utils.showSnackbar(this, it.message)
             }
         })*/
    }

    override fun onClick(v: View?) {
        when (v) {
            btnCancelBooking -> {
                Utils.showDialog(
                    this,
                    getString(R.string.alert),
                    "Are you sure you want to cancel this booking?"
                ) {
                    menuViewModel.cancel_table(bookingId)
                }
            }

            ivBack -> onBackPressed()
        }
    }
}