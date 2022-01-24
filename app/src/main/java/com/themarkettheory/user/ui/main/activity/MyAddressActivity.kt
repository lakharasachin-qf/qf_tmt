package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.AddressListAdapter
import com.themarkettheory.user.viewmodel.AddressViewModel
import kotlinx.android.synthetic.main.activity_my_address.*
import kotlinx.android.synthetic.main.toolbar.*

class MyAddressActivity : BaseActivity(), View.OnClickListener {
    lateinit var addressViewModel: AddressViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_address)

        tvTitle.setText(getString(R.string.myAddress))
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        getResponse()
        ivBack.setOnClickListener(this)
        tvAddAddress.setOnClickListener(this)
    }

    private fun getResponse() {
        addressViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        addressViewModel.responseGetAddress.observe(this, Observer {
            if (it.status!!) {
                val addressListAdapter = AddressListAdapter(this, it.data?.list!!) { data, str ->
                    if (str == "edit") {
                        startActivity(
                            Intent(this, AddAddressActivity::class.java)
                                .putExtra("model", data)
                        )
                    } else if (str == "select") {
                        addressViewModel.edit_address(
                            data.googleAddress,
                            data.houseNumber,
                            data.floor,
                            data.tower,
                            data.type.toString(),
                            "1",
                            data.addressOptional,
                            data.latitude,
                            data.longitude,
                            data.id.toString()
                        )
                    } else if (str == "delete") {
                        Utils.showDialog(
                            this,
                            "Alert",
                            "Are you sure you want to delete this address?"
                        ) {
                            addressViewModel.delete_address(data.id.toString())
                        }
                    }
                }
                rvAddress.adapter = addressListAdapter
                rvAddress.layoutManager = LinearLayoutManager(this)
            }
        })

        addressViewModel.responseEditAddress.observe(this, Observer {
            if (it.status!!) {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        })

        addressViewModel.responseDeleteAddress.observe(this, Observer {
            if (it.status!!) {
                addressViewModel.addressList()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        addressViewModel.addressList()
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            tvAddAddress -> startActivity(Intent(this, AddAddressActivity::class.java))
        }
    }
}