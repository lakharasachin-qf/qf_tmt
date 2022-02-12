package com.themarkettheory.user.ui.main.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.GsonBuilder
import com.makeramen.roundedimageview.RoundedImageView
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.network.ApiService
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.coupon.CouponActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.*
import com.themarkettheory.user.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_menu.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : BaseFragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /*Profile View Model*/
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var ivBackgroundMenu: ImageView
    private lateinit var ivProfileMenu: RoundedImageView
    lateinit var userName: String
    lateinit var zip: String
    lateinit var dob: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        /*Initialize Profile View Model*/
        profileViewModel =
            ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        requireActivity().supportFragmentManager.beginTransaction().attach(this).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            Log.e("onViewCreated", "onViewCreated call")

            ivProfileMenu = requireActivity().findViewById(R.id.ivProfileMenu)
            ivBackgroundMenu = requireActivity().findViewById(R.id.ivBackground)
            /*Initialize Gson and MyRoomDb*/
            gson = GsonBuilder().serializeNulls().create()
            myRoomDatabase = MyRoomDatabase.getDB(requireActivity())!!

            //init()
            if (PubFun.isInternetConnection(requireActivity())) {
                profileViewModel.profileNew()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
            /*call profile API*/

            flTotalPoints.setOnClickListener(this)
            flCoupons.setOnClickListener(this)
            ivProfileMenu.setOnClickListener(this)
            flMyOrders.setOnClickListener(this)
            flMyBookings.setOnClickListener(this)
            flMyEvents.setOnClickListener(this)
            flReferAndEarn.setOnClickListener(this)
            flSwitchToBusinessProfile.setOnClickListener(this)
            flRate.setOnClickListener(this)
            flSupport.setOnClickListener(this)
            flSettings.setOnClickListener(this)
            flCollectStamp.setOnClickListener(this)
            flSignout.setOnClickListener(this)
            ivEditProfile.setOnClickListener(this)
            ivMenuEditProfile.setOnClickListener(this)

            flReferAndEarn.visibility=View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            Log.e("onResume", "onResume call")
            requireActivity().supportFragmentManager.beginTransaction().attach(this).commit()
            init()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {

        /*Profile View Model Progress Bar Loading */
        profileViewModel.isLoading.observe(viewLifecycleOwner, {
            try {
                if (it!!) {
                    Utils.showProgress(requireActivity())
                } else {
                    Utils.hideProgress(requireActivity())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        /*Profile Response*/
        profileViewModel.responseGetProfileNew.observe(viewLifecycleOwner, {
            try {
                when (it.status) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> populateProfileData(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun populateProfileData(loginRes: NewLoginResponse) {
        try {
            myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbNewLoginRes)
            myRoomDatabase.daoConfig()
                .insertConfigTable(TableConfig(Config.dbNewLoginRes, gson.toJson(loginRes)))
            if (loginRes.data != null) {
                if (loginRes.data.mobileVerified == 0) {
                    prefs.setLoginModel(loginRes.data)
                    startActivity(Intent(activity, VerifyOtpActivity::class.java))
                }
            }


            val profileUrl = if (loginRes.data!!.profilePic.trim()
                    .startsWith("https")
            ) loginRes.data.profilePic.trim() else
                "${
                    ApiService.BASE_URL.replace(
                        "/api/v1/",
                        ""
                    )
                }/uploads/profile_pics/${loginRes.data.profilePic.trim()}"
            ivProfileMenu = requireActivity().findViewById(R.id.ivProfileMenu)
            ivBackgroundMenu = requireActivity().findViewById(R.id.ivBackground)

            /*  Utils.showProgress(requireActivity())*/
            if (profileUrl.isEmpty()) {
                ivProfileMenu.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_profile
                    )
                )
            } else {
                Glide.with(this).clear(ivProfileMenu)
                Glide.with(this).clear(ivBackgroundMenu)

                Glide.with(this).load(profileUrl).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                    .into(ivProfileMenu)

                Glide.with(requireActivity()).load(profileUrl)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            ivBackgroundMenu.setImageDrawable(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            ivBackgroundMenu.setImageDrawable(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.ic_gradient_rectangle
                                )
                            )
                        }
                    })

            }

            userName = loginRes.data!!.name.trim()
            zip = loginRes.data!!.zip.toString()
            dob = loginRes.data!!.dob.toString()

            tvName.text = loginRes.data!!.name.trim()
            tvPhoneEmail.text = "${loginRes.data.mobile.trim()} | ${loginRes.data.email.trim()}"
            tvPoints.text = loginRes.data.points.toString().trim()

            // setting the notification flag
            Config.isnotification = loginRes.data.is_notification
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            flTotalPoints -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isMyPointClickedFromHome = false
                    startActivity(Intent(activity, TotalPointsActivity::class.java))
                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flCoupons -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbOfferListServiceId)
                    Config.isCouponOpeningFromBucket = false
                    Config.isEventBottomBarClicked = true
                    Config.isMyCouponClickedFromHome = true
                    Config.isMyCouponClickedFromProfileMenu = true
                    Config.isCouponComingFromMenuFragment = true
                    startActivity(Intent(activity, CouponActivity::class.java))
                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            ivMenuEditProfile, ivProfileMenu, ivEditProfile -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(
                        Intent(
                            activity,
                            EditProfileActivity::class.java
                        ).putExtra("userName", userName).putExtra("zip", zip).putExtra("dob", dob)
                    )

                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flReferAndEarn -> {
                startActivity(Intent(activity, InviteAndEarnActivity::class.java))
                removeFragment()
            }

            flSettings -> {
                startActivity(Intent(activity, OtherSettingsActivity::class.java))
                removeFragment()
                /*if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(activity, OtherSettingsActivity::class.java))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }*/
            }

            flSupport -> {
                startActivity(Intent(requireActivity(), SupportHelpActivity::class.java))
                removeFragment()
            }

            flMyEvents -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(activity, MyEventsActivity::class.java))
                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flMyOrders -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    myRoomDatabase.daoConfig()
                        .deleteConfigTableByField(Config.dbMyOrderListRecyclerViewPosition)
                    startActivity(
                        Intent(activity, MyOrdersActivity::class.java)
                            .putExtra("type", Constants.Order)
                    )
                    //removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flMyBookings -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    Config.isMyBookingClickedFromHome = false
                    startActivity(
                        Intent(activity, MyTableBookingsActivity::class.java)
                    )
                   // removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flSwitchToBusinessProfile -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(activity, BusinessProfileActivity::class.java))
                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flRate -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    val uri: Uri = Uri.parse("market://details?id=" + activity?.packageName)
                    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                    try {
                        startActivity(myAppLinkToMarket)
                    } catch (e: ActivityNotFoundException) {
                        showMsgDialogAndProceed(" unable to find market app")
                    }
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flCollectStamp -> {
                if (PubFun.isInternetConnection(requireActivity())) {
                    startActivity(Intent(activity, CollectStampsActivity::class.java))
                    removeFragment()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            flSignout -> {
                Utils.showDialog(
                    requireActivity(),
                    getString(R.string.sign_out),
                    getString(R.string.are_you_sure)
                ) {
                    prefs.clear()
                    startActivity(Intent(requireActivity(), SigninActivity::class.java))
                    activity?.finishAffinity()
                }
            }
        }
    }

    private fun removeFragment() {
        try {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Menu"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 500)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}