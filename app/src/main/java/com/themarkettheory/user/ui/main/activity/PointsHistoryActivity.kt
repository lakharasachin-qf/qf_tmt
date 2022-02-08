package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.pointhistory.NewPointHistoryData
import com.themarkettheory.user.newmodels.pointhistory.NewPointHistoryRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.PointsViewModel
import kotlinx.android.synthetic.main.activity_points_history.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*
import java.util.*
import kotlin.collections.ArrayList

class PointsHistoryActivity : BaseActivity(), View.OnClickListener {
    var textviews = arrayOf<TextView>()
    lateinit var pointsViewModel: PointsViewModel
    var serviceid: String = ""

    private val getPointsAll = "all"
    private val getPointsEarn = "earn"
    private val getPointsSpend = "spend"
    private var pointsType = getPointsAll
    private lateinit var arrayPointsList: ArrayList<NewPointHistoryData>
    private lateinit var pointsRes: NewPointHistoryRes

    // adapter declaration
    private lateinit var newPointHistoryAdapter: NewPointHistoryListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_history)

        pointsViewModel = ViewModelProvider(this).get(PointsViewModel::class.java)
        textviews = arrayOf(tvAll, tvEarn, tvSpend)
        ivFavorite.setImageResource(R.drawable.ic_search)
        ivShare.visibility = View.GONE
        ivFavorite.visibility = View.GONE
        tvTitle.setText(getString(R.string.points_history))
        if (intent != null) {
            serviceid = intent.getStringExtra("serviceid").toString()
        }
        tvAll.background =
            ContextCompat.getDrawable(this, R.drawable.ic_left_corner_radius_gradient)
        tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))

        getPointsHistoryResponse()

        ivBack.setOnClickListener(this)
        tvAll.setOnClickListener(this)
        llEarn.setOnClickListener(this)
        llSpend.setOnClickListener(this)

        tvAll.performClick()
    }

    private fun getPointsHistoryResponse() {
        pointsViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        pointsViewModel.responsePointsHistory.observe(this, Observer {
            when (it.status) {
                0 -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                1 -> {
                    populate(it)
                }
            }
        })
    }

    private fun populate(res: NewPointHistoryRes) {
        try {
            pointsRes = res
            newPointHistoryAdapter = NewPointHistoryListAdapter()
            rvPointsHistory.apply {
                layoutManager = LinearLayoutManager(this@PointsHistoryActivity)
                adapter = newPointHistoryAdapter
            }
            if (res.data!!.isNotEmpty()) {
                rvPointsHistory.visibility = View.VISIBLE
                arrayPointsList = ArrayList<NewPointHistoryData>()
                when (pointsType) {
                    getPointsAll -> arrayPointsList.addAll(res.data!! as ArrayList<NewPointHistoryData>)
                    getPointsEarn -> {
                        for (i in res.data!!.indices) {
                            if (res.data!![i].getPoint!!.trim()
                                    .lowercase(Locale.getDefault()) == getPointsEarn
                            ) {
                                arrayPointsList.add(res.data!![i])
                            }
                        }
                    }
                    getPointsSpend -> {
                        for (i in res.data!!.indices) {
                            if (res.data!![i].getPoint!!.trim()
                                    .lowercase(Locale.getDefault()) == getPointsSpend
                            ) {
                                arrayPointsList.add(res.data!![i])
                            }
                        }
                    }
                }
                newPointHistoryAdapter.addTotalPointList(arrayPointsList)
            } else {
                rvPointsHistory.visibility = View.GONE
                Utils.hideProgress(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            startActivity(
                Intent(this@PointsHistoryActivity, MyPointsActivity::class.java)
                    .putExtra("serviceId", serviceid)
            )
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            tvAll -> {
                resetUI()
                tvAll.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_left_corner_radius_gradient)
                tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
                if (PubFun.isInternetConnection(this)) {
                    pointsType = getPointsAll
                    pointsViewModel.points_history("all", serviceid)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            llSpend -> {
                resetUI()
                ivSpend.setColorFilter(Color.argb(255, 255, 255, 255))
                tvSpend.setTextColor(ContextCompat.getColor(this, R.color.white))
                llSpend.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_right_corner_radius_gradient)
                pointsType = getPointsSpend
                populate(pointsRes)
            }

            llEarn -> {
                resetUI()
                llEarn.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gradient_rectangle)
                ivEarn.setColorFilter(Color.argb(255, 255, 255, 255))
                tvEarn.setTextColor(ContextCompat.getColor(this, R.color.white))
                pointsType = getPointsEarn
                populate(pointsRes)
            }
        }
    }

    fun resetUI() {
        ll.background = ContextCompat.getDrawable(this, R.drawable.ic_tab_unselected_bg)
        textviews.map {
            it.setTextColor(ContextCompat.getColor(this, R.color.bali_hai))
        }
        ivSpend.setColorFilter(
            ContextCompat.getColor(this, R.color.bali_hai),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        ivEarn.setColorFilter(
            ContextCompat.getColor(this, R.color.bali_hai),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
        tvAll.background = ContextCompat.getDrawable(this, R.drawable.ic_left_corner_disable)
        llSpend.background = ContextCompat.getDrawable(this, R.drawable.ic_right_corner_disable)
        llEarn.setBackgroundColor(ContextCompat.getColor(this, R.color.mirage2))
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@PointsHistoryActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Point History"
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}