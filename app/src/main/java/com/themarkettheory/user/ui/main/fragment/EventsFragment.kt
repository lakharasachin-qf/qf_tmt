package com.themarkettheory.user.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.activity.MyAddressActivity
import com.themarkettheory.user.ui.main.activity.NotificationActivity
import com.themarkettheory.user.ui.main.activity.PopularEventsActivity
import com.themarkettheory.user.ui.main.activity.SearchActivity
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.viewmodel.EventsViewModel
import com.themarkettheory.user.viewmodel.FavoriteViewModel
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.fragment_events.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventsFragment : BaseFragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var eventsViewModel: EventsViewModel
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var generalViewModel: GeneralViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        generalViewModel = ViewModelProvider(requireActivity()).get(GeneralViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsViewModel.get_events()

        getEventsResponse()

        tvAddress.setText(prefs.getLoginModel().address)

        llSearch.setOnClickListener(this)
        ivNotification.setOnClickListener(this)
        tvEventsSeeAll.setOnClickListener(this)
        tvAddress.setOnClickListener(this)
        coCart.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        generalViewModel.get_cart()
//        generalViewModel.profile()
    }

    private fun getEventsResponse() {
        eventsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        eventsViewModel.responseGetEvents.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                llEvents.visibility = View.VISIBLE
                /*val upcomingListAdapter = UpcomingListAdapter(requireActivity(),it.data?.upcomingEvent!!){
                    favoriteViewModel.favourite(it.id.toString())
                }
                viewpager_upcomingEvents.adapter = upcomingListAdapter*/
                viewpager_upcomingEvents.setPadding(40, 0, 40, 0)
                viewpager_upcomingEvents.pageMargin = 10;
                viewpager_upcomingEvents.clipToPadding = false

                /*val popularEventsAdapter = PopularEventsAdapter(requireActivity(),it.data?.popularEvent!!){
                    startActivity(Intent(activity, EventDetailActivity::class.java)
                        .putExtra("eventId",it.id.toString()))
                }
                rvPopularEvents.adapter = popularEventsAdapter*/
                rvPopularEvents.layoutManager = LinearLayoutManager(requireActivity())
            }
        })

        generalViewModel.responseGetProfile.observe(viewLifecycleOwner, Observer {
            if (it.status!! == 1) {
                /*if(it.data?.address?.houseNumber.isNullOrEmpty() &&it.data?.address?.tower.isNullOrEmpty()){
                    tvAddress.setText(Utils.getAddress(it.data!!))
                }*/
                tvAddress.text = Utils.getAddress(it.data!!)
            }
        })

        generalViewModel.responseGetCart.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                if (it.data?.list.isNullOrEmpty()) {
                    tvCartCount.visibility = View.GONE
                } else {
                    tvCartCount.visibility = View.VISIBLE
                    tvCartCount.setText(it.data?.list!!.size.toString())
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventsFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v) {
            llSearch -> {
                val activityOptionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        llSearch,
                        getString(R.string.search)
                    )
                val intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent, activityOptionsCompat.toBundle())
            }

            tvEventsSeeAll -> {
                startActivity(Intent(requireActivity(), PopularEventsActivity::class.java))
            }

            coCart -> {
                startActivity(Intent(requireActivity(), MyBucketActivity::class.java))
            }

            ivNotification -> {
                startActivity(Intent(requireActivity(), NotificationActivity::class.java))
            }

            tvAddress -> {
                startActivity(Intent(requireActivity(), MyAddressActivity::class.java))
            }
        }
    }
}