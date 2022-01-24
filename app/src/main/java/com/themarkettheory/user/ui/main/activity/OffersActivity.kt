package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.viewmodel.HomeViewModel
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.toolbar.*

class OffersActivity : BaseActivity(), View.OnClickListener {
    lateinit var homeViewModel: HomeViewModel
    lateinit var offerViewModel: OfferViewModel
    var service_id: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        if (intent != null) {
            service_id = intent.getStringExtra("service_id")
        }

        tvTitle.setText(getString(R.string.trending_offers))
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        getOfferResponse()

        ivBack.setOnClickListener(this)
    }

    private fun getOfferResponse() {
        homeViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        homeViewModel.view_all_offers(service_id)

        /* homeViewModel.responseAllOffers.observe(this, Observer {
             if(it.status!!){
                 val trendingOfferListAdapter = TrendingOfferListAdapter(this,it.data?.list!!,false){
                     offerViewModel.activate_coupon(it.id.toString())
                 }
                 rvOffers.adapter = trendingOfferListAdapter
                 rvOffers.layoutManager = LinearLayoutManager(this)
             }
         })*/

        offerViewModel.isLoadingActivateCoupon.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        offerViewModel.responseActivateCoupon.observe(this, Observer {
            if (it.status!!) {
                Utils.showToast(this, "Coupon activated")
            } else {
                Utils.showSnackbar(this, it.message)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}