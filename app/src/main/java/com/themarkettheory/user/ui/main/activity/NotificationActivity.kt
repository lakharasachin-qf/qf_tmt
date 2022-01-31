package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.ui.main.fragment.NotificationAlertFragment
import com.themarkettheory.user.ui.main.fragment.NotificationCouponCodesFragment
import com.themarkettheory.user.viewmodel.OfferViewModel
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_notification.ll
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class NotificationActivity : BaseActivity(), View.OnClickListener {
    var textviews = arrayOf<TextView>()
    lateinit var offerViewModel: OfferViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        offerViewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
        textviews = arrayOf(tvalerts, tvCouponCodes)
        tvTitle.setText(getString(R.string.notifications))

        setFragment(NotificationAlertFragment())
        offerViewModel.get_activated_coupons()

        getResponse()

        ivBack.setOnClickListener(this)
        llAlerts.setOnClickListener(this)
        llCouponCode.setOnClickListener(this)
    }



    private fun getResponse() {
        offerViewModel.responseActivatedCoupons.observe(this, Observer {
            if (it.status!!) {
                if (it.data.isNullOrEmpty()) {
                    tvCouponCodeCount.visibility = View.GONE
                } else {
                    tvCouponCodeCount.visibility = View.VISIBLE
                    tvCouponCodeCount.setText(it.data!!.size.toString())
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            llAlerts -> {
                resetUI()
                llAlerts.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_left_corner_radius_gradient)
                llCouponCode.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_right_corner_disable)
                tvalerts.setTextColor(ContextCompat.getColor(this, R.color.white))
                setFragment(NotificationAlertFragment())
            }

            llCouponCode -> {
                resetUI()
                llCouponCode.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_right_corner_radius_gradient)
                llAlerts.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_left_corner_disable)
                tvCouponCodes.setTextColor(ContextCompat.getColor(this, R.color.white))
                setFragment(NotificationCouponCodesFragment())
            }
        }
    }

    fun resetUI() {
        ll.background = ContextCompat.getDrawable(this, R.drawable.ic_tab_unselected_bg)
        textviews.map {
            it.setTextColor(ContextCompat.getColor(this, R.color.bali_hai))
        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutNotification, fragment)
            .commit()
    }
}