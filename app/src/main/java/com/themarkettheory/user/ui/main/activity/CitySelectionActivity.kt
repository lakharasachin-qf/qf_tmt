package com.themarkettheory.user.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.FieldSelector
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.model.LocalityModel
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.LocalityListAdapter
import com.themarkettheory.user.viewmodel.AddressViewModel
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.activity_city_selection.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.row_my_cart.*
import java.util.*
import kotlin.collections.ArrayList

class CitySelectionActivity : BaseActivity(), View.OnClickListener {
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var latitude = 0.0
    var longitude = 0.0
    private var google_address: String = ""
    lateinit var addressViewModel: AddressViewModel
    private var fieldSelector: FieldSelector? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 23487
    lateinit var placesClient: PlacesClient
    private val typeCity = "1"
    private val typeLocality = "2"

    private lateinit var recentListener: ListClickListener
    private lateinit var popularListener: ListClickListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_selection)

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val places_api_key = getString(R.string.google_place_api_key)
        Places.initialize(this, places_api_key)

        placesClient = Places.createClient(this)

        fieldSelector = FieldSelector(CheckBox(this), TextView(this))

        tvTitleCitySelection.paint.shader = PubFun.getShader(this, tvTitleCitySelection)

        getResponse()
        getRecentPopularList()

        recentListener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                val list = objects as LocalityModel
                val location = list.value.split(",")
                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbLat)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLat,
                            if (location[0].trim().length >= 9) location[0]
                                .substring(0, 9) else location[0].trim()
                        )
                    )

                    deleteConfigTableByField(Config.dbLng)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLng,
                            if (location[1].trim().length >= 9) location[1]
                                .substring(0, 9) else location[1].trim()
                        )
                    )
                }
                updateCurrentCitySelection(list.name)
                goToMainActivity()
            }
        }

        popularListener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                val list = objects as LocalityModel
                val location = list.value.split(",")
                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbLat)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLat,
                            location[0].trim()
                        )
                    )

                    deleteConfigTableByField(Config.dbLng)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLng,
                            location[1].trim()
                        )
                    )
                }
                addressViewModel.addLocation(
                    list.name,
                    location[0].trim(),
                    location[1].trim(),
                    "",
                    typeCity
                )
                updateCurrentCitySelection(list.name)
            }
        }
        hideShowRecentList(false)
        hideShowPopularList(false)
        ivLocationCitySelection.setOnClickListener(this)
        rlSearch.setOnClickListener(this)
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@CitySelectionActivity)) {
                super.onBackPressed()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateCurrentCitySelection(currentCityName: String) {
        myRoomDatabase.daoConfig().apply {
            deleteConfigTableByField(Config.dbCurrentCitySelection)
            insertConfigTable(
                TableConfig(
                    Config.dbCurrentCitySelection,
                    currentCityName
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                edPlace.setText(place.address)
                place.latLng?.let {
                    latitude = place.latLng!!.latitude
                    longitude = place.latLng!!.longitude
                }

                updateCurrentCitySelection(place.address.toString())
                Config.currentLat = if (latitude.toString().length >= 9) latitude.toString()
                    .substring(0, 9) else latitude.toString()
                Config.currentLng = if (longitude.toString().length >= 9) longitude.toString()
                    .substring(0, 9) else longitude.toString()

                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbLat)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLat,
                            Config.currentLat
                        )
                    )

                    deleteConfigTableByField(Config.dbLng)
                    insertConfigTable(
                        TableConfig(
                            Config.dbLng,
                            Config.currentLng
                        )
                    )
                }

                addressViewModel.addLocation(
                    place.address.toString(),
                    Config.currentLat,
                    Config.currentLng,
                    place.id.toString(),
                    typeCity
                )
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                status.statusMessage?.let { showMsgDialogAndProceed(it) }
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getResponse() {
        addressViewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }

        })

        addressViewModel.addLocation.observe(this, {
            when (it.status!!) {
                0 -> showMsgDialogAndProceed(it.message!!)
                1 -> {
                    goToMainActivity()
                }
            }
        })

        addressViewModel.responseGetRecentPopularLocations.observe(this, {
            when (it.status!!) {
                0 -> {
                    hideShowRecentList(false)
                    hideShowPopularList(false)
                    showMsgDialogAndProceed(it.message!!)
                }
                1 -> {
                    if (it.message!!.lowercase(Locale.getDefault()) == "success") {
                        //region recent location
                        val recentList = ArrayList<LocalityModel>()
                        it.getRecentPopularData!!.recentLocation?.let { recent ->
                            for (i in recent.indices) {
                                recentList.add(
                                    LocalityModel(
                                        recent[i].address!!,
                                        "${recent[i].latitude!!}, ${recent[i].longitude!!}, ${recent[i].id!!}"
                                    )
                                )
                            }
                            if (recentList.isNotEmpty()) {
                                hideShowRecentList(true)
                                val recentListAdapter =
                                    LocalityListAdapter(this, recentList, recentListener)
                                rvRecent.apply {
                                    adapter = recentListAdapter
                                    layoutManager = LinearLayoutManager(this@CitySelectionActivity)
                                }
                            }
                        }
                        //endregion

                        //region popular location
                        val popularList = ArrayList<LocalityModel>()
                        it.getRecentPopularData!!.popularLocation?.let { popular ->
                            for (i in popular.indices) {
                                popularList.add(
                                    LocalityModel(
                                        popular[i].address!!,
                                        "${popular[i].latitude!!}, ${popular[i].longitude!!}, ${popular[i].id!!}"
                                    )
                                )
                            }
                            if (popularList.isNotEmpty()) {
                                hideShowPopularList(true)
                                val popularListAdapter =
                                    LocalityListAdapter(this, popularList, popularListener)
                                rvPopularCities.apply {
                                    adapter = popularListAdapter
                                    layoutManager = LinearLayoutManager(this@CitySelectionActivity)
                                }
                            }
                        }
                        //endregion
                    }
                    //verify check
                    if (prefs.getLoginModel().mobileVerified == 0) {
                        editProfile("Please verify your account by phone")
                    }

                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun editProfile(msg: String) {
        try {
            val myDialog = DialogToast(this@CitySelectionActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.edit_profile)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                            startActivity(
                                Intent(
                                    this@CitySelectionActivity,
                                    EditProfileActivity::class.java
                                ).putExtra("citySelection", "Yes")
                            )
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

    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@CitySelectionActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "City Selection"
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
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideShowRecentList(isDataFound: Boolean) {
        //region recent list
        horLine1CitySelection.visibility = if (isDataFound) View.VISIBLE else View.GONE
        tvRecentTitle.visibility = if (isDataFound) View.VISIBLE else View.GONE
        rvRecent.visibility = if (isDataFound) View.VISIBLE else View.GONE
        //endregion
    }

    private fun hideShowPopularList(isDataFound: Boolean) {
        //region popular list
        horLine2CitySelection.visibility = if (isDataFound) View.VISIBLE else View.GONE
        tvPopularCitiesTitle.visibility = if (isDataFound) View.VISIBLE else View.GONE
        rvPopularCities.visibility = if (isDataFound) View.VISIBLE else View.GONE
        //endregion
    }

    private fun getRecentPopularList() {
        try {
            if (PubFun.isInternetConnection(this@CitySelectionActivity)) {
                addressViewModel.getRecentPopularLocationList()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToMainActivity() {
        Utils.showProgress(this)
        Handler(Looper.getMainLooper()).postDelayed({
            Utils.hideProgress(this)
            startActivity(Intent(applicationContext, MainActivity::class.java))
            Utils.slideEnter(this)
            finish()
        }, 2000)
    }

    fun setDialog() {
        val dialog = Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_push_notification);
        val window = dialog.getWindow();
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val btnTurnOnNotification =
            dialog.findViewById<CircularProgressButton>(R.id.btnTurnOnNotification)
        val llClose = dialog.findViewById<LinearLayout>(R.id.llClose)

        btnTurnOnNotification.setOnClickListener {
            dialog.dismiss()
        }

        llClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v) {
            ivLocationCitySelection -> {
                if (PubFun.isInternetConnection(this@CitySelectionActivity)) {
                    getLastLocation()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }

            rlSearch -> {
                if (PubFun.isInternetConnection(this@CitySelectionActivity)) {
                    val autocompleteIntent =
                        Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY,
                            listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.ADDRESS,
                                Place.Field.LAT_LNG,
                                Place.Field.TYPES
                            )
                        )
                            .setInitialQuery("")
                            .setCountry("In")
                            .build(this);
                    startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {

            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (LocationManagerCompat.isLocationEnabled(locationManager)) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        val sydney = LatLng(location.latitude, location.longitude)
//                        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                        val zoomLevel = 16.0f //This goes up to 21

//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))


                        val geocoder = Geocoder(this, Locale.getDefault())

                        val addresses: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        var address = ""
                        /*if (addresses[0].subLocality.isNotEmpty()) {
                            address = "$addresses[0].subLocality, "
                        }*/
                        if (addresses[0].locality.isNotEmpty()) {
                            address = "${addresses[0].locality}, "
                        }
                        if (addresses[0].adminArea.isNotEmpty()) {
                            address += "${addresses[0].adminArea}, "
                        }
                        if (addresses[0].postalCode.isNotEmpty()) {
                            address += addresses[0].postalCode
                        }

                        /*address = addresses[0].getAddressLine(0)*/

                        latitude = location.latitude
                        longitude = location.longitude
                        google_address = address

                        updateCurrentCitySelection(google_address)
                        Config.currentLat = if (latitude.toString().length >= 9) latitude.toString()
                            .substring(0, 9) else latitude.toString()
                        Config.currentLng =
                            if (longitude.toString().length >= 9) longitude.toString()
                                .substring(0, 9) else longitude.toString()

                        myRoomDatabase.daoConfig().apply {
                            deleteConfigTableByField(Config.dbLat)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbLat,
                                    Config.currentLat
                                )
                            )

                            deleteConfigTableByField(Config.dbLng)
                            insertConfigTable(
                                TableConfig(
                                    Config.dbLng,
                                    Config.currentLng
                                )
                            )
                        }

                        /*edPlace.setText(google_address)*/
                        addressViewModel.addLocation(
                            google_address,
                            Config.currentLat,
                            Config.currentLng,
                            "",
                            typeCity
                        )

                        /*addressViewModel.add_address(
                            google_address,
                            "",
                            "",
                            "",
                            "4",
                            "1",
                            "",
                            latitude.toString(),
                            longitude.toString()
                        )*/
//                        edt_address.setText(address)
//                        mMap.setMyLocationEnabled(true);


//                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
//                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                        /*getCurrentLocation(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )*/

                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        /*else {
            requestPermissions()
        }*/
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            101
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation


            // Add a marker in Sydney and move the camera
            val sydney = LatLng(mLastLocation.latitude, mLastLocation.longitude)
//            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

            val zoomLevel = 16.0f //This goes up to 21

//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

            val geocoder = Geocoder(this@CitySelectionActivity, Locale.getDefault())

            val addresses: List<Address> =
                geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
            val address: String = addresses[0].getAddressLine(0)
            val city: String = addresses[0].getLocality()
            val state: String = addresses[0].getAdminArea()
            val zip: String = addresses[0].getPostalCode()
            val country: String = addresses[0].getCountryName()


            edt_address.setText(address)
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude

//            mMap.setMyLocationEnabled(true);

        }

    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
                getLastLocation()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}