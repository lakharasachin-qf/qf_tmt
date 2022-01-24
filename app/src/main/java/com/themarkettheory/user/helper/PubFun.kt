package com.themarkettheory.user.helper

import android.app.Activity
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import okio.ByteString.Companion.readByteString
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class PubFun {
    companion object {
        fun hideKeyboard(activity: Activity) {
            try {
                val view = activity.currentFocus
                if (view != null) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }

        fun showKeyboard(activity: Activity) {
            try {
                val view = activity.currentFocus
                if (view != null) {
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }

        fun getShader(activity: Activity, textView: MaterialTextView): Shader {
            val lineWidth = textView.paint.measureText(textView.text!!.toString())
            val lineHeight = textView.paint.fontMetrics.bottom - textView.paint.fontMetrics.top
            return LinearGradient(
                0f,
                0f,
                lineWidth,
                textView.textSize,
                intArrayOf(
                    ContextCompat.getColor(activity, R.color.mySummerSky),
                    ContextCompat.getColor(activity, R.color.myTurquoise),
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }

        fun getSendDateFromat(date: String): String {
            var senDate = Date()
            var temDate = ""
            val input = SimpleDateFormat("dd MMM yyyy")
            val output = SimpleDateFormat("dd/MM/yyyy")
            try {
                senDate = input.parse(date) // parse input
                temDate = (output.format(senDate)) // format output
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return date
        }

        fun hideShowPassword(
            context: Context,
            showPassword: Boolean,
            edPassword: TextInputEditText,
            ivHideShowPassword: ImageView
        ) {
            var isShowPassword = showPassword
            if (isShowPassword) {
                edPassword.transformationMethod =
                    PasswordTransformationMethod()
                ivHideShowPassword.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_visible_eye
                    )
                )
                isShowPassword = false
            } else {
                edPassword.transformationMethod = null
                ivHideShowPassword.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_invisible_eye
                    )
                )
                isShowPassword = true
            }
        }

        fun readAssetsFile(context: Context, fileName: String): String {
            var jsonRes = ""
            try {
                val source = context.assets.open(fileName)
                jsonRes = source.readByteString(source.available()).string(Charsets.UTF_8)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return jsonRes
        }

        fun isNull(str: String?, defStr: String): String {
            if (str != null && str.isNotBlank()) {
                return str
            }
            return defStr
        }

        fun addTimeInCurrentTime(
            returnDateFormat: SimpleDateFormat,
            type: Int,
            timeValue: Int
        ): String {
            try {
                val cal = Calendar.getInstance()
                cal.add(type, timeValue)
                return returnDateFormat
                    .format(
                        SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.getDefault())
                            .parse(cal.time.toString())
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        fun parseDate(
            time: String?,
            inputPattern: String?,
            outputPattern: String?
        ): String? {
//        String inputPattern = "yyyy-MM-dd";
//        String outputPattern = "dd-MM-yyyy";
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
            val date: Date?
            var str: String? = ""
            try {
                date = inputFormat.parse(time)
                if (date != null) {
                    str = outputFormat.format(date)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return str
        }

        public fun toCamelCase(s: String): String? {
            if (s.length == 0) {
                return s
            }
            val parts = s.split(" ".toRegex()).toTypedArray()
            var camelCaseString = ""
            for (part in parts) {
                camelCaseString = camelCaseString + toProperCase(part) + " "
            }
            return camelCaseString
        }

        public fun toProperCase(s: String): String {
            return s.substring(0, 1).uppercase() +
                    s.substring(1).lowercase()
        }

        fun isInternetConnection(context: Context): Boolean {
            try {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val nw = connectivityManager.activeNetwork ?: return false
                    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                    return when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        //for other device how are able to connect with Ethernet
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        //for check internet over Bluetooth
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                        else -> false
                    }
                } else {
                    val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                    return nwInfo.isConnected
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        fun getOfferTitle(
            context: Context,
            title: String,
            discountAmount: String,
            buyQty: String
        ): SpannableStringBuilder {
            val ssbTitle = SpannableStringBuilder(title)
            try {
                val fscGoldenSand = ForegroundColorSpan(
                    ContextCompat.getColor(
                        context, R.color.myGoldenSand
                    )
                )
                if (title.contains(discountAmount)) {
                    if (title.contains("%")) {
                        ssbTitle.setSpan(
                            fscGoldenSand,
                            title.indexOf(discountAmount),
                            title.indexOf(discountAmount) +
                                    discountAmount.length + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        ssbTitle.setSpan(
                            fscGoldenSand,
                            title.indexOf(discountAmount),
                            title.indexOf(discountAmount) +
                                    discountAmount.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                } else if (title.contains(buyQty.toString())) {
                    ssbTitle.setSpan(
                        fscGoldenSand,
                        title.indexOf(buyQty),
                        title.indexOf(buyQty) +
                                buyQty.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ssbTitle
        }

        fun filterEmojisFromEditText(): Array<InputFilter>? {
            val emojiFilter =
                InputFilter { source, start, end, dest, dstart, dend ->
                    for (index in start until end) {
                        val type = Character.getType(source[index])
                        if (type == Character.SURROGATE.toInt()
                            || type == Character.NON_SPACING_MARK.toInt()
                            || type == Character.OTHER_SYMBOL.toInt()
                        ) {
                            return@InputFilter ""
                        }
                    }
                    null
                }
            return arrayOf(emojiFilter)
        }

        fun getReadableDate(date: String): String {
            var suffix = "th"
            when (date) {
                "01", "21", "31" -> suffix = "st"
                "02", "22" -> suffix = "nd"
                "03", "23" -> suffix = "rd"
            }
            return date + suffix
        }

        fun getMonthInName(month: String): String {
            return when (month) {
                "01" -> "January"
                "02" -> "February"
                "03" -> "March"
                "04" -> "April"
                "05" -> "May"
                "06" -> "June"
                "07" -> "July"
                "08" -> "August"
                "09" -> "September"
                "10" -> "October"
                "11" -> "November"
                else -> "December"
            }
        }
    }
}