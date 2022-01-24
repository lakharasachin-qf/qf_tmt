package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.MyEventsAdapter
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_my_events.*
import kotlinx.android.synthetic.main.toolbar.*

class MyEventsActivity : BaseActivity(), View.OnClickListener {
    lateinit var menuViewModel: MenuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        tvTitle.setText(getString(R.string.my_events))
        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

        getEventResponse()
        menuViewModel.myEvents()
        ivBack.setOnClickListener(this)
    }

    private fun getEventResponse() {
        menuViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        menuViewModel.responseEvents.observe(this, Observer {
            if (it.status!!) {
                val myEventsAdapter = MyEventsAdapter(this, it.data?.list!!) {
                    startActivity(
                        Intent(this, TicketDetailsActivity::class.java)
                            .putExtra("orderId", it.id.toString())
                    )
                }
                rvMyEvents.adapter = myEventsAdapter
                rvMyEvents.layoutManager = LinearLayoutManager(this)
            } else {

            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}