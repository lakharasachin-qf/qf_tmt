package com.themarkettheory.user.ui.restaurant

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.activity.BaseActivity
import com.themarkettheory.user.ui.main.activity.WebviewActivity
import com.themarkettheory.user.ui.main.adapter.AmenitiesListAdapter
import com.themarkettheory.user.ui.main.adapter.OpenHoursAdapter
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_restaurant_description.*
import kotlinx.android.synthetic.main.toolbar.*

class RestaurantDescriptionActivity : BaseActivity(), View.OnClickListener {
    private var privacy_document: String? = ""
    private var facebookUrl: String? = ""
    private var googleUrl: String? = ""
    private var twitterUrl: String? = ""
    private var instagramUrl: String? = ""
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    var serviceId: String? = ""
    var phone: String? = ""
    var email: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_description)

        tvTitle.setText("")
        vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")
        }

        getServiceDetail()

        ivBack.setOnClickListener(this)
        //llPhone.setOnClickListener(this)
        //llEmail.setOnClickListener(this)
        rlFacebook.setOnClickListener(this)
        rlInstagram.setOnClickListener(this)
        rlTwitter.setOnClickListener(this)
        rlGoogle.setOnClickListener(this)
        tvPrivacyPolicy.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        vendorDetailViewModel.service_details(serviceId)
    }

    private fun getServiceDetail() {
        vendorDetailViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        vendorDetailViewModel.responseServiceDetail.observe(this, Observer {
            when (it.status!!) {
                0 -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                1 -> {
                    nestedScroll.visibility = View.VISIBLE
                    tvDescription.setText(it.data?.description)
                    // phone = it.data?.phone
                    email = it.data?.email
                    facebookUrl = it.data?.facebookUrl
                    twitterUrl = it.data?.twitterUrl
                    googleUrl = it.data?.googleUrl
                    instagramUrl = it.data?.instagramUrl
                    //   tvPhone.setText(it.data?.mobile)
                    // tvEmail.setText(it.data?.email)
                    //   tvTitle.setText(it.data?.title)
                    tvTitle.setText("Restaurant Description")
                    privacy_document = it.data?.privacyPolicyUrl

//                    if (it.data?.mobile.isNullOrEmpty()) {
//                        llPhone.visibility = View.GONE
//                    }
//                    if (it.data?.email.isNullOrEmpty()) {
//                        llEmail.visibility = View.GONE
//                    }

                    if (privacy_document.isNullOrEmpty()) {
                        tvPrivacyPolicy.visibility = View.GONE
                    }

                    if (it.data?.amenities.isNullOrEmpty()) {
                        tvFacilitiesAndFeatures.visibility = View.GONE
                        view.visibility = View.GONE

                    } else {
                        val amenitiesListAdapter = AmenitiesListAdapter(this, it.data?.amenities!!)
                        rvAmenities.adapter = amenitiesListAdapter
                        rvAmenities.layoutManager = GridLayoutManager(this, 2)
                    }

                    if (it.data?.tags.isNullOrEmpty()) {
                        tvTags.visibility = View.GONE
                        viewTags.visibility = View.GONE
                    } else {
                        val listOfTags = mutableListOf<String>()
                        for (i in 0 until it.data?.tags!!.size) {
                            listOfTags.add(it.data!!.tags!![i].name!!)
                        }
                        lylTags.tags = listOfTags

                        /*val tagsAdapter = TagListAdapter(this, it.data?.tags!!)
                        rvTags.adapter = tagsAdapter
                        rvTags.layoutManager = StaggeredGridLayoutManager(
                            2,
                            StaggeredGridLayoutManager.HORIZONTAL
                        )*/
                    }
                    Log.e("GSON", gson.toJson(it.data!!.openingTime))
                        if (it.data?.openAt.isNullOrEmpty()) {
                            res_desc_isOpen.setText("Open")
                            res_desc_isOpen.setTextColor(Color.parseColor("#1CBD54"))
                        } else {
                            res_desc_isOpen.setText("Open At " + it.data?.openAt)
                            res_desc_isOpen.setTextColor(Color.parseColor("#1CBD54"))
                        }
                    if (it.data!!.openingTime.isNullOrEmpty()) {
                        res_desc_isOpen.visibility = View.INVISIBLE
                    }


                    if (!it.data?.openingTime.isNullOrEmpty()) {
                        val openHoursAdapter = OpenHoursAdapter(this, it.data?.openingTime!!)
                        rvOpeningHours.adapter = openHoursAdapter
                        rvOpeningHours.layoutManager = LinearLayoutManager(this)
                    }

                    if (it.data?.facebookUrl.isNullOrEmpty()) {
                        rlFacebook.visibility = View.GONE
                    }
                    if (it.data?.instagramUrl.isNullOrEmpty()) {
                        rlInstagram.visibility = View.GONE
                    }
                    if (it.data?.googleUrl.isNullOrEmpty()) {
                        rlGoogle.visibility = View.GONE
                    }
                    if (it.data?.twitterUrl.isNullOrEmpty()) {
                        rlTwitter.visibility = View.GONE
                    }
                    if(it.data?.facebookUrl.isNullOrBlank() &&
                        it.data?.instagramUrl.isNullOrBlank() &&
                        it.data?.googleUrl.isNullOrBlank() &&
                        it.data?.twitterUrl.isNullOrBlank()){
                        restaurant_desc_social.visibility = View.GONE
                        restaurant_desc_view3.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

//            llPhone -> {
//                if (!phone.isNullOrEmpty()) {
//                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
//                    startActivity(intent);
//                }
//            }

            tvPrivacyPolicy -> {
                try {
                    privacy_document =
                        "https://docs.google.com/viewer?url=$privacy_document"
                    /*val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(privacy_document)
                    startActivity(i)*/
                    startActivity(
                        Intent(this, WebviewActivity::class.java)
                            .putExtra("link", privacy_document)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            rlFacebook -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(facebookUrl)
                startActivity(i)
            }

            rlInstagram -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(instagramUrl)
                startActivity(i)
            }

            rlTwitter -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(twitterUrl)
                startActivity(i)
            }

            rlGoogle -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(googleUrl)
                startActivity(i)
            }

            /*   llEmail -> {
                   try {
                       val i = Intent(Intent.ACTION_SEND)
                       i.type = "text/plain"
                       i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                       i.putExtra(Intent.EXTRA_SUBJECT, "The market Theory | Support")
                       i.putExtra(Intent.EXTRA_TEXT, "")
                       val pm = getPackageManager()
                       val matches = pm.queryIntentActivities(i, 0)
                       var best: ResolveInfo? = null
                       for (info in matches)
                           if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase()
                                   .contains(
                                       "gmail"
                                   )
                           )
                               best = info
                       if (best != null)
                           i.setClassName(best.activityInfo.packageName, best.activityInfo.name)
                       try {
                           startActivity(Intent.createChooser(i, "Send mail..."))
                       } catch (ex: android.content.ActivityNotFoundException) {
                           Toast.makeText(
                               this,
                               "There are no email clients installed.",
                               Toast.LENGTH_SHORT
                           )
                               .show()
                       }

                   } catch (ex: ActivityNotFoundException) {
                       Toast.makeText(
                           this,
                           "There are no email clients installed.",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }*/
        }
    }
}