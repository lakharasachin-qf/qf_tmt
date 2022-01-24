package com.themarkettheory.user.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.overview.Image
import com.themarkettheory.user.newmodels.overview.allServiceImages.AllServiceImages
import com.themarkettheory.user.ui.dialog.dialogOverviewPhotos.DialogOverviewPhotos
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.VendorPhotoListAdapter
import com.themarkettheory.user.ui.restaurant.restaurant_detail.OverviewPhotosAdapter
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_see_all_photos.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.ivBack
import kotlinx.android.synthetic.main.toolbar.tvTitle

class SeeAllPhotosActivity : BaseActivity(), View.OnClickListener {
    private var imageData: ArrayList<Image>? = ArrayList()
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    lateinit var vendorPhotoListAdapter: VendorPhotoListAdapter
    private var title: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_all_photos)

        try {
            title = intent.getStringExtra("title").toString()
            tvTitle.setText(title)
            vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
            vendorDetailViewModel.isLoading.observe(this, {
                if (it!!) {
                    Utils.showProgress(this)
                } else {
                    Utils.hideProgress(this)
                }
            })
            vendorDetailViewModel.responseAllServiceImages.observe(this, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it, "", false)
                    1 -> {
                        if (it.data != null) {
                            imageData = it.data!!
                            vendorPhotoListAdapter =
                                VendorPhotoListAdapter(
                                    this@SeeAllPhotosActivity,
                                    false,
                                    imageData!!,
                                    true
                                )

                            val listener = object : ListClickListener {
                                override fun onClickListener(view: View, pos: Int, objects: Any) {
                                    val dialogOverview =
                                        DialogOverviewPhotos(this@SeeAllPhotosActivity)
                                    dialogOverview.show()
                                    dialogOverview.holder?.viewPager?.adapter =
                                        vendorPhotoListAdapter
                                    dialogOverview.holder?.indicator?.setViewPager(dialogOverview.holder?.viewPager)
                                    if (pos != -1) dialogOverview.holder?.viewPager?.currentItem =
                                        pos
                                    dialogOverview.holder?.btnClose?.setOnClickListener {
                                        dialogOverview.dismiss()
                                    }
                                }
                            }

                            val overviewPhotosListAdapter = OverviewPhotosAdapter(listener)
                            rvSeeAllPhotos.apply {
                                adapter = overviewPhotosListAdapter
                                layoutManager = GridLayoutManager(this@SeeAllPhotosActivity, 3)
                            }
                            overviewPhotosListAdapter.addImages(imageData!!)
                        }
                    }
                }
            })

            if (PubFun.isInternetConnection(this@SeeAllPhotosActivity)) {
                vendorDetailViewModel.all_service_images(Config.serviceDetail!!.id.toString())
            } else {
                showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
            }
            ivBack.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMsgDialogAndProceed(res: AllServiceImages?, msg: String, isMsgShow: Boolean) {
        try {
            val myDialog = DialogToast(this@SeeAllPhotosActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "See All Photos & Videos"
                it.tvMessage.text = if (isMsgShow) msg else res!!.message
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
                                postDelayed(this, 1000)
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
            ivBack -> {
                if (PubFun.isInternetConnection(this@SeeAllPhotosActivity)) {
                    finish()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }
        }
    }
}