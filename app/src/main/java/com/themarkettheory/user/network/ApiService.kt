package com.themarkettheory.user.network

import android.util.Log
import androidx.databinding.library.BuildConfig
import com.google.gson.GsonBuilder
import com.themarkettheory.user.model.*
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.addlocation.NewAddLocationRes
import com.themarkettheory.user.newmodels.addreview.AddReviewRes
import com.themarkettheory.user.newmodels.booking.NewBookingRes
import com.themarkettheory.user.newmodels.booking.bookingdetails.NewBookingDetailsRes
import com.themarkettheory.user.newmodels.bucketcart.GetCartNewRes
import com.themarkettheory.user.newmodels.bucketcart.confirm_order.NewConfirmOrderRes
import com.themarkettheory.user.newmodels.changepassword.ChangePasswordRes
import com.themarkettheory.user.newmodels.coupons.NewOfferListRes
import com.themarkettheory.user.newmodels.coupons.apply.CheckPromoCodeRes
import com.themarkettheory.user.newmodels.getcategories.NewGetCategoriesRes
import com.themarkettheory.user.newmodels.getrecentpopularlocation.NewGetRecentPopularLocation
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.newmodels.livedeals.NewLiveDealRes
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.newmodels.login.NewResendOtpResponse
import com.themarkettheory.user.newmodels.map.NewServiceListRes
import com.themarkettheory.user.newmodels.menulist.NewMenuListRes
import com.themarkettheory.user.newmodels.myorders.MyOrdersNewRes
import com.themarkettheory.user.newmodels.mypoints.NewMyPointsRes
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingNewRes
import com.themarkettheory.user.newmodels.orderconfirmation.GetNewOrderConfirmRes
import com.themarkettheory.user.newmodels.overview.NewServiceDetailsRes
import com.themarkettheory.user.newmodels.overview.allServiceImages.AllServiceImages
import com.themarkettheory.user.newmodels.pointhistory.NewPointHistoryRes
import com.themarkettheory.user.newmodels.review.NewReviewDataRes
import com.themarkettheory.user.newmodels.searchrestaurant.SearchRestaurantRes
import com.themarkettheory.user.newmodels.totalpoints.NewTotalPointRes
import com.themarkettheory.user.newmodels.viewallrecommended.NewAllRecommendedRes
import io.reactivex.Observable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import okhttp3.ResponseBody
import retrofit2.Call


interface ApiService {

    companion object {
        // var BASE_URL = "https://dev.themarkettheory.com/api/"
        var BASE_URL = "https://themarkettheory.com/api/v1/"

        fun create(token: String?): ApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        Log.e("Authorization", "Bearer $token")
                        val builder = chain.request().newBuilder()
                        if (token != null) {
                            if(token.isNotEmpty())
                                builder.addHeader("Authorization", "Bearer $token")
                                //builder.addHeader("Content-type","application/json")
                        }
                        val newRequest = builder.build()
                        return chain.proceed(newRequest)
                    }

                }).build()

            val okHttpBuilder = OkHttpClient.Builder()
            okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            })

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BASE_URL)
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("login_via") login_via: String,
        @Field("device_type") device_type: String,
        @Field("device_token") device_token: String,
//        @Field("name") name: String?,
//        @Field("profile_pic") profile_pic: String?
    ): Observable<NewLoginResponse>

    /*
       *
     * */
    @FormUrlEncoded
    @POST("auth/login")
    fun social_login(
        @Field("login_via") login_via: String?,
        @Field("device_token") device_token: String?,
        @Field("device_type") device_type: String?,
        @Field("social_id") social_id: String?,
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("profile_pic") profile_pic: String?
    ): Observable<NewLoginResponse>

    /*@Field("facebook_token") facebook_token: String?,
    @Field("instagram_token") instagram_token: String?,
    @Field("google_token") google_token: String?,*/

    @FormUrlEncoded
    @POST("auth/check_email_mobile")
    fun check_email_mobile(
        @Field("mobile") mobile: String?,
        @Field("country_code") country_code: String?,
        @Field("email") email: String?,
        @Field("is_edit") is_edit: String?,
        @Field("login_via") login_via: String?,
        @Field("password") password: String?,
        @Field("social_id") social_id: String?,
        @Field("profile_pic") profile_pic: String?
    ): Observable<NewLoginResponse>


    @Multipart
    @POST("auth/register")
    fun register(
        @Part("email") email: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("name") name: RequestBody,
        @Part("zip") zip: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("device_type") device_type: RequestBody,
        @Part("login_via") login_via: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("password") password: RequestBody,
        @Part("social_id") social_id: RequestBody,
        @Part register: MultipartBody.Part?
    ): Observable<LoginResponse>

    @GET("profile")
    fun profile(): Observable<LoginResponse>

    @GET("profile")
    fun profileNew(): Observable<NewLoginResponse>

    @GET("account_status")
    fun accountStatus(): Observable<NewLoginResponse>

    @GET("points_history")
    fun points_history(
        @Query("get_point") get_point: String?,
        @Query("service_id") service_id: String?
    ): Observable<NewPointHistoryRes>

    @GET("get_sub_categories/{id}")
    fun get_sub_categories(@Path("id") id: String?): Observable<SubCategoriesResponse>

    @Multipart
    @POST("update_profile")
    fun update_profile(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("country_code") country_code: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("dob") dob: RequestBody,
        @Part("zip") zip: RequestBody,
        @Part profile_image: MultipartBody.Part?
    ): Observable<NewLoginResponse>

    @GET("get_events")
    fun get_events(): Observable<GetEventsResponse>

    @FormUrlEncoded
    @POST("get_event_details")
    fun get_event_details(
        @Field("event_id") event_id: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Observable<EventDetailResponse>

    @FormUrlEncoded
    @POST("change_notification_status")
    fun change_notification_status(
        @Field("notification_status") notification_status: Int?,
        @Field("remind_me") remind_me: Int?
    ): Observable<NewGeneralRes>


    @FormUrlEncoded
    @POST("updatesettings")
    fun update_notification(
        @Field("is_notification") notification_status: Int?
    ): Observable<NewGeneralRes>

    @FormUrlEncoded
    @POST("get_static_pages")
    fun get_static_pages(
        @Field("type") type: String
    ): Observable<GetStaticPageResponse>

    @FormUrlEncoded
    @POST("favourite")
    fun favourite(
        @Field("event_id") event_id: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("participate")
    fun participate(
        @Field("event_id") event_id: String?,
        @Field("subtotal") subtotal: String?,
        @Field("tax_amount") tax_amount: String?,
        @Field("tax") tax: String?,
        @Field("discount_type") discount_type: String?,
        @Field("discount") discount: String?,
        @Field("total") total: String?,
        @Field("payment_id") payment_id: String?,
        @Field("offer_id") offer_id: String?,
        @Field("ticket") ticket: JSONArray
    ): Observable<CreateEventResponse>

    @FormUrlEncoded
    @POST("ticket_details")
    fun ticket_details(
        @Field("order_id") order_id: String?
    ): Observable<GetTicketDetailResponse>

    @FormUrlEncoded
    @POST("service_list")
    fun service_list(
        @Field("category") category: String?,
        @Field("sub_category") sub_category: String?,
        @Field("type") type: String?,
        @Field("food_type") food_type: String?,
        @Field("sort") sort: String?,
        @Field("is_favourite") is_favourite: String?,
        @Field("popular_location_id") popular_location_id: String?
    ): Observable<NewServiceListRes>

    @FormUrlEncoded
    @POST("service_details")
    fun service_details(
        @Field("id") id: String?,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<NewServiceDetailsRes>

    @FormUrlEncoded
    @POST("all_service_images")
    fun all_service_images(
        @Field("id") id: String?
    ): Observable<AllServiceImages>

//    @FormUrlEncoded
//    @POST("menus")
//    fun menus(
//        @Field("id") id: String?
//    ): Observable<GetMenuResponse>

    @FormUrlEncoded
    @POST("menu_list")
    fun menus(
        @Field("id") id: String?
    ): Observable<NewMenuListRes>

    @FormUrlEncoded
    @POST("menus")
    fun retail_menus(
        @Field("id") id: String?
    ): Observable<RetailMenuResponse>


    @FormUrlEncoded
    @POST("add_review")
    fun add_review(
        @Field("service_id") service_id: Int?,
        @Field("rating[0]") rating0: Float,
        @Field("rating[1]") rating1: Float,
        @Field("rating[2]") rating2: Float,
        @Field("rating[3]") rating3: Float,
        @Field("comment") comment: String?
    ): Observable<AddReviewRes>

    @FormUrlEncoded
    @POST("review")
    fun review(
        @Field("id") service_id: String?
    ): Observable<NewReviewDataRes>

    @FormUrlEncoded
    @POST("pickup")
    fun pickup(
        @Field("service_id") service_id: String,
        @Field("subtotal") subtotal: String,
        @Field("tax_amount") tax_amount: String,
        @Field("tax") tax: String,
        @Field("discount_type") discount_type: String,
        @Field("discount") discount: String,
        @Field("total") total: String,
        @Field("payment_id") payment_id: String,
        @Field("offer_id") offer_id: String
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("favourite_services")
    fun favourite_services(
        @Field("service_id") service_id: String?
    ): Observable<NewGeneralRes>

    @GET("get_categories")
    fun get_categories(): Observable<NewGetCategoriesRes>

    @FormUrlEncoded
    @POST("get_favourite_services")
    fun get_favourite_services(
        @Field("category_id") category_id: String?
    ): Observable<GetFavoriteServicesResponse>

    @POST("mybooking")
    fun myTableBooking(): Observable<MyTableBookingNewRes>

    @FormUrlEncoded
    @POST("home")
    fun home(
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String
    ): Observable<NewHomeRes>

    @FormUrlEncoded
    @POST("auth/otp_verification")
    fun otp_verification(
        @Field("mobile") mobile: String?,
        @Field("otp") otp: String?,
        @Field("country_code") country_code: String
    ): Observable<NewLoginResponse>

    @FormUrlEncoded
    @POST("auth/resend_otp")
    fun resend_otp(
        @Field("mobile") mobile: String?,
        @Field("country_code") country_code: String
    ): Observable<NewResendOtpResponse>

    @GET("mypoints")
    fun mypoints(@Query("service_id") service_id: String?): Observable<NewMyPointsRes>

    @GET("total_points")
    fun total_points(
        @Query("filter") filter: String?,
        @Query("service_id") service_id: String?
    ): Observable<NewTotalPointRes>

    @FormUrlEncoded
    @POST("services")
    fun saloon_spa_services(
        @Field("id") id: String?,
        @Field("gender") gender: String
    ): Observable<SaloonSpaServicesResponse>

    @FormUrlEncoded
    @POST("book_saloon")
    fun book_saloon(
        @Field("service_id") service_id: String?,
        @Field("subtotal") subtotal: String?,
        @Field("tax_amount") tax_amount: String?,
        @Field("tax") tax: String?,
        @Field("discount_type") discount_type: String?,
        @Field("discount") discount: String?,
        @Field("total") total: String?,
        @Field("payment_id") payment_id: String?,
        @Field("offer_id") offer_id: String?,
        @Field("services") services: JSONArray,
        @Field("specialist_id") specialist_id: String?,
        @Field("date") date: String?,
        @Field("time") time: String?,
        @Field("total_person") total_person: String?,
        @Field("adult") adult: String?,
        @Field("child") child: String?,
        @Field("occasion_id") occasion_id: String?,
        @Field("special_instruction") special_instruction: String?
    ): Observable<BookSaloonResponse>

    @FormUrlEncoded
    @POST("book_table")
    fun book_table(
        @Field("service_id") service_id: String?,
        @Field("date") date: String?,
        @Field("time") time: String?,
        @Field("total_person") total_person: String?,
        @Field("adult") adult: String?,
        @Field("child") child: String?,
        @Field("occasion_id") occasion_id: String?,
        @Field("special_request") special_request: String?
    ): Observable<NewBookingRes>

    @FormUrlEncoded
    @POST("gym_packages")
    fun gym_packages(@Field("id") id: String?): Observable<GymPackagesResponse>

    @FormUrlEncoded
    @POST("retail_menu_detail")
    fun retail_menu_detail(@Field("id") id: String?): Observable<RetailMenuDetailResponse>

    @GET("my_orders")
    fun my_orders(): Observable<MyOrdersNewRes>

    @GET("occations")
    fun occations(): Observable<OccationsResponse>

    @FormUrlEncoded
    @POST("my_order_details")
    fun my_order_details(@Field("id") id: String?): Observable<OrderDetailResponse>

    @FormUrlEncoded
    @POST("my_order_details")
    fun my_order_details_new(@Field("id") id: String?): Observable<GetNewOrderConfirmRes>

    @FormUrlEncoded
    @POST("cancel_order")
    fun cancelOrder(
        @Field("id") id: String?,
        @Field("reason") reason: String?
    ): Observable<NewGeneralRes>

    /*@GET("view_all_event")
    fun view_all_event(): Observable<AllEventResponse>*/

    @POST("view_all_recommanded")
    fun view_all_recommanded(): Observable<NewAllRecommendedRes>

    @POST("view_all_nearest")
    fun view_all_nearest(): Observable<AllNearestResponse>

    @FormUrlEncoded
    @POST("view_all_offers")
    fun view_all_offers(@Field("service_id") service_id: String?): Observable<AllOfferResponse>

    @GET("get_cart")
    fun get_cart(): Observable<GetCartResponse>

    @GET("get_cart")
    fun get_cart(
        @Query("booking_id") booking_id: Int,
        @Query("is_redeem") is_redeem: Int,
        @Query("is_live_deal") is_live_deal: Int,
        @Query("is_dine_in") is_dine_in: Int
    ): Observable<GetCartNewRes>

    @FormUrlEncoded
    @POST("add_cart")
    fun add_cart(
        @Field("menu_id") menu_id: String?,
        @Field("qty") qty: String?,
        @Field("service_id") service_id: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("add_cart")
    fun add_cart(
        @Field("menu_id") menu_id: String?,
        @Field("qty") qty: String?,
        @Field("service_id") service_id: String?,
        @Field("is_redeem") is_redeem: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("add_cart")
    fun menu_add_cart(
        @Field("service_id") service_id: String?,
        @Field("dish_id") dish_id: String?,
        @Field("is_redeem") is_redeem: String?,
        @Field("qty") qty: String?,
        @Field("booking_id") booking_id: String?,
        @Field("is_live_deal") is_live_deal: Int?,
        @Field("is_dine_in") is_dine_in: Int?
    ): Observable<NewGeneralRes>

    @FormUrlEncoded
    @POST("offers_list")
    fun offers_list(
        @Field("id") id: String?,
        @Field("type") type: String?
    ): Observable<NewOfferListRes>

    @FormUrlEncoded
    @POST("favourite_coupon")
    fun favourite_coupon(
        @Field("offer_id") offer_id: String
    ): Observable<NewGeneralRes>

    @FormUrlEncoded
    @POST("coupons")
    fun coupons(@Field("type") type: String?): Observable<OfferListResponse>

    @FormUrlEncoded
    @POST("check_promocode")
    fun check_promocode(
        @Field("promocode") promocode: String?,
        @Field("id") id: String?,
        @Field("type") type: String?
    ): Observable<CheckPromoCodeResponse>

    @FormUrlEncoded
    @POST("check_promocode")
    fun check_promo_code_new(
        @Field("id") id: String?,
        @Field("promocode") promocode: String?,
        @Field("booking_id") booking_id: String?
    ): Observable<CheckPromoCodeRes>

    @FormUrlEncoded
    @POST("add_address")
    fun add_address(
        @Field("google_address") google_address: String?,
        @Field("house_number") house_number: String?,
        @Field("floor") floor: String?,
        @Field("tower") tower: String?,
        @Field("type") type: String?,
        @Field("is_default") is_default: String?,
        @Field("address_optional") address_optional: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("edit_address")
    fun edit_address(
        @Field("google_address") google_address: String?,
        @Field("house_number") house_number: String?,
        @Field("floor") floor: String?,
        @Field("tower") tower: String?,
        @Field("type") type: String?,
        @Field("is_default") is_default: String?,
        @Field("address_optional") address_optional: String?,
        @Field("latitude") latitude: String?,
        @Field("longitude") longitude: String?,
        @Field("id") id: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("delete_address")
    fun delete_address(@Field("id") id: String?): Observable<GeneralResponse>

    @GET("addresses")
    fun addresses(): Observable<GetAddressListResponse>

    @GET("stamps")
    fun stamps(@Query("service_id") service_id: String?): Observable<GetStampsResponse>

    @GET("auth/countries")
    fun countries(): Observable<CountryListResponse>

    @FormUrlEncoded
    @POST("live_deals")
    fun live_deals(@Field("id") id: String?): Observable<NewLiveDealRes>

    @FormUrlEncoded
    @POST("pickup")
    fun pickup(
        @Field("service_id") service_id: String?,
        @Field("subtotal") subtotal: String?,
        @Field("tax_amount") tax_amount: String?,
        @Field("tax") tax: String?,
        @Field("discount_type") discount_type: String?,
        @Field("discount") discount: String?,
        @Field("total") total: String?,
        @Field("payment_id") payment_id: String?,
        @Field("offer_id") offer_id: String?,
        @Field("orders") orders: JSONArray?,
        @Field("points") points: String?,
        @Field("special_instruction") special_instruction: String?,
        @Field("pickup_time") pickup_time: String?
    ): Observable<PickupResponse>

    @FormUrlEncoded
    @POST("check_service")
    fun check_service(
        @Field("service_id") service_id: String?,
        @Field("subtotal") subtotal: String?,
        @Field("tax_amount") tax_amount: String?,
        @Field("tax") tax: String?,
        @Field("discount_type") discount_type: String?,
        @Field("discount") discount: String?,
        @Field("total") total: String?,
        @Field("payment_id") payment_id: String?,
        @Field("offer_id") offer_id: String?,
        @Field("orders") orders: JSONArray?,
        @Field("points") points: String?,
        @Field("special_instruction") special_instruction: String?,
        @Field("pickup_time") pickup_time: String?
    ): Observable<PickupResponse>

    @FormUrlEncoded
    @POST("pickup")
    fun pickup(
        @Field("service_id") service_id: String?,
        @Field("items") items: JSONArray?,
        @Field("subtotal") subtotal: String?,
        @Field("total") total: String?,
        @Field("offer_id") offer_id: String?,
        @Field("payment_id") payment_id: String?,
        @Field("points") points: String?,
        @Field("special_instruction") special_instruction: String?,
        @Field("pickup_time") pickup_time: String?,
        @Field("tax") tax: String?,
        @Field("booking_id") booking_id: String?
    ): Observable<NewConfirmOrderRes>

    // new add order api integration

    @FormUrlEncoded
    @POST("pickup")
    fun addOrder(
        @Field("service_id") service_id: String?,
        @Field("items") items: String?,
        @Field("subtotal") subtotal: String?,
        @Field("total") total: String?,
        @Field("offer_id") offer_id: String?,
        @Field("payment_id") payment_id: String?,
        @Field("points") points: String?,
        @Field("special_instruction") special_instruction: String?,
        @Field("pickup_time") pickup_time: String?,
        @Field("tax") tax: String?,
        @Field("booking_id") booking_id: String?,
    ): Observable<GetCartNewRes>

    @FormUrlEncoded
    @POST("pickup")
    fun reatilpickup(
        @Field("service_id") service_id: String?,
        @Field("subtotal") subtotal: String?,
        @Field("tax_amount") tax_amount: String?,
        @Field("tax") tax: String?,
        @Field("discount_type") discount_type: String?,
        @Field("discount") discount: String?,
        @Field("total") total: String?,
        @Field("payment_id") payment_id: String?,
        @Field("offer_id") offer_id: String?,
        @Field("orders") orders: JSONArray?,
        @Field("points") points: String?,
        @Field("special_instruction") special_instruction: String?,
        @Field("sizeId") sizeId: String?
    ): Observable<PickupResponse>

    @FormUrlEncoded
    @POST("specialists")
    fun specialists(
        @Field("id") id: String?
    ): Observable<SpecialistListResponse>

    @FormUrlEncoded
    @POST("total_points_details")
    fun total_points_details(
        @Field("id") id: String?
    ): Observable<PointsDetailResponse>

    @FormUrlEncoded
    @POST("mybooking_details")
    fun mybooking_details(
        @Field("id") id: String?
    ): Observable<NewBookingDetailsRes>

    @FormUrlEncoded
    @POST("cancel_table")
    fun cancel_table(
        @Field("booking_id") booking_id: String?
    ): Observable<NewGeneralRes>

    @GET("my_events")
    fun my_events(): Observable<MyEventsResponse>

    @GET("get_places")
    fun get_places(): Observable<GetPlacesResponse>

    @FormUrlEncoded
    @POST("gym_enquiry")
    fun gym_enquiry(
        @Field("package_id") package_id: String?,
        @Field("service_id") service_id: String?,
        @Field("person") person: String?,
        @Field("special_instruction") special_instruction: String?
    ): Observable<GeneralResponse>

    @FormUrlEncoded
    @POST("total_points_details_new")
    fun total_points_details_new(
        @Field("service_id") service_id: String?
    ): Observable<TotalPointsDetailResponse>

    @FormUrlEncoded
    @POST("search_places")
    fun search_places(
        @Field("search_str") service_id: String?
    ): Observable<AllNearestResponse>

    @FormUrlEncoded
    @POST("allsearch")
    fun all_search(
        @Field("search") search: String
    ): Observable<SearchRestaurantRes>

    @FormUrlEncoded
    @POST("switch_to_business")
    fun switch_to_business(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("mobile") mobile: String?,
        @Field("details") details: String?
    ): Observable<NewGeneralRes>

    @FormUrlEncoded
    @POST("report_service")
    fun report_service(
        @Field("id") id: String?,
        @Field("description") description: String?,
        @Field("type") type: String?,
        @Field("question") question: String?
    ): Observable<NewGeneralRes>

    @FormUrlEncoded
    @POST("change_password")
    fun change_password(
        @Field("new_password") new_password: String?,
        @Field("password") password: String?
    ): Observable<ChangePasswordRes>

    @FormUrlEncoded
    @POST("activate_coupon")
    fun activate_coupon(
        @Field("offer_id") offer_id: String?
    ): Observable<GeneralResponse>

    @POST("delete_account")
    fun delete_account(): Observable<DeleterResponse>

    @GET("get_activated_coupons")
    fun get_activated_coupons(): Observable<GetActivatedCouponsResponse>

    @FormUrlEncoded
    @POST("auth/forget_password")
    fun forget_password(
        @Field("email") email: String?
    ): Observable<NewGeneralRes>

    @GET("get_notification")
    fun get_notifications(): Observable<NotificationListResponse>
    // fun get_notifications(@Path(value = "name") name: String?): Observable<NotificationListResponse>

    @GET("get_notification")
    fun get_notifications_offer(@Query("filter") coupon: String?): Observable<NotificationOfferListResponse>

    @GET("get_recent_popular_location")
    fun get_recent_popular_location(): Observable<NewGetRecentPopularLocation>

    @FormUrlEncoded
    @POST("add_location")
    fun add_location(
        @Field("address") address: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("place_id") place_id: String,
        @Field("type") type: String
    ): Observable<NewAddLocationRes>

    @FormUrlEncoded
    @POST("special_instruction")
    fun add_special_instruction(
        @Field("booking_id") booking_id: String,
        @Field("special_instruction") special_instruction: String,
        @Field("is_redeem") is_redeem: String,
        @Field("is_live_deal") is_live_deal: Int
    ): Observable<GetCartNewRes>

    @FormUrlEncoded
    @POST("book_type")
    fun schedule_pickup_type(
        @Field("type") type: String,
        @Field("schedule_time") schedule_time: String,
        @Field("is_redeem") is_redeem: Int,
        @Field("is_live_deal") is_live_deal: Int,
        @Field("is_dine_in") is_dine_in: Int
    ): Observable<GetCartNewRes>

    @FormUrlEncoded
    @POST("check_restaurant_time")
    fun checkRestaurantTime(
        @Field("id") id: Int?,
        @Field("time") time: String?
    ): Observable<NewGeneralRes>

    // check_restaurant_time , pickup
}