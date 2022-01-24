package com.themarkettheory.user.helper

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.andrognito.flashbar.Flashbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.themarkettheory.user.R
import com.themarkettheory.user.model.Data
import com.themarkettheory.user.model.OfferDatum
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Admin on 19-01-2018.
 */

@SuppressLint("StaticFieldLeak")
object Utils {
    lateinit var progressDialog: ProgressDialog
    var activity: Activity? = null
    lateinit var alertDialog: AlertDialog
    var category = MutableLiveData<String>()
    fun changeFont(context: Context, font: Int, value: String): SpannableString {
        val typeface = ResourcesCompat.getFont(context, font)
        val s = SpannableString(value)
        s.setSpan(
            typeface, 0, s.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return s
    }

    fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun clearSharedPreference(context: Context) {
        val preferences: SharedPreferences =
            context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor
        editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isEmptyTextInputEditText(
        input: TextInputLayout,
        edt: TextInputEditText,
        msg: String
    ): Boolean {
        if (edt.text.toString().isEmpty()) {
            input.isErrorEnabled = true
            input.error = msg
            edt.error = msg
            edt.requestFocus()
            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isEmptyEditText(edt: EditText, msg: String): Boolean {
        if (edt.text.toString().isEmpty()) {
            edt.error = msg
            edt.requestFocus()
            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isPasswordMatch(edt: TextInputEditText, editText: TextInputEditText, msg: String): Boolean {
        if (!edt.text.toString().matches(editText.text.toString().trim { it <= ' ' }.toRegex())) {
            editText.error = msg
            editText.requestFocus()
            return true
        }
        return false
    }

    fun showProgress(context: Activity) {
        hideProgress(context)

        val dialogBuilder = AlertDialog.Builder(context, R.style.NewDialog)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.apply {
            show()
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    fun hideProgress(context: Context) {
//        progressDialog.hide()
        if (Utils::alertDialog.isInitialized && alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isPhoneCorrect(edt: EditText): Boolean {
        if (edt.text.toString().length < 10) {
            edt.error = "Please enter 10 digit number"
            edt.requestFocus()
            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isEmailCorrect(edt: TextInputEditText): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!edt.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
            edt.error = "Please enter correct email"
            edt.requestFocus()
            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun isEmailCorrect(edt: EditText): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!edt.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
            edt.error = "Please enter correct email"
            edt.requestFocus()
            return true
        }
        return false
    }


    /*fun getJsonFromJsonArray(jsonObject: JSONArray): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
    }

    fun getJsonFromJsonObject(jsonObject: JSONObject): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())
    }*/

    fun getDecimalFormat(): DecimalFormat {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df
    }

    fun formatDate(
        date: String, initDateFormat: String,
        endDateFormat: String
    ): String? {
        var parsedDate: String? = null
        val initDate: Date
        try {
            initDate = SimpleDateFormat(initDateFormat, Locale.getDefault()).parse(date)
            val formatter = SimpleDateFormat(endDateFormat, Locale.getDefault())
            parsedDate = formatter.format(initDate)

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        return parsedDate
    }

    fun getAddress(context: Context, lat: Double, lng: Double): String {
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>
        var address = ""
        try {
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.isNotEmpty()) {
                address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                val postalCode = addresses[0].postalCode
                val knownName = addresses[0].featureName
            }
            return address
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return address
    }


    fun getWidthOfScreen(mContext: Context): Int {
        val displayMetrics = DisplayMetrics()
        try {
            (mContext as AppCompatActivity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        } catch (e: Exception) {
            val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        return displayMetrics.widthPixels
    }

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }

    fun replaceFragment(
        fragment: Fragment, Tag: String,
        isBackStack: Boolean, isMenuSupport: Boolean,
        fragmentManager: FragmentManager, id: Int
    ) {
        val ft = fragmentManager.beginTransaction()
        fragment.setHasOptionsMenu(isMenuSupport)
        ft.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        ft.replace(id, fragment, Tag)
        if (isBackStack) {
            ft.addToBackStack(Tag)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    @SuppressLint("NewApi")
    fun setDatePickerDialog(context: Context, editText: EditText, onComplete: () -> Unit) {

        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val newCalendar = Calendar.getInstance()
        newCalendar.add(Calendar.YEAR, -5)
        val dateDialog = DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                editText.setText(dateFormatter.format(newDate.time))
                onComplete()
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val inputDate: Date
        try {
            inputDate = dateFormat.parse(dateFormat.format(Date()))
            dateDialog.datePicker.maxDate = inputDate.time

            dateDialog.show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    fun setDatePickerDialog1(context: Context, editText: EditText, onComplete: () -> Unit) {

        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val newCalendar = Calendar.getInstance()
        val dateDialog = DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                editText.setText(dateFormatter.format(newDate.time))
                onComplete()
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val inputDate: Date
        try {
            inputDate = dateFormat.parse(dateFormat.format(Date()))
            dateDialog.datePicker.minDate = inputDate.time

            dateDialog.show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("NewApi")
    fun setTimePickerDialog(context: Context, editText: EditText) {

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            editText.setText(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time))
        }

        TimePickerDialog(
            context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    @SuppressLint("NewApi")
    fun setTimePickerDialog(context: Context, editText: EditText, listener: (String) -> Unit) {

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            editText.setText(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time))
            listener(editText.text.toString())
        }

        TimePickerDialog(
            context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }


    @SuppressLint("NewApi")
    fun setTimePickerDialog(context: Context, editText: TextView, listener: (String) -> Unit) {

        val cal = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            editText.setText(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time))
            listener(editText.text.toString())
        }

        TimePickerDialog(
            context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun getDistance(startLat: Double, startLong: Double, endLat: Double, endLong: Double): Double {
        val startPoint = Location("locationA")
        startPoint.latitude = startLat
        startPoint.longitude = startLong

        val endPoint = Location("locationA")
        endPoint.latitude = endLat
        endPoint.longitude = endLong

        val distance = startPoint.distanceTo(endPoint) / 1000
        val number3digits: Double = String.format("%.3f", distance).toDouble()
        val number2digits: Double = String.format("%.2f", number3digits).toDouble()

        return String.format("%.1f", number2digits).toDouble()
    }

    fun showError(view: View, message: String?) {
        Snackbar.make(view, message.toString(), Snackbar.LENGTH_SHORT).show()
    }

    fun showToast(context: Activity, text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun formatTime(time: Date): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.format(time)
        return date
    }

    fun parseTime(time: String): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(time)
        return date
    }

    fun parseDate(time: String): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(time)
        return date
    }

    fun getLocationAddress(context: Context, location: Location): Any {
        var geocoder: Geocoder;
        var addresses: List<Address>;
        geocoder = Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        ); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        var address: String = addresses.get(0)
            .getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        var city: String = addresses.get(0).getLocality();
        var state = addresses.get(0).getAdminArea();
        var country = addresses.get(0).getCountryName();
        var postalCode = addresses.get(0).getPostalCode();
        var knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        return address
    }

    @SuppressLint("NewApi")
    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo?.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }

    /*fun getImageEncodeImage(bitmap: Bitmap): Image {
        val base64EncodedImage = Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        val byteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        val imageBytes = byteArrayOutputStream.toByteArray();
        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }*/

    fun findToday(value: Long): String {
        return when {
            DateUtils.isToday(value) -> "Today"
            DateUtils.isToday(value + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
            else -> {
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                formatter.format(Date(value))
            }
        }
    }

    fun waitForAnimation(view: View, onComplete: () -> Unit) {
        onComplete()
        /*AnimationUtils.zoomInzoomOut(view)
        Handler().postDelayed({
            onComplete()
        },300)*/
    }

    fun slideEnter(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    /*fun SlideRight(context: Context) {
        (context as Activity).overridePendingTransition(R.anim.slide_from_left, R.anim.stay)
    }*/

    fun slideExit(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
    }

    fun dpToPx(dp: Float, displayMetrics: DisplayMetrics): Int =
        (dp * displayMetrics.density).toInt()

    fun pxToDp(px: Float, displayMetrics: DisplayMetrics): Int =
        (px / displayMetrics.density).toInt()

    fun showSnackbar(context: Context?, text: String?) {
        com.nispok.snackbar.Snackbar.with(context) // context
            .text(text)// text to display
            .textColor(R.color.white)
            .show(context as Activity?)
    }

    fun showFlashBar(context: Activity, text: String?) {
        text?.let {
            Flashbar.Builder(context)
                .gravity(Flashbar.Gravity.BOTTOM)
                .message(text)
                .duration(1000)
                .build()
        }
    }

    fun showFlashBar(context: Activity, text: String, title: String) {
        Flashbar.Builder(context)
            .gravity(Flashbar.Gravity.BOTTOM)
            .title(title)
            .message(text)
            .duration(1000)
            .build()
    }

    fun setMargins(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun createRequestBody(value: String?): RequestBody {
        return value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    fun createMultiPart(file: File, key: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }

    fun createVideoMutilPart(file: File, key: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            key,
            URLEncoder.encode(file.name, "utf-8"),
            requestFile
        )
    }

    fun setTranscluentStatusBar(context: Activity) {
        val window = context.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = 0x00000000 // transparent
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            window.addFlags(flags)
        } else if (Build.VERSION.SDK_INT > 21) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            //toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        context.actionBar?.hide()

    }

    fun setGradient(context: Context, textview: TextView, text: String) {
        textview.setText(text)
        val paint: TextPaint = textview.paint
        val width = paint.measureText(text)
        val textShader: Shader = LinearGradient(
            0.0f,
            0.0f,
            dpToPx(200.0f, context.resources.displayMetrics).toFloat(),
            textview.getTextSize(),
            intArrayOf(
                Color.parseColor("#28edd1"),
                Color.parseColor("#1db0e7")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textview.getPaint().setShader(textShader);
    }

    fun showDialog(context: Activity, title: String, msg: String, listener: () -> Unit) {
        val builder =
            AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        builder.setTitle(title)
        builder.setMessage(msg)
        val positiveText = context.getString(android.R.string.ok)

        builder.setPositiveButton(
            positiveText
        ) { dialog, which ->
            // positive button logic
            listener()
        }
        val negativeText = context.getString(android.R.string.cancel)
        builder.setNegativeButton(
            negativeText
        ) { dialog, which ->
            // negative button logic
        }
        val dialog = builder.create()
        // display dialog
        dialog.show()
    }

    fun roundToHalf(d: Double): Double {
        return Math.round(d * 2) / 2.0
    }

    fun getDiscountAmount(discountType: String?, discountAmount: String?, subTotal: Float): Float {
        var discountedFinalAmount = 0.0f
        if (discountType.isNullOrEmpty()) {
            return discountedFinalAmount
        } else {

            if (discountType == "1") {
                discountedFinalAmount = subTotal.times(discountAmount!!.toFloat()).div(100)
            } else if (discountType == "2") {
                discountedFinalAmount = discountAmount!!.toFloat()
            }
            if (subTotal < discountedFinalAmount) {
                discountedFinalAmount = 0.0f
            }
            return discountedFinalAmount
        }
    }

    fun convert24To12System(hour: Int, minute: Int): String? {
        var hour = hour
        var time = ""
        var am_pm = ""
        if (hour < 12) {
            if (hour == 0) hour = 12
            am_pm = "AM"
        } else {
            if (hour != 12) hour -= 12
            am_pm = "PM"
        }
        var h = hour.toString() + ""
        var m = minute.toString() + ""
        if (h.length == 1) h = "0$h"
        if (m.length == 1) m = "0$m"
        time = "$h:$m $am_pm"
        return time
    }


    fun getDiscountAmount(offerDatum: OfferDatum, subTotal: Float): Float {
        var discountedFinalAmount = 0.0f
        if (offerDatum?.discountType == null) {
            return discountedFinalAmount
        } else {
            if (offerDatum?.discountType == 1) {
                discountedFinalAmount = subTotal.times(offerDatum?.discountAmount!!.toFloat()).div(
                    100
                )
            } else if (offerDatum?.discountType == 2) {
                discountedFinalAmount = offerDatum?.discountAmount!!.toFloat()
            } else if (offerDatum?.discountType == 3) {
                discountedFinalAmount = offerDatum?.discountAmount!!.toFloat()
            }
            if (offerDatum.maxDiscount!! < discountedFinalAmount) {
                discountedFinalAmount = offerDatum.maxDiscount!!
            }
            if (subTotal < discountedFinalAmount) {
                discountedFinalAmount = 0.0f
            }
            return discountedFinalAmount
        }
    }

    fun getAddress(data: Data): String {
        /*var address = ""
        if(data?.address?.houseNumber.isNullOrEmpty() && data?.address?.tower.isNullOrEmpty()){
            return checkGoogleAddress(data)
        }else{
            return data?.address?.houseNumber!!+","+data?.address?.tower
        }*/
        return data.address!!
//        return address
    }

    /*private fun checkGoogleAddress(data: Data): String {
        if (data?.address?.googleAddress.isNullOrEmpty()) {
            return ""
        } else {
            return data.address?.googleAddress!!
        }
    }*/

    /*fun navigateScreen(context: Activity, it: Data) {
        val prefs = Prefs(context)
        prefs.setLoginModel(it)
        prefs.setAccessToken(context, it.token!!)
        if (prefs.getLoginModel().zip.isNullOrEmpty()) {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        } else if (prefs.getLoginModel().mobileVerified == 0) {
//            startActivity(Intent(this, VerifyOtpActivity::class.java))
            context.startActivity(Intent(context, ProfileActivity::class.java))
        } else if (prefs.getLoginModel().address?.id == null) {
            context.startActivity(Intent(context, CitySelectionActivity::class.java))
        } else {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
        context.finishAffinity()
    }*/


}
