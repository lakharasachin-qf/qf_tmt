package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.ParticipantListAdapter
import com.themarkettheory.user.viewmodel.EventsViewModel
import kotlinx.android.synthetic.main.activity_event_detail.*

class EventDetailActivity : BaseActivity(), View.OnClickListener {
    var eventId: String? = ""
    lateinit var eventsViewModel: EventsViewModel
    var is_liked: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        if (intent != null) {
            eventId = intent.getStringExtra("eventId")
        }
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        eventsViewModel.get_event_details(eventId, "23.0501", "72.6315")

        getEventResponse()

        ivBack.setOnClickListener(this)
        btnBookNow.setOnClickListener(this)
        ivFavorite.setOnClickListener(this)
    }

    private fun getEventResponse() {
        eventsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        eventsViewModel.responseEventDetail.observe(this, Observer {
            if (it.status!!) {
                rl.visibility = View.VISIBLE
                Glide.with(this).load(it.data?.image).into(ivEvent)
                tvTitle.text = it.data?.title
                tvTags.setText(it.data?.tags)
                tvAddress.setText(it.data?.address)
                tvDistance.setText(it.data?.distance)
                tvDescr.setText(it.data?.description)
                tvTotalFavorites.setText(it.data?.favourite.toString())
                is_liked = it.data?.is_liked
                if (is_liked == 1) {
                    ivFavorite.setImageResource(R.drawable.ic_like_selected)
                } else {
                    ivFavorite.setImageResource(R.drawable.ic_like_unselected)
                }

                tvDate.setText(
                    Utils.formatDate(
                        it.data?.start!!,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM, yyyy '-' hh:mm a"
                    )
                )
                /*val min: Float? = it.data?.prices?.minOfOrNull { it.price!!.toFloat() }

                if (it.data.prices.isNullOrEmpty()) {
                    flBottom.visibility = View.GONE
                } else {
                    tvPrice.text = it.data?.prices!![0].currency + min.toString()
                }*/

                if (it.data?.participants.isNullOrEmpty()) {
                    tvParticipants.visibility = View.GONE
                } else {
                    tvParticipants.visibility = View.VISIBLE
                }

                val participantListAdapter = ParticipantListAdapter(this, it.data?.participants!!)
                rvParticipants.adapter = participantListAdapter
                rvParticipants.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                /* val crossPromotionListAdapter = CrossPromotionListAdapter(this)
                 rvCrossPromotionPartners.adapter = crossPromotionListAdapter
                 rvCrossPromotionPartners.layoutManager =
                     LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)*/
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnBookNow -> {
                startActivity(
                    Intent(this, SelectCategoryActivity::class.java)
                        .putExtra("eventId", eventId)
                )
            }

            ivFavorite -> {
                if (is_liked == 0) {
                    ivFavorite.setImageResource(R.drawable.ic_like_selected)
                } else {
                    ivFavorite.setImageResource(R.drawable.ic_like_unselected)
                }
                is_liked = is_liked!!.xor(1)
                eventsViewModel.favourite_event(eventId)
            }
        }
    }
}