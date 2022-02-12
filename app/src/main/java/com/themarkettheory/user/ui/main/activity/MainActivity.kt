package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.appevents.internal.AppEventUtility.bytesToHex
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.ui.coupon.CouponActivity
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.fragment.*
import com.themarkettheory.user.ui.restaurant.restaurant_list.RestaurantListActivity
import com.themarkettheory.user.viewmodel.GeneralViewModel
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest


class MainActivity : BaseActivity(), View.OnClickListener {
    var imageViewArray = arrayOf<ImageView>()
    lateinit var homeViewModel: HomeViewModel
    lateinit var generalViewModel: GeneralViewModel
    lateinit var menuFragment: MenuFragment
    var isMenu = false
    lateinit var LocalFragmentCalled: Fragment
    lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = null
        setContentView(com.themarkettheory.user.R.layout.activity_main)
        Config.mainActivity = this@MainActivity

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
        imageViewArray = arrayOf(ivHomeLine, ivFavoriteLine, ivEventLine, ivMenuLine)

        myRoomDatabase.daoConfig().apply {
            deleteConfigTableByField(Config.dbIsDashboard)
            insertConfigTable(
                TableConfig(
                    Config.dbIsDashboard,
                    "true"
                )
            )
        }

        setFragment(HomeFragment())
        printHashKey(this)
        if (PubFun.isInternetConnection(this)) {
            generalViewModel.profile()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
        getResponse()

        rlHome.setOnClickListener(this)
        rlFavorite.setOnClickListener(this)
        rlEvent.setOnClickListener(this)
        rlMenu.setOnClickListener(this)
        ivCenter.setOnClickListener(this)


    }

    private fun getResponse() {
        generalViewModel.responseGetProfile.observe(this, Observer {
            if (it.status!! == 1) {
                it.data?.let {
                    val loginModel = it
                    loginModel.token = prefs.getAccessToken(this@MainActivity)
                    /*prefs.setLoginModel(loginModel)*/
                }
            }
        })
    }

    fun printHashKey(pContext: Context) {
        val signatureList: List<String>
        /*try {

            val info: PackageInfo = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("", "printHashKey()", e)
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // New signature

            val sig = this.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo
            signatureList = if (sig.hasMultipleSigners()) {
                // Send all with apkContentsSigners
                sig.apkContentsSigners.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest())
                }
            } else {
                // Send one with signingCertificateHistory
                sig.signingCertificateHistory.map {
                    val digest = MessageDigest.getInstance("SHA")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest())
                }
            }
        } else {
            val sig = this.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures
            signatureList = sig.map {
                val digest = MessageDigest.getInstance("SHA")
                digest.update(it.toByteArray())
                bytesToHex(digest.digest())
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(com.themarkettheory.user.R.id.frameLayout, fragment)
            .commit()
    }

    override fun onClick(v: View?) {
        Config.fragmentName = ""
        when (v) {
            rlHome -> {
                if (PubFun.isInternetConnection(this@MainActivity)) {
                    LocalFragmentCalled = HomeFragment()
                    Config.FragmentCalled = HomeFragment()
                    resetBottomNav()
                    ivHome.setImageResource(com.themarkettheory.user.R.drawable.ic_home_selected)
                    ivHomeLine.visibility = View.VISIBLE
                    setFragment(HomeFragment())
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlFavorite -> {
                if (PubFun.isInternetConnection(this@MainActivity)) {
                    resetBottomNav()
                    ivFavorite1.setImageResource(com.themarkettheory.user.R.drawable.ic_favorite_selected)
                    ivFavoriteLine.visibility = View.VISIBLE
                    Config.isRestaurantListOpeningFrom =
                        Config.restaurantListOpeningFromFavoriteBottomSection
                    startActivity(
                        Intent(
                            this@MainActivity,
                            RestaurantListActivity::class.java
                        )
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlEvent -> {
                if (PubFun.isInternetConnection(this@MainActivity)) {
                    resetBottomNav()
                    ivEvent.setImageResource(com.themarkettheory.user.R.drawable.ic_event_selected)
                    ivEventLine.visibility = View.VISIBLE
                    Config.isEventBottomBarClicked = true
                    Config.isMyCouponClickedFromHome = false
                    Config.isCouponOpeningFromBucket = false
                    Config.isCouponComingFromMainActivity = true
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbOfferListServiceId)
                    startActivity(
                        Intent(
                            this@MainActivity,
                            CouponActivity::class.java
                        ).putExtra("backToHome", "HomeFragment")
                    )
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlMenu -> {
                if (PubFun.isInternetConnection(this@MainActivity)) {
                    LocalFragmentCalled = MenuFragment()
                    Config.FragmentCalled = MenuFragment()
                    resetBottomNav()
                    ivMenu.setImageResource(com.themarkettheory.user.R.drawable.ic_profile_menu_selected)
                    ivMenuLine.visibility = View.VISIBLE
                    menuFragment = MenuFragment()
                    setFragment(menuFragment)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            ivCenter -> {
                if (PubFun.isInternetConnection(this@MainActivity)) {
                    isMenu = false
                    resetBottomNav()
                    Config.fragmentName = Config.mapFragment
//                setFragment(HomeFragment.newInstance(HomeData(),"map"))
                    setFragment(MapFragment.newInstance("0", ""))
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
    }

    fun resetBottomNav() {
        imageViewArray.map {
            it.visibility = View.GONE
        }
        ivHome.setImageResource(com.themarkettheory.user.R.drawable.ic_home_unselected)
        ivFavorite1.setImageResource(com.themarkettheory.user.R.drawable.ic_favorite_unselected)
        ivEvent.setImageResource(com.themarkettheory.user.R.drawable.ic_event_unselected)
        ivMenu.setImageResource(com.themarkettheory.user.R.drawable.ic_profile_menu_unselected)
    }


/*    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        Config.FragmentCalled = LocalFragmentCalled
    }

    override fun onRestoreInstanceState(state: Bundle) {
        super.onRestoreInstanceState(state)
        setFragment(Config.FragmentCalled!!)
    }*/

    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }

    override fun onResume() {
        super.onResume()
        when (Config.bottomBarClickedName) {
            Config.homeBottomBarClicked -> rlHome.performClick()
            Config.favoriteBottomBarClick -> rlFavorite.performClick()
            Config.locationBottomBarClick -> ivCenter.performClick()
            Config.eventBottomBarClick -> rlEvent.performClick()
            Config.menuBottomBarClick -> rlMenu.performClick()
        }
        Config.bottomBarClickedName = ""
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@MainActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Main"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 500)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
