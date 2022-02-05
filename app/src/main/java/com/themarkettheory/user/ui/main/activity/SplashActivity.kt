package com.themarkettheory.user.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast

class SplashActivity : BaseActivity() {
    //For grating permission on Lollipop and above flavors
    private var sentToSettings = false

    //check if all permissions are granted
    var allGranted = false
    private val PERMISSION_CALLBACK_CONSTANT = 100
    private var onActivityRequestCode = 0
    private val REQUEST_PERMISSION_SETTING = 101
    var permissionsRequired = arrayOf<String>()
    lateinit var appUpdateManager: AppUpdateManager
    val APP_UPDATES = 1001

    val onActivityResultSplash = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (onActivityRequestCode) {
            REQUEST_PERMISSION_SETTING -> {
                if (ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permissionsRequired[0]
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permissionsRequired[1]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //Got Permission
                    allGranted = true
                    proceedAfterPermission()
                } else {
                    allGranted = false
                    grantPermission()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkForUpdates()

        /**
         * https://stackoverflow.com/questions/19545889/app-restarts-rather-than-resumes
         * Added by Milan Sheth on 17-Jul-2019 at Office
         * Below code is to resume app from where user was left out instead of restarting
         * app from its launcher activity.
         */
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }

        Config.spPermission = getSharedPreferences(Config.sharedPreferencePermission, MODE_PRIVATE)

        permissionsRequired = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // only for lollipop and newer versions
                grantPermission()
            } else {
                proceedAfterPermission()
            }
        }, 1000)
    }

    //app updates
    private fun checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                startAppUpdates(appUpdateInfo)
            }
        }
    }

    private fun startAppUpdates(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                this@SplashActivity,
                APP_UPDATES
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATES) {
            if (resultCode == RESULT_CANCELED) {
                checkForUpdates()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allGranted = true
                    } else {
                        allGranted = false
                        break
                    }
                }
                if (allGranted) {
                    proceedAfterPermission()
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@SplashActivity,
                        permissionsRequired[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this@SplashActivity,
                        permissionsRequired[1]
                    )
                ) {
                    openPermissionDialog()
                    /*val builder = AlertDialog.Builder(this@SplashActivity)
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        *//*   ActivityCompat.requestPermissions(
                               this@SplashActivity,
                               permissionsRequired,
                               PERMISSION_CALLBACK_CONSTANT
                           )*//*
                    }
                    builder.setNegativeButton("SKIP") { dialogInterface, i ->
                        dialogInterface.cancel()
                        init()
                    }
                    builder.show()*/
                } else {
                    /*Toast.makeText(ActivitySplash.this, "Unable to get permission", Toast.LENGTH_LONG).show();*/
                    grantPermission()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this@SplashActivity,
                            APP_UPDATES
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }

    }

    @SuppressLint("SetTextI18n")
    private fun openPermissionDialog() {
        try {
            val dialogPermission = DialogToast(this@SplashActivity)
            dialogPermission.show()
            dialogPermission.holder?.let { dialog ->
                dialog.tvTitle.text = "Location permission denied"
                dialog.tvMessage.text =
                    "We still need location permission to be allowed in order " +
                            "to run few app functionality smoothly otherwise app might " +
                            "not work properly"
                dialog.btnDialogLogout.apply {
                    text = "GRANT"
                    setOnClickListener {
                        dialogPermission.dismiss()
                        ActivityCompat.requestPermissions(
                            this@SplashActivity,
                            permissionsRequired,
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                }
                dialog.btnDialogCancel.apply {
                    text = "SKIP"
                    setOnClickListener {
                        dialogPermission.dismiss()
                        init()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        try {
            if (sentToSettings) {
                if (ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permissionsRequired[0]
                    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        this@SplashActivity,
                        permissionsRequired[1]
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //Got Permission
                    proceedAfterPermission()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun grantPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this@SplashActivity,
                    permissionsRequired[0]
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this@SplashActivity,
                    permissionsRequired[1]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permissionsRequired[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this@SplashActivity,
                        permissionsRequired[1]
                    )
                ) {
                    //Show Information about why you need the permission
                    openPermissionDialog()

                    /*val builder = AlertDialog.Builder(this@SplashActivity)
                    builder.setTitle("Location permission denied")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        ActivityCompat.requestPermissions(
                            this@SplashActivity,
                            permissionsRequired,
                            PERMISSION_CALLBACK_CONSTANT
                        )
                    }
                    builder.setNegativeButton("SKIP") { dialogInterface, i ->
                        dialogInterface.cancel()
                        init()
                    }
                    builder.show()*/
                } else if (Config.spPermission.getBoolean(
                        Config.sharedPreferencePermission,
                        false
                    )
                ) {
                    //Previously Permission Request was cancelled with Don't Ask Again,
                    // Redirect to Settings after showing Information about why you need the permission
                    val dialogPermission = DialogToast(this@SplashActivity)
                    dialogPermission.show()
                    dialogPermission.holder?.let { dialog ->
                        dialog.tvTitle.text = "Location permission denied"
                        dialog.tvMessage.text =
                            "We still need location permission to be allowed in order " +
                                    "to run few app functionality smoothly otherwise app might " +
                                    "not work properly"
                        dialog.btnDialogLogout.apply {
                            text = "GRANT"
                            setOnClickListener {
                                dialogPermission.dismiss()
                                allGranted = false
                                sentToSettings = true
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package",
                                    packageName, null
                                )
                                intent.data = uri
                                onActivityRequestCode = REQUEST_PERMISSION_SETTING
                                onActivityResultSplash.launch(intent)
                                Toast.makeText(
                                    this@SplashActivity,
                                    "Go to Permissions to Grant Permission",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        dialog.btnDialogCancel.apply {
                            text = "SKIP"
                            setOnClickListener {
                                dialogPermission.dismiss()
                                init()
                            }
                        }
                    }


                    /*val builder = AlertDialog.Builder(this@SplashActivity)
                    builder.setTitle("Location permission denined")
                    builder.setMessage(
                        "We still need location permission to be allowed in order " +
                                "to run few app functionality smoothly otherwise app might " +
                                "not work properly"
                    )
                    builder.setPositiveButton(
                        "GRANT"
                    ) { dialogInterface, i ->
                        dialogInterface.cancel()
                        allGranted = false
                        sentToSettings = true
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts(
                            "package",
                            packageName, null
                        )
                        intent.data = uri
                        onActivityRequestCode = REQUEST_PERMISSION_SETTING
                        onActivityResultSplash.launch(intent)
                        Toast.makeText(
                            this@SplashActivity,
                            "Go to Permissions to Grant Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    builder.setNegativeButton(null, null)
                    builder.setNegativeButton("SKIP") { dialogInterface, i ->
                        dialogInterface.cancel()
                        init()
                    }
                    builder.show()*/
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(
                        this@SplashActivity,
                        permissionsRequired,
                        PERMISSION_CALLBACK_CONSTANT
                    )
                }
                Config.spPermission.edit().putBoolean(Config.sharedPreferencePermission, true)
                    .apply()
            } else {
                //You already have the permission, just go ahead.
                allGranted = true
                proceedAfterPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun proceedAfterPermission() {
        try {
            if (allGranted) {
                Config.spPermission.edit().putBoolean(Config.sharedPreferencePermission, true)
                    .apply()
                init()
            } else {
                grantPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        Config.myRoomDatabase = MyRoomDatabase.getDB(this@SplashActivity)!!
        val isDashboard = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbIsDashboard)
       // Log.e("SPles",gson.toJson(prefs.getLoginModel()))

        val selectNavigates = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
      //  Log.e("SPles", selectNavigates.toString())

        if(selectNavigates != Config.editProfileActivity) {
            myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
            Handler(Looper.getMainLooper()).postDelayed({

                if (prefs.getLoginModel().emailVerified == 0) {
                    startActivity(Intent(this, SigninActivity::class.java))
                } else if (prefs.getLoginModel().emailVerified == 1 &&
                    prefs.getLoginModel().mobileVerified == 0
                ) {
                    //startActivity(Intent(this, VerifyOtpActivity::class.java))
                    startActivity(Intent(this, SigninActivity::class.java))
                } else if (prefs.getLoginModel().emailVerified == 1 &&
                    prefs.getLoginModel().mobileVerified == 1 &&
                    prefs.getLoginModel().zip.isEmpty()
                ) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else if (prefs.getLoginModel().emailVerified == 1 &&
                    prefs.getLoginModel().mobileVerified == 1 &&
                    prefs.getLoginModel().zip.isNotEmpty() && isDashboard == "false"
                ) {
                    startActivity(Intent(this, CitySelectionActivity::class.java))
                } else {
                    startActivity(Intent(this, CitySelectionActivity::class.java))
                }

                finish()
            }, 1000)
        }else{
            startActivity(Intent(this, CitySelectionActivity::class.java))
        }

    }
}
