package com.themarkettheory.user.ui.main.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.makeramen.roundedimageview.RoundedImageView
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.newmodels.overview.Data
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import kotlinx.android.synthetic.main.activity_add_address.*
import java.util.*


class MapActivity : BaseActivity(), OnMapReadyCallback {
    lateinit var mapFragment: SupportMapFragment
    lateinit var mMap: GoogleMap
    var serviceDetail: Data? = null

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mCurrentLat: Double = 0.0
    private var mCurrentLng: Double = 0.0
    private var handlerCurrentLocation = Handler(Looper.getMainLooper())
    private lateinit var runnableCurrentLocation: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        if (PubFun.isInternetConnection(this@MapActivity)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()

            /*if (intent != null) {
                serviceDetail = intent.getSerializableExtra("serviceDetail") as ServiceDetail?
            }*/
            serviceDetail = Config.serviceDetail!!

            mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

            mapFragment.getMapAsync(this)
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (LocationManagerCompat.isLocationEnabled(locationManager)) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        mCurrentLat = location.latitude
                        mCurrentLng = location.longitude
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        /*else {
          //  requestPermissions()
        }*/
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            mCurrentLat = mLastLocation.latitude
            mCurrentLng = mLastLocation.longitude
        }
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

    override fun onMapReady(p0: GoogleMap?) {
        try {
            runnableCurrentLocation = Runnable {
                if (mCurrentLat == 0.0 && mCurrentLng == 0.0) {
                    handlerCurrentLocation.postDelayed(runnableCurrentLocation, 2000)
                } else {
                    handlerCurrentLocation.removeCallbacks(runnableCurrentLocation)
                    handlerCurrentLocation.removeCallbacksAndMessages(null)

                    //region Start Map Section
                    mMap = p0!!
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json
                        )
                    );
                    mMap.uiSettings.isScrollGesturesEnabled = true
                    val markerOptions = MarkerOptions()
                    val bounds = LatLngBounds.builder()
                    val latLng = LatLng(
                        serviceDetail?.latitude!!.toDouble(),
                        serviceDetail?.longitude!!.toDouble()
                    )
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
                    mMap.animateCamera(cameraUpdate)
                    markerOptions.position(latLng)
                    bounds.include(latLng)
                    markerOptions.icon(
                        bitmapDescriptorFromVector(
                            this,
                            R.drawable.ic_location_selected_svg
                        )
                    )
                    markerOptions.title(serviceDetail?.id.toString())
                    mMap.addMarker(markerOptions)

                    val options = PolylineOptions().width(5F).color(Color.BLUE).geodesic(true)
                    options.add(LatLng(mCurrentLat, mCurrentLng))
                    options.add(
                        LatLng(
                            serviceDetail?.latitude!!.toDouble(),
                            serviceDetail?.longitude!!.toDouble()
                        )
                    )
                    mMap.addPolyline(options)

                    mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(p0: Marker): View? {
                            return null
                        }

                        override fun getInfoWindow(p0: Marker?): View {
                            val v = View.inflate(
                                this@MapActivity,
                                com.themarkettheory.user.R.layout.infowindow,
                                null
                            )
                            val iv =
                                v.findViewById<RoundedImageView>(com.themarkettheory.user.R.id.iv)
                            val tvVendorName =
                                v.findViewById<TextView>(com.themarkettheory.user.R.id.tvVendorName)
                            val tvDistance =
                                v.findViewById<TextView>(com.themarkettheory.user.R.id.tvDistance)
                            val tvRating =
                                v.findViewById<TextView>(com.themarkettheory.user.R.id.tvRating)
                            val rating =
                                v.findViewById<RatingBar>(com.themarkettheory.user.R.id.rating)


                            Glide.with(this@MapActivity).load(serviceDetail?.image!![0].image)
                                .error(R.drawable.ic_camera)
                                .into(iv)
                            tvVendorName.setText(serviceDetail?.title)
                            tvDistance.setText(serviceDetail?.distance)
                            tvRating.setText("(" + serviceDetail?.rating + ")")
                            rating.rating = serviceDetail?.rating!!.toFloat()

                            return v
                        }

                    })
                    //endregion
                }
            }
            handlerCurrentLocation.postDelayed(runnableCurrentLocation, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@MapActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Map"
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