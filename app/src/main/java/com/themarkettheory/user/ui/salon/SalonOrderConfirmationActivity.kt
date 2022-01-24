package com.themarkettheory.user.ui.salon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.SalonBookingModel
import com.themarkettheory.user.model.SaloonSpaList
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.activity.MainActivity
import com.themarkettheory.user.ui.main.adapter.SalonSelectedServicesAdapter
import com.themarkettheory.user.viewmodel.SaloonViewModel
import kotlinx.android.synthetic.main.activity_salon_order_confirmation.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class SalonOrderConfirmationActivity : BaseActivity(), View.OnClickListener {
    //    var orderId:String? = ""
    var salonBookingModel: SalonBookingModel? = SalonBookingModel()
    lateinit var saloonViewModel: SaloonViewModel
    var serviceList = ArrayList<SaloonSpaList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salon_order_confirmation)

        saloonViewModel = ViewModelProvider(this).get(SaloonViewModel::class.java)
        if (intent != null) {
//            orderId = intent.getStringExtra("orderId")
            salonBookingModel = intent.getSerializableExtra("data") as SalonBookingModel?
            serviceList = intent.getSerializableExtra("serviceList") as ArrayList<SaloonSpaList>
        }
        tvTitle.setText(getString(R.string.order_confirmation))

        if (salonBookingModel != null) {
            tvServiceName.setText(salonBookingModel?.serviceName)
            tvAddress.setText(salonBookingModel?.location)
            tvDateTime.setText(
                Utils.formatDate(
                    salonBookingModel?.date!!,
                    "yyyy-MM-dd",
                    "dd MMM, yyyy"
                ) + " | " + salonBookingModel?.time
            )
            tvSpecialistName.setText(salonBookingModel?.specialist_name)
            tvOrderPoints.setText(salonBookingModel?.totalPoints.toString())
            Glide.with(this).load(salonBookingModel?.specialist_image).into(ivSpecialistProfile)

            val salonSelectedServicesAdapter = SalonSelectedServicesAdapter(this, serviceList)
            rvSalonServices.adapter = salonSelectedServicesAdapter
            rvSalonServices.layoutManager = LinearLayoutManager(this)

            if (salonBookingModel?.specialist_id.isNullOrEmpty()) {
                llSpecialist.visibility = View.GONE
            }
        }

        saloonViewModel.responseBookSalon.observe(this, Observer {
            Utils.showSnackbar(this, it.message)
            if (it.status!!) {
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }, 2000)

            } else {
//                Utils.showSnackbar(this,it.message)
            }
        })

        saloonViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                btnConfirmYourOrder.startAnimation()
            } else {
                btnConfirmYourOrder.stopAnimation()
                btnConfirmYourOrder.revertAnimation()
            }
        })

        ivBack.setOnClickListener(this)
        btnConfirmYourOrder.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnConfirmYourOrder -> {
                val jsonArray = JSONArray()
                serviceList?.mapIndexed { index, saloonSpaList ->
                    val jsonObject = JSONObject()
                    jsonObject.put("saloon_service_id", saloonSpaList.id)
                    jsonObject.put("price", saloonSpaList.discountedPrice)
                    jsonObject.put("points", saloonSpaList.points)
                    jsonArray.put(jsonObject)
//                    subTotal += saloonSpaList.mrpPrice!!.toFloat()
//                    totalPoints += saloonSpaList.points!!.toInt()
                }

                saloonViewModel.book_saloon(
                    salonBookingModel?.serviceId,
                    "0",
                    "0",
                    "0",
                    "0",
                    "0",
                    salonBookingModel?.total.toString(),
                    "",
                    "0",
                    jsonArray,
                    salonBookingModel?.specialist_id,
                    salonBookingModel?.date,
                    salonBookingModel?.time,
                    salonBookingModel?.total_person.toString(),
                    salonBookingModel?.adult,
                    salonBookingModel?.child,
                    "0",
                    salonBookingModel?.special_instruction
                )
            }
        }
    }
}