package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.toolbar.*

class EventsActivity : BaseActivity(), View.OnClickListener {
    lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        tvTitle.text = getString(R.string.events)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        getEventResponse()

        ivBack.setOnClickListener(this)
    }

    private fun getEventResponse() {
        homeViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        /*homeViewModel.view_all_event()

        homeViewModel.responseAllEvents.observe(this, Observer {
            val allEventsAdapter = AllEventsAdapter(this,it.data?.list!!){
                homeViewModel.favourite(it.id.toString())
            }
            rvEvents.adapter = allEventsAdapter
            rvEvents.layoutManager = LinearLayoutManager(this)
        })*/
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}