package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.viewmodel.EventsViewModel
import kotlinx.android.synthetic.main.activity_popular_events.*
import kotlinx.android.synthetic.main.toolbar.*

class PopularEventsActivity : BaseActivity(), View.OnClickListener {
    lateinit var eventsViewModel: EventsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_events)

        tvTitle.setText(getString(R.string.popular_events))
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        getPopularEventsResponse()

        ivBack.setOnClickListener(this)
    }

    private fun getPopularEventsResponse() {
        eventsViewModel.get_events()

        eventsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        eventsViewModel.responseGetEvents.observe(this, Observer {
            if (it.status!!) {
                /*val popularEventsAdapter = PopularEventsAdapter(this,it.data?.popularEvent!!){
                    startActivity(Intent(this,EventDetailActivity::class.java)
                        .putExtra("eventId",it.id.toString()))
                }
                rvPopularEvents.adapter = popularEventsAdapter*/
                rvPopularEvents.layoutManager = LinearLayoutManager(this)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}