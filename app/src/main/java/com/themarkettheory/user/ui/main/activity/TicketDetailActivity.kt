package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.TicketDetailEventPassListAdapter
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_ticket_detail.*
import kotlinx.android.synthetic.main.toolbar.*

class TicketDetailActivity : BaseActivity() {
    lateinit var menuViewModel: MenuViewModel
    var orderId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_detail)

        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        if (intent != null) {
            orderId = intent.getStringExtra("orderId")
        }
        tvTitle.setText("Event Detail")

        menuViewModel.ticket_details(orderId)

        getTicketDetailResponse()
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
                nestedScroll.visibility = View.VISIBLE
                tvEventTitle.setText(it.data?.title)
                tvDate.setText(it.data?.date)
                tvTime.setText(it.data?.start + " to " + it.data?.end)
                var currency: String? = ""
                it.data?.tickets?.map {
                    /*currency = it.ticketDetails?.currency*/
                }
                tvSubTotal.setText(currency + it.data?.order?.subtotal)
                tvTaxAmt.setText(currency + it.data?.order?.taxAmount)
                tvTotal.setText(currency + it.data?.order?.total)

                val ticketDetailEventPassListAdapter =
                    TicketDetailEventPassListAdapter(this, it.data?.tickets!!)
                rvTicketDetail.adapter = ticketDetailEventPassListAdapter
                rvTicketDetail.layoutManager = LinearLayoutManager(this)
            } else {
                Utils.showSnackbar(this, getString(R.string.something_went_wrong))
            }
        })
    }
}