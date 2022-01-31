package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_add_report.*
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.toolbar.*

class AddReportActivity : BaseActivity(), View.OnClickListener {

    var serviceId: String? = ""
    lateinit var edDetails: TextInputEditText
    val listOfReportService = ArrayList<String>()
    lateinit var newHomeRes: NewHomeRes
    var reportAddedSuccefully = 0

    lateinit var vendorDetailViewModel: VendorDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report)
        edDetails = findViewById(R.id.edDetails)
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")
        }

        tvTitle.setText(getString(R.string.report))

        newHomeRes = gson.fromJson(
            myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
            NewHomeRes::class.java
        )

        listOfReportService.add(0, "Select Issue") // adding default 1st item
        listOfReportService.addAll(newHomeRes.data!!.report_service_questions as ArrayList<out String>)
        val dataAdapter =
            ArrayAdapter(
                this@AddReportActivity,
                R.layout.row_spinner,
                listOfReportService
            )
        spSelectIssue.adapter = dataAdapter

        getResponse()

        ivBack.setOnClickListener(this)
        btnDone.setOnClickListener(this)
    }


    private fun getResponse() {
        vendorDetailViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        vendorDetailViewModel.responseAddReport.observe(this, Observer {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> {
                    try {
                        showMsgDialogAndProceed("Reported successfully")
                        reportAddedSuccefully = 1
                       /* Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 2000)*/
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> {
                PubFun.hideKeyboard(this@AddReportActivity)
                onBackPressed()
            }

            btnDone -> {
                var spSelectedItem = ""
                if (spSelectIssue.selectedItemPosition == 0) {
                    spSelectedItem = ""
                    showMsgDialogAndProceed("Please select issue for your service report")
                    return
                } else {
                    spSelectedItem = listOfReportService[spSelectIssue.selectedItemPosition]
                }
                if (!edDetails.text.toString().isNullOrEmpty()) {
                    PubFun.hideKeyboard(this@AddReportActivity)


                    vendorDetailViewModel.report_service(
                        serviceId,
                        edDetails.getText().toString().trim(),
                        "category",
                        spSelectedItem
                    )
                } else {
                    showMsgDialogAndProceed("Please enter report detail")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")

    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(this@AddReportActivity)
            val myDialog = DialogToast(this@AddReportActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_bucket)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                            if(reportAddedSuccefully==1){
                                this@AddReportActivity.finish()
                            }
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}