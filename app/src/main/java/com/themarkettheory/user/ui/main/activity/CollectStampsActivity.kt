package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.CollectStampListAdapter
import com.themarkettheory.user.viewmodel.StampViewModel
import kotlinx.android.synthetic.main.activity_collect_stamps.*
import kotlinx.android.synthetic.main.toolbar_restaurant.*

class CollectStampsActivity : BaseActivity(), View.OnClickListener {
    lateinit var stampViewModel: StampViewModel
    var serviceId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_stamps)

        stampViewModel = ViewModelProvider(this).get(StampViewModel::class.java)
        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")
        }
        ivFavorite.setImageResource(R.drawable.ic_search)
        ivShare.visibility = View.GONE
        ivFavorite.visibility = View.GONE
        tvTitle.setText(getString(R.string.collect_stamps))

        stampViewModel.stamps(serviceId)

        getStampResponse()

        ivBack.setOnClickListener(this)
    }

    private fun getStampResponse() {
        stampViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        stampViewModel.responseStamps.observe(this, Observer {
            if (it.status!!) {
                val collectStampListAdapter = CollectStampListAdapter(this, it.data?.list!!)
                rvStamps.adapter = collectStampListAdapter
                rvStamps.layoutManager = LinearLayoutManager(this)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}