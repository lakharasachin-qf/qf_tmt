package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.themarkettheory.user.R
import kotlinx.android.synthetic.main.activity_payment_method.*
import kotlinx.android.synthetic.main.toolbar.*

class PaymentMethodActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        tvTitle.setText(getString(R.string.payment_method))

        ivBack.setOnClickListener(this)
        btnPaySecurely.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnPaySecurely -> {
                startActivity(Intent(this, TicketDetailsActivity::class.java))
            }
        }
    }
}