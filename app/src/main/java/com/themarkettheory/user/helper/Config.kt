package com.themarkettheory.user.helper

import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.newmodels.overview.Data
import com.themarkettheory.user.newmodels.overview.Image
import com.themarkettheory.user.ui.main.activity.MainActivity

class Config {
    companion object {
        var isLoginWithSocialButton = false
        var isLoginWithSocialName = ""
        val socialLoginResponseBundle = "socialLoginResponse"
        val countryCode = "+91"
        val otpResendTimer = 30
        var password = ""
        var socialId = ""
        var email = ""
        var phone = ""
        var mainActivity: MainActivity? = null
        var currentLat = ""
        var currentLng = ""
        const val signUpActivity = "signUpActivity"
        const val signInActivity = "signInActivity"
        var serviceDetail: Data? = null
        var overviewImage: ArrayList<Image>? = null
        var FragmentCalled: Fragment? = null
        var vendorDetailServiceId = ""
        var vendorDetailServiceName = ""
        var foodCategorySelectedId = ""
        var bottomBarClickedName = ""
        const val homeBottomBarClicked = "Home"
        const val favoriteBottomBarClick = "Favorite"
        const val locationBottomBarClick = "Location"
        const val eventBottomBarClick = "Event"
        const val menuBottomBarClick = "Menu"
        var isCouponRedeemButtonVisible = false
        var menuServiceId = ""
        var menuServiceName = ""
        var isHomeGoCartClicked = false
        var isVideoPlay = true
        var inBucketPoints = "0"
        var inBucketCount = "0"
        var isCouponRedeem = false
        var getSelectedCouponCode = ""
        var isCouponApplied = false
        var isCouponDiscountType = 0
        var isCouponBuyQty = 0
        var isCouponGetQty = 0
        var isCouponMenuId = 0
        var isCouponBuyGetSelected = false
        var isBucketAddMoreClicked = false
        const val subCategory0IndexRestaurantOldName = "All"
        const val subCategory0IndexRestaurantNewName = "Home"
        var isCouponOpeningFromBucket = false
        var isCouponOpeningFromOverview = false
        var isnotification = 0
        var isCouponDetailComingFromCouponActivity = false
        var foodCategoryPreviousPosition = 0
        var isMyPointClickedFromHome = false
        var isMyCouponClickedFromHome = false
        var isMyCouponClickedFromProfileMenu = false
        var isMyBookingClickedFromHome = false
        var isVendorComingFromCouponDetail = false
        var isVendorComingFromMapFragment = false
        var isEventMoveToback = false
        var isFavoriteMoveToBack = false
        var isFavoriteBottomBarListClick = false
        var isCouponComingFromMainActivity = false
        var isCouponComingFromAllFragment = false
        var isCouponComingFromOverviewViewAll = false
        var isCouponComingFromMenuFragment = false
        const val autoDialogDismissTimeInSec = 3
        var isPreOrder = false
        var fragmentName = ""
        const val mapFragment = "mapFragment"
        var enablePointsFilter = false
        var isMyPointsActivityComingFromOverviewFragment = false
        var isPopularLocationListCallFromInside = false
        var isVendorDetailComingFromTotalPoints = false
        const val newPattern = "dd-MM-yyyy"


        //Shared Preferences
        lateinit var spPermission: SharedPreferences
        const val sharedPreferencePermission = "PERMISSION"

        var isMenuFragmentComingFrom = ""
        const val isMenuFragmentComingFromBucketCart = "Bucket Cart"
        const val isMenuFragmentComingFromBookingTable = "Booking Table"

        var isOverviewPlayVideo = true

        var isVendorDetailOpeningFrom = ""
        const val isVendorDetailComingFromMapFragment = "Map Fragment"

        var isBookingDetailOpeningFrom = ""
        const val isBookingDetailOpeningFromMyBookingList = "My Booking List"
        const val isBookingDetailOpeningFromRecommnededListAdapter = "Recommended List Adapter"
        const val isBookingDetailOpeningFromLiveDealListAdapter = "Live Deal List Adapter"
        const val isBookingDetailOpeningFromNearestListAdapter = "Nearest List Adapter"
        const val isBookingDetailOpeningFromTotalPoints = "Total Points"

        var isCouponDetailOpeningFrom = ""
        const val isCouponDetailOpeningFromAllFragment = "All Fragment"
        const val isCouponDetailOpeningFromOverview = "Overview"

        var isEventBottomBarClicked = false

        //Date & Time Format
        const val requestDateFormat = "yyyy-MM-dd"
        const val requestTimeFormat = "HH:mm:ss"
        const val defaultDateFormat = "dd/MM/yyyy"
        const val defaultTimeFormat = "hh:mm aa"
        const val editProfileDateFormat = "dd MMM yyyy"

        //Restaurant List
        var isRestaurantListOpeningFrom = ""
        const val restaurantListOpeningFromMapFragment = "MapFragment"
        const val restaurantListOpeningFromRecommendedSeeAll = "RecommendedSeeAll"
        const val restaurantListOpeningFromTrendingOfferSeeAll = "TrendingOfferSeeAll"
        const val restaurantListOpeningFromHomeFragment = "HomeFragment"
        const val restaurantListOpeningFromLiveDealSeeAll = "LiveDealSeeAll"
        const val restaurantListOpeningFromNearestSellAll = "NearestSeeAll"
        const val restaurantListOpeningFromPopularLocationSeeAll = "PopularLocationSeeAll"
        const val restaurantListOpeningFromFavoriteBottomSection = "FavoriteBottomSection"
        const val restaurantListOpeningFromPopularLocationId = "PopularLocationId"

        //Database objects
        lateinit var myRoomDatabase: MyRoomDatabase
        val gson = GsonBuilder().serializeNulls().create()
        const val dbNewLoginRes = "NewLoginRes"
        const val dbLat = "Lat"
        const val dbLng = "Lng"
        const val dbCurrentCitySelection = "CurrentCitySelection"
        const val dbNewHomeRes = "NewHomeRes"
        const val dbVerifyOTPEmail = "verifyOTPEmail"
        const val dbIsDashboard = "isDashboard"
        const val dbVerifyOTPNavigatesFrom = "verifyOTPNavigatesFrom"
        const val dbSignUpData = "SignUpData"
        const val dbSocialLogin = "SocialLogin"
        const val dbOrderId = "OrderId"
        const val dbTableBookingId = "TableBookingId"
        const val dbNewBookingDetailRes = "NewBookingDetailRes"
        const val dbMyOrderListRecyclerViewPosition = "MyOrderListRecyclerViewPosition"
        const val dbOfferListServiceId = "OfferListServiceId"
        const val dbOfferListResRowData = "OfferListResRowData"
        const val dbIsRedeemedServiceId = "isRedeemedServiceId"
        const val dbNewPointsDataRowServiceDetails = "NewPointsDataRowServiceDetails"
        const val dbNewPointsDataRow = "NewPointsDataRow"
        const val dbServiceId = "ServiceId"
        const val dbPopularLocationId = "popularLocationId"
        const val dbPopularLocationAddress = "popularLocationAddress"

        // custom Toast type...
        const val TYPE_SUCCESS = 0
        const val TYPE_INFO = 1
        const val TYPE_WARNING = 2
        const val TYPE_ERROR = 3
        const val TYPE_BLANK = 4

        var typeface: Typeface? = null
        var typefaceBold: Typeface? = null
        var typefaceSemiBold: Typeface? = null
        var typefaceExtraBold: Typeface? = null
        var typefaceBlack: Typeface? = null
        var typefaceLight: Typeface? = null
        var toolbartitle: String? = null

        //Common Project
        const val msgToastForInternet = "The Internet connection appears to be offline."
        const val msgToastForInternetIsAvailable = "Internet is available"
        const val msgToastForInternetIsLost = "Internet is lost"
        const val playStoreUrl =
            "https://play.google.com/store/apps/details?id=com.themarkettheory.user"
        var isRateThisAppClicked = false

        // otp variables
        var otpSmsTextReceiver: String = ""
    }
}