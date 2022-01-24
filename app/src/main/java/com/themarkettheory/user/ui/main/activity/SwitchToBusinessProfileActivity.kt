package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.ui.main.adapter.PlanListAdapter
import kotlinx.android.synthetic.main.activity_switch_to_business_profile.*
import kotlinx.android.synthetic.main.toolbar.*

class SwitchToBusinessProfileActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_to_business_profile)

        tvTitle.setText(getString(R.string.switch_to_business_profile))

        val planAdapter = PlanListAdapter(this)
        rvPlan.adapter = planAdapter
        rvPlan.layoutManager = GridLayoutManager(this, 3)
//        rvPlan.addItemDecoration()

        ivBack.setOnClickListener(this)
        btnContactUs.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnContactUs -> startActivity(Intent(this, BusinessProfileActivity::class.java))
        }
    }
}