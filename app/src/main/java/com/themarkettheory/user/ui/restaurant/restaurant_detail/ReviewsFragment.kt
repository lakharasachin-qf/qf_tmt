package com.themarkettheory.user.ui.restaurant.restaurant_detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.review.NewReviewDataRes
import com.themarkettheory.user.newmodels.review.ReviewData
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.ReviewListAdapter
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.fragment_review_new.*
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewsFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var id: String? = null
    private var param2: String? = null
    private var totalRating = 0.0

    @Inject
    lateinit var gson: Gson
    private lateinit var myReviewList: ArrayList<ReviewData>
    lateinit var vendorDetailViewModel: VendorDetailViewModel

    private val numberFormat: NumberFormat = DecimalFormat("#.0")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
    }

    private fun showFooter() {
        try {
            if (Config.inBucketPoints.toInt() > 0 && Config.inBucketCount.toInt() > 0) {
                if (rlReviewFooter == null) return
                rlReviewFooter.visibility = View.VISIBLE
                tvReviewNoOfItems.text = Config.inBucketCount
                tvReviewTotalPoints.text = "Total Points : ${Config.inBucketPoints}"
            } else {
                rlReviewFooter.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        showFooter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // calling api for data
        gson = GsonBuilder()
            .setLenient()
            .create()
        // Log.e("review Id:", gson.toJson(vendorDetailViewModel.review(id)))
        vendorDetailViewModel.review(id)

        vendorDetailViewModel.responseReviews.observe(viewLifecycleOwner, Observer {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> populateData(it)
            }
        })

        tvReviewNext.setOnClickListener(this)
    }

    private fun populateData(res: NewReviewDataRes) {

        try {

            //  Log.e("Review Data", gson.toJson(res.data))

            if (res.data!!.isNotEmpty()) {

                Log.e("totalMyReviewData", gson.toJson(res.data!!))
                //setting total no of rating
                tvReviewTotalText.setText(" ${res.data!!.size} Reviews")
                totalRating = 0.0
                for (i in res.data!!.indices) {
                    totalRating += res.data!![i].rating!!.toDouble()
                }
                // setting stars
                val avgRating = totalRating / res.data!!.size
                rbTotalStart.rating = avgRating.toFloat()

                // setting no of avg rating
                tvReviewTotalPoint.setText(numberFormat.format(avgRating))

                val reviewListAdapter = ReviewListAdapter(requireActivity())
                rvReviews.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = reviewListAdapter
                }
                if (res.data!!.isNotEmpty()) {
                    myReviewList = res.data!! as ArrayList<ReviewData>
                    reviewListAdapter.addTotalPointList(myReviewList)
                    noDataFound.visibility = View.GONE
                    rvReviews.visibility = View.VISIBLE
                } else {
                    rvReviews.visibility = View.GONE
                    noDataFound.visibility = View.VISIBLE

                }
            } else {
                noDataFound.visibility = View.VISIBLE

                rvReviews.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReviewsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            ReviewsFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(requireActivity())
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Review"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                            } else {
                                i--
                                postDelayed(this, 500)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            tvReviewNext -> {
                startActivity(Intent(activity, MyBucketActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}