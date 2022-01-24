package com.themarkettheory.user.ui.salon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.SalonBookingModel
import com.themarkettheory.user.model.SaloonSpaList
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.adapter.GuestListAdapter
import com.themarkettheory.user.ui.main.adapter.SelectSpecialistListAdapter
import com.themarkettheory.user.viewmodel.SaloonViewModel
import kotlinx.android.synthetic.main.activity_salon_booking_information.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SalonBookingInformationActivity : BaseActivity(), View.OnClickListener {
    private var bookDate: String? = ""
    private var bookTime: String? = ""
    var serviceList = ArrayList<SaloonSpaList>()
    lateinit var adultGuestListAdapter: GuestListAdapter
    lateinit var childGuestListAdapter: GuestListAdapter
    lateinit var saloonViewModel: SaloonViewModel
    var child = 0
    var adults = 0
    var discountType: String? = ""
    var discountAmount: String? = ""
    var paymentId: String? = ""
    var offerId: String? = ""
    var total = 0.0f
    var subTotal = 0.0f
    var taxAmount = 0.0f
    var tax = 0.0f
    var serviceId: String? = ""
    var specialistId: String? = ""
    var date = Date()
    val salonBookingModel = SalonBookingModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salon_booking_information)

        tvTitle.setText(getString(R.string.booking_information))
        saloonViewModel = ViewModelProvider(this).get(SaloonViewModel::class.java)

        if (intent != null) {
            serviceList = intent.getSerializableExtra("serviceList") as ArrayList<SaloonSpaList>
            serviceId = intent.getStringExtra("serviceId")
        }

        adultGuestListAdapter = GuestListAdapter(this, 20, false) {
//            val flags = adultGuestListAdapter.getFlags()
            adults = adultGuestListAdapter.getCurrentPosition() + 1
        }
        rvAdults.adapter = adultGuestListAdapter
        rvAdults.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        childGuestListAdapter = GuestListAdapter(this, 10, false) {
//            val flags = childGuestListAdapter.getFlags()
            child = childGuestListAdapter.getCurrentPosition() + 1
        }
        rvChild.adapter = childGuestListAdapter
        rvChild.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        saloonViewModel.specialists(serviceId)

        saloonViewModel.responseSpecialists.observe(this, Observer {
            if (it.status!!) {
                val selectSpecialistListAdapter =
                    SelectSpecialistListAdapter(this, it.data?.list!!) {
                        specialistId = it.id.toString()
                        salonBookingModel.specialist_id = it.id.toString()
                        salonBookingModel.specialist_name = it.name.toString()
                        salonBookingModel.specialist_image = it.profilePic.toString()
                    }
                rvSpecialist.adapter = selectSpecialistListAdapter
                rvSpecialist.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                if (it.data?.list.isNullOrEmpty()) {
                    tvSelectSpecialist.visibility = View.GONE
                }
            }
        })

        saloonViewModel.responseBookSalon.observe(this, Observer {
            if (it.status!!) {
                startActivity(
                    Intent(this, SalonOrderConfirmationActivity::class.java)
                        .putExtra("orderId", it.data?.id.toString())
                )
            } else {
                Utils.showSnackbar(this, it.message)
            }
        })

        weekCalendar.setOnDateClickListener {
            date = it.toLocalDate().toDate()
            bookDate =
                it.toLocalDate().year.toString() + "-" + (it.toLocalDate().monthOfYear).toString() + "-" + it.toLocalDate().dayOfMonth.toString()
        }

        saloonViewModel.service_details(serviceId)

        saloonViewModel.responseServiceDetail.observe(this, Observer {
            when (it.status!!) {

                0 -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                1 -> {
                    salonBookingModel.serviceName = it.data?.title
                    salonBookingModel.location = it.data?.address
                }
            }
        })

        ivBack.setOnClickListener(this)
        edSelectTime.setOnClickListener(this)
        btnSelectAndContinue.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnSelectAndContinue -> {
                subTotal = 0.0f
                total = 0.0f
                var totalPerson = 0
                var totalPoints = 0
                totalPerson = adults + child
                val jsonArray = JSONArray()
                serviceList.mapIndexed { index, saloonSpaList ->
                    val jsonObject = JSONObject()
                    jsonObject.put("saloon_service_id", saloonSpaList.id)
                    jsonObject.put("price", saloonSpaList.discountedPrice)
                    jsonObject.put("points", saloonSpaList.points)
                    jsonArray.put(jsonObject)
                    subTotal += saloonSpaList.mrpPrice!!.toFloat()
                    totalPoints += saloonSpaList.points!!.toInt()
                }
                total = subTotal

                salonBookingModel.serviceId = serviceId.toString()
//                salonBookingModel.services = jsonArray
                salonBookingModel.adult = adults.toString()
                salonBookingModel.child = child.toString()
                salonBookingModel.date = bookDate
                salonBookingModel.time = bookTime
                salonBookingModel.total = total
                salonBookingModel.totalPoints = totalPoints
                salonBookingModel.subtotal = subTotal
                salonBookingModel.total_person = totalPerson
//                salonBookingModel.serviceList.clear()
//                salonBookingModel.serviceList.addAll(serviceList)

                if (bookDate.isNullOrEmpty()) {
                    Utils.showSnackbar(this, "Please select date")
                } else if (bookTime.isNullOrEmpty()) {
                    Utils.showSnackbar(this, "Please select time")
                } else if (totalPerson == 0) {
                    Utils.showSnackbar(this, "Please select Number of guests")
                } else if (System.currentTimeMillis() - date.time > 0) {
                    Utils.showSnackbar(this, "Please select valid booking date")
                } else {
                    startActivity(
                        Intent(this, SalonOrderConfirmationActivity::class.java)
//                        .putExtra("orderId",it.data?.id.toString())
                            .putExtra("serviceList", serviceList)
                            .putExtra("data", salonBookingModel)
                    )
/*                    saloonViewModel.book_saloon(
                        serviceId,
                        subTotal.toString(),
                        taxAmount.toString(),
                        tax.toString(),
                        discountType,
                        discountAmount,
                        total.toString(),
                        paymentId,
                        offerId,
                        jsonArray,
                        specialistId,
                        bookDate,
                        bookTime,
                        totalPerson.toString(),
                        adults.toString(),
                        child.toString(),
                        "",
                        edSpecialRequest.text.toString()
                    )*/
                }
            }

            edSelectTime -> {
                Utils.setTimePickerDialog(this, edSelectTime) {
                    bookTime = it
                }
            }
        }
    }
}