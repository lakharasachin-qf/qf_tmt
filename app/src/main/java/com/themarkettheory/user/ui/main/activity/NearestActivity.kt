package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.ui.main.adapter.NearestListAdapter
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_nearest.*
import kotlinx.android.synthetic.main.toolbar.*

class NearestActivity : BaseActivity(), View.OnClickListener {
    lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearest)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        tvTitle.setText(getString(R.string.nearest))

        getNearestResponse()

        ivBack.setOnClickListener(this)
    }

    private fun getNearestResponse() {
        homeViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        homeViewModel.view_all_nearest()

        homeViewModel.responseAllNearest.observe(this, Observer {
            if (it.status!!) {
                val listener = object : ListClickListener {
                    override fun onClickListener(view: View, pos: Int, objects: Any) {
                    }
                }
                val nearestListAdapter = NearestListAdapter(this, listener, it.data?.list!!)
                rvnearest.adapter = nearestListAdapter
                rvnearest.layoutManager = GridLayoutManager(this, 2)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }
}