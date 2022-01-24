package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.EventTicketPromotionListAdapter
import com.themarkettheory.user.ui.main.adapter.VendorTagsAdapter
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_ticket_details.*
import kotlinx.android.synthetic.main.toolbar.*

class TicketDetailsActivity : BaseActivity(), View.OnClickListener {
    lateinit var menuViewModel: MenuViewModel
    var orderId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_details)
        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        if (intent != null) {
            orderId = intent.getStringExtra("orderId")
        }
        tvTitle.text = getString(R.string.ticket_details)

        menuViewModel.ticket_details(orderId)

        getTicketDetailResponse()

        val eventTicketPromotionListAdapter = EventTicketPromotionListAdapter(this)
        rvPartners.adapter = eventTicketPromotionListAdapter
        rvPartners.layoutManager = GridLayoutManager(this, 3)

        ivBack.setOnClickListener(this)
    }

    private fun getTicketDetailResponse() {
        menuViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        menuViewModel.responseGetticketDetail.observe(this, Observer {
            if (it.status!!) {
                ll.visibility = View.VISIBLE
                Glide.with(this).load(it.data?.image).into(ivTicket)
                tvEventName.setText(it.data?.title)
                tvDate.setText(it.data?.date)
                tvStartTime.setText(it.data?.start)
                tvEndTime.setText(it.data?.end)
                tvAddress.setText(it.data?.address)
                tvTicketNo.setText(it.data?.ticketNumber)

                var currency: String? = ""
                var total = 0.0f
                it.data?.tickets?.map {
                    /*currency = it.ticketDetails?.currency
                    total += it.qty!!.times(it.ticketDetails?.price!!.toFloat())*/
                }
                tvTotal.setText(currency + total)
                if (it.data?.order?.status == 1)
                    tvStatus.setText("Confirmed")
                else if (it.data?.order?.status == 0)
                    tvStatus.setText("Pending")
                else if (it.data?.order?.status == 3)
                    tvStatus.setText("Rejected")
                val tagsAdapter = VendorTagsAdapter(this, it.data?.tags!!)
                rvVendorTags.adapter = tagsAdapter
                rvVendorTags.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL, false
                )
            } else {
                Utils.showSnackbar(this, getString(R.string.something_went_wrong))
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}