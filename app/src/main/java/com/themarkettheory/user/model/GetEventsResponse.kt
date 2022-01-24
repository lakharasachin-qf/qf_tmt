package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetEventsResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: EventData? = null
}

class EventData {
    /*@SerializedName("upcoming_event")
    @Expose
    var upcomingEvent: List<UpcomingEvent>? = null

    @SerializedName("popular_event")
    @Expose
    var popularEvent: List<UpcomingEvent>? = null*/

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null
}

class PopularEvent {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("tags")
    @Expose
    var tags: String? = null

    @SerializedName("start")
    @Expose
    var start: String? = null

    @SerializedName("end")
    @Expose
    var end: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null

    /*@SerializedName("prices")
    @Expose
    var prices: List<Price>? = null*/
}



