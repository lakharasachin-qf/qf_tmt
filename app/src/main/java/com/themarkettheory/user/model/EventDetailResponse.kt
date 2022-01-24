package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class EventDetailResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: EventDetailData? = null
}

class EventDetailData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("currency_str")
    @Expose
    var currency_str: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("tags")
    @Expose
    var tags: String? = null

    @SerializedName("tax")
    @Expose
    var tax: List<Tax>? = null

    @SerializedName("start")
    @Expose
    var start: String? = null

    @SerializedName("end")
    @Expose
    var end: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("is_liked")
    @Expose
    var is_liked: Int? = 0

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

    @SerializedName("distance")
    @Expose
    var distance: String? = null

    @SerializedName("event_participant_base_url")
    @Expose
    var eventParticipantBaseUrl: String? = null

    @SerializedName("event_base_url")
    @Expose
    var eventBaseUrl: String? = null

    @SerializedName("favourite")
    @Expose
    var favourite: Int? = null

    /*@SerializedName("prices")
    @Expose
    var prices: List<Price>? = null*/

    @SerializedName("participants")
    @Expose
    var participants: List<Participant>? = null
}

class Participant {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("event_id")
    @Expose
    var eventId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}