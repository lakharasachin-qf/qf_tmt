package com.themarkettheory.user.ui.main.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.NotificationCouponCodesAdapter
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.fragment_notification_coupon_codes.*
import kotlinx.android.synthetic.main.fragment_notiification_alert.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationCouponCodesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationCouponCodesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var generalViewModel: GeneralViewModel

    @Inject
    lateinit var gson: Gson
    lateinit var offerViewModel: OfferViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_coupon_codes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gson = Gson()
        generalViewModel.get_notifications_offerList()
        getResponse()
        //offerViewModel.get_activated_coupons()
    }

    private fun getResponse() {
        generalViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {

                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        generalViewModel.responseNotificationsOffer.observe(viewLifecycleOwner, Observer {

            if (it.status == 1) {
                val notificationCouponCodesAdapter =
                    it.data?.let { it1 ->
                        NotificationCouponCodesAdapter(
                            requireActivity(), it1, "",
                            0,
                            "",
                            ""
                        )
                    }
                Log.e("CouponData List Data:", Config.gson.toJson(it.data))
                rvCoupons.adapter = notificationCouponCodesAdapter
                rvCoupons.layoutManager = LinearLayoutManager(activity)
            } else {
                Utils.showSnackbar(requireActivity(), it.message)
            }
        })

//        offerViewModel.responseActivatedCoupons.observe(viewLifecycleOwner, Observer {
//            if (it.status!!) {
//                val notificationCouponCodesAdapter =
//                    NotificationCouponCodesAdapter(requireActivity(), it.data!!)
//
//                Log.e("Coupon", it.data.toString())
//                rvCoupons.adapter = notificationCouponCodesAdapter
//                rvCoupons.layoutManager = LinearLayoutManager(activity)
//            }
//        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationCouponCodesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationCouponCodesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}