package com.themarkettheory.user.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.AddressList
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.AddressTagListAdapter
import com.themarkettheory.user.viewmodel.AddressViewModel
import kotlinx.android.synthetic.main.activity_add_address.*
import java.util.*
import kotlin.collections.ArrayList

class AddAddressActivity : BaseActivity(), OnMapReadyCallback {
    private var google_address: String = ""
    var latitude = 0.0
    var longitude = 0.0
    var list_of_tag = ArrayList<String>()
    lateinit var addressViewModel: AddressViewModel
    private lateinit var mMap: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var model: AddressList? = null
    var address_type = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (intent != null) {
            model = intent.getSerializableExtra("model") as AddressList?
        }

        list_of_tag.add(resources.getString(R.string.home))
        list_of_tag.add(resources.getString(R.string.work))
        list_of_tag.add(resources.getString(R.string.office))
        list_of_tag.add(resources.getString(R.string.other))

        if (model != null) {
            edt_house_tower.setText(model?.tower)
            edt_house_floor.setText(model?.floor)
            edt_house_number
                .setText(model?.houseNumber)
        }
        img_close.setOnClickListener {
            onBackPressed()
        }

        ll_ConfirmLocation.setOnClickListener {

            ll_completed_address.visibility = View.VISIBLE
            txt_change.visibility = View.VISIBLE
            ll_ConfirmLocation.visibility = View.GONE
            showTagList()
        }

        txt_change.setOnClickListener {
            ll_completed_address.visibility = View.GONE
            txt_change.visibility = View.GONE
            ll_ConfirmLocation.visibility = View.VISIBLE
        }

        ll_save_address.setOnClickListener {

            val str_full_address = edt_address.text.toString().trim()
            val str_house_number = edt_house_number.text.toString().trim()
            val str_house_floor = edt_house_floor.text.toString().trim()
            val str_house_tower = edt_house_tower.text.toString().trim()

            if (str_full_address.equals("")) {
                showMsgDialogAndProceed("Please enter full address")
            } else if (str_house_number.equals("")) {
                showMsgDialogAndProceed("Please enter house number")
            } else if (str_house_floor.equals("")) {
                showMsgDialogAndProceed("Please enter house floor")

            } else if (str_house_tower.equals("")) {
                showMsgDialogAndProceed("Please enter house tower")
            } else {
                if (address_type.equals("")) {
                    showMsgDialogAndProceed(
                        "Please select address type"
                    )
                } else {
                    if (model != null && model?.id != null) {
                        addressViewModel.edit_address(
                            google_address,
                            str_house_number,
                            str_house_floor,
                            str_house_tower,
                            address_type,
                            "0",
                            edt_address.text.toString(),
                            latitude.toString(),
                            longitude.toString(),
                            model?.id.toString()
                        )

                    } else {
                        addressViewModel.add_address(
                            google_address,
                            str_house_number,
                            str_house_floor,
                            str_house_tower,
                            address_type,
                            "0",
                            edt_address.text.toString(),
                            latitude.toString(),
                            longitude.toString()
                        )

                    }
                }
            }

        }


        if (model != null && model?.id != null) {
            txt_address_save.setText(resources.getString(R.string.saveadddress))

            showTagList()
        } else {
            txt_address_save.setText(resources.getString(R.string.update_address))
        }

        getResponse()
    }

    private fun showTagList() {
        val addressTagListAdapter = AddressTagListAdapter(this, list_of_tag) {
            address_type = it
        }
        list_of_location_save.adapter = addressTagListAdapter
        list_of_location_save.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun getResponse() {
        addressViewModel.isLoading.observe(this, androidx.lifecycle.Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        addressViewModel.responseAddAddress.observe(this, androidx.lifecycle.Observer {
            Utils.showSnackbar(this, it.message)
            if (it.status!!) {
                Handler().postDelayed({
                    onBackPressed()
                }, 2000)
            }
        })

        addressViewModel.responseEditAddress.observe(this, androidx.lifecycle.Observer {
            Utils.showSnackbar(this, it.message)
            if (it.status!!) {
                Handler().postDelayed({
                    onBackPressed()
                }, 2000)
            }
        })
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.clear()
        mMap.setOnMapClickListener { arg0 ->
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(arg0.latitude, arg0.longitude)
                ).title("It's Me!")
            )

            val geocoder = Geocoder(this, Locale.getDefault())

            val addresses: List<Address> =
                geocoder.getFromLocation(arg0.latitude, arg0.longitude, 1)
            val address: String = addresses[0].getAddressLine(0)
            google_address = address
            edt_address.setText(address)

            latitude = arg0.latitude
            longitude = arg0.longitude

        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            101
        )
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


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {

            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (LocationManagerCompat.isLocationEnabled(locationManager)) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {

                        val sydney = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(sydney))
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                        val zoomLevel = 16.0f //This goes up to 21

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))


                        val geocoder = Geocoder(this, Locale.getDefault())

                        val addresses: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address: String = addresses[0].getAddressLine(0)

                        latitude = location.latitude
                        longitude = location.longitude
                        google_address = address
                        edt_address.setText(address)
//                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMapClickListener { arg0 ->
                            mMap.clear()

                            mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(arg0.latitude, arg0.longitude)
                                ).title("It's Me!")
                            )

                            val geocoder =
                                Geocoder(this@AddAddressActivity, Locale.getDefault())

                            val addresses: List<Address> =
                                geocoder.getFromLocation(arg0.latitude, arg0.longitude, 1)
                            val address: String = addresses[0].getAddressLine(0)
                            google_address = address
                            edt_address.setText(address)
                            latitude = location.latitude
                            longitude = location.longitude

                        }


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
        /* else {
             requestPermissions()
         }*/
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation


            // Add a marker in Sydney and move the camera
            val sydney = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

            val zoomLevel = 16.0f //This goes up to 21

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel))

            val geocoder = Geocoder(this@AddAddressActivity, Locale.getDefault())

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
            mMap.setOnMapClickListener { arg0 ->
                mMap.clear()

                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(arg0.latitude, arg0.longitude)
                    ).title("It's Me!")
                )

                val geocoder = Geocoder(this@AddAddressActivity, Locale.getDefault())

                val addresses: List<Address> =
                    geocoder.getFromLocation(arg0.latitude, arg0.longitude, 1)
                val address: String = addresses[0].getAddressLine(0)
                edt_address.setText(address)

                latitude = arg0.latitude
                longitude = arg0.longitude


            }


        }

    }

    override fun onResume() {
        super.onResume()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@AddAddressActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Add Address"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}