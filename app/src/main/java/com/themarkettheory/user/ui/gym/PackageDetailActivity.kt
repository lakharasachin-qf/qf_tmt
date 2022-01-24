package com.themarkettheory.user.ui.gym

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.GymList
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.activity.MainActivity
import com.themarkettheory.user.ui.main.adapter.OpenDaysAdapter
import com.themarkettheory.user.viewmodel.GymViewModel
import kotlinx.android.synthetic.main.activity_package_detail.*

class PackageDetailActivity : BaseActivity(), View.OnClickListener {
    var gymList: GymList? = null
    lateinit var gymViewModel: GymViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_detail)

        gymViewModel = ViewModelProvider(this).get(GymViewModel::class.java)
        if (intent != null) {
            gymList = intent.getSerializableExtra("data") as GymList?
        }

        if (gymList != null) {
            Glide.with(this).load(gymList?.image).into(ivGym)
            tvGymTitle.setText(gymList?.title)
            tvDesc.setText(gymList?.description)
            tvDay.setText("Day: " + gymList?.openDays)
            tvtime.setText("Time: " + gymList?.openTime)
            val openDaysAdapter = OpenDaysAdapter(this, gymList?.openDays!!) {

            }
            rvOpenDays.adapter = openDaysAdapter
            rvOpenDays.layoutManager = LinearLayoutManager(this)

            if (gymList?.discountedPrice.isNullOrEmpty()) {
                tvtotalCost.setText("Total Cost: " + gymList?.price + "/" + gymList?.duration)
            } else {
                tvtotalCost.setText("Total Cost: " + gymList?.discountedPrice + "/" + gymList?.duration)
            }
        }

        getResponse()
        ivBack.setOnClickListener(this)
        btnGetQuotes.setOnClickListener(this)
    }

    private fun getResponse() {
        gymViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                btnGetQuotes.startAnimation()
            } else {
                btnGetQuotes.stopAnimation()
            }
        })

        gymViewModel.responseGymPackageEnquiry.observe(this, Observer {
            if (it.status!!) {
                Toast.makeText(
                    this,
                    gymList?.service_name + " will reach you soon",
                    Toast.LENGTH_LONG
                ).show()
                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }, 2000)
            } else {
                Utils.showSnackbar(this, getString(R.string.something_went_wrong))
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnGetQuotes -> {
                gymViewModel.gym_enquiry(
                    gymList?.id.toString(),
                    gymList?.serviceId.toString(),
                    "1",
                    edDetails.text.toString()
                )
            }
        }
    }
}