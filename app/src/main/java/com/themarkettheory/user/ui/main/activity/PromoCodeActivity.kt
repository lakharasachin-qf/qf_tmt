package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.OfferDatum
import com.themarkettheory.user.ui.main.adapter.PromoCodeListAdapter
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_promo_code.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.tvTitle

class PromoCodeActivity : BaseActivity(), View.OnClickListener {
    lateinit var offerViewModel: OfferViewModel
    var id: String? = ""
    var type: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_code)

        tvTitle.text = getString(R.string.promo_code)
        if (intent != null) {
            id = intent.getStringExtra("id")
            type = intent.getStringExtra("type")
        }
        offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)

        offerViewModel.offer_list(id, type)

        getOfferResponse()

        btnApply.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    private fun getOfferResponse() {
        offerViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        offerViewModel.responseOffers.observe(this, Observer {
            /*if (it.status!!) {
                val promoCodeListAdapter = PromoCodeListAdapter(this, it.data!!) {
                    val data = Intent()
                    data.putExtra("discountType", it.discountType)
                    data.putExtra("discountAmount", it.discountAmount)
                    data.putExtra("offerId", it.id.toString())
                    data.putExtra("couponCode", it.couponCode)
                    data.putExtra("offerDatum", it)
                    setResult(2, data)
                    finish()
                }
                rvPromoCode.adapter = promoCodeListAdapter
                rvPromoCode.layoutManager = LinearLayoutManager(this)
            } else {
                Utils.showFlashBar(this, it.message)
            }*/
        })

        offerViewModel.responseCheckPromode.observe(this, Observer {
            if (it.status!!) {
                if (it.data?.id != null) {
                    val list = ArrayList<OfferDatum>()
                    it.data?.let {
                        list.add(it)
                    }
                    val promoCodeListAdapter = PromoCodeListAdapter(this, list) {
                        val data = Intent()
                        data.putExtra("discountType", it.discountType.toString())
                        data.putExtra("discountAmount", it.discountAmount.toString())
                        data.putExtra("offerId", it.id.toString())
                        data.putExtra("couponCode", it.couponCode)
                        data.putExtra("offerDatum", it)
                        setResult(2, data)
                        finish()
                    }
                    rvPromoCode.adapter = promoCodeListAdapter
                    rvPromoCode.layoutManager = LinearLayoutManager(this)
                    /*val data = Intent()
                    data.putExtra("discountType",it.data?.discountType)
                    data.putExtra("discountAmount",it.data?.discountAmount)
                    setResult(2,data)
                    finish()*/
                }
            } else {
                Utils.showFlashBar(this, it.message)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            btnApply -> {
                if (!edPromoCode.text.toString().isNullOrEmpty()) {
                    /*CommonUtils.hideKeyboard(this,edPromoCode)*/
                    PubFun.hideKeyboard(this@PromoCodeActivity)
                    offerViewModel.check_promocode(edPromoCode.text.toString(), id, type)
                }
            }

            ivBack -> onBackPressed()
        }
    }
}