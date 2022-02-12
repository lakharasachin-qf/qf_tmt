package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowReviewNewBinding
import com.themarkettheory.user.newmodels.review.ReviewData
import javax.inject.Inject

class ReviewListAdapter(val context: Context) :
    RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    private var totalRating = 0.0

    @Inject
    lateinit var gson: Gson
    private var totalReivewLIst = ArrayList<ReviewData>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_review_new, FrameLayout(p0.context), false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // return data.size
        return totalReivewLIst.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val totalMyReviewData = totalReivewLIst[position]
            gson = GsonBuilder().serializeNulls().create()
            Log.e("totalMyReviewAdapter", gson.toJson(totalMyReviewData))


            // setting up the images
            if (totalMyReviewData.user!!.profilePic.isNullOrEmpty()) {
                holder.newReviewBinding.ivRowReviewUserImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(totalMyReviewData.user!!.profilePic)
                    .error(R.drawable.ic_profile)
                    .into(holder.newReviewBinding.ivRowReviewUserImage)
            }

            // setting up the review time
            holder.newReviewBinding.tvRowReviewTime.text = totalMyReviewData.reviewTime!!

            // setting up the name
            holder.newReviewBinding.tvRowReviewUserName.text = totalMyReviewData.user!!.name!!
            totalRating = 0.0
            totalRating += totalMyReviewData.rating!!.toDouble()
            // setting up the review Star
            holder.newReviewBinding.tvRowReviewPointText.text =
                totalRating.toString()!!

            // setting up the description

            holder.newReviewBinding.tvRowReviewDescription.text = totalMyReviewData.comment!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addTotalPointList(alPoints: ArrayList<ReviewData>) {
        try {
            if (alPoints.isNotEmpty()) {
                this.totalReivewLIst.clear()
                this.totalReivewLIst.addAll(alPoints)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val newReviewBinding: RowReviewNewBinding = DataBindingUtil.bind(itemView)!!
    }

}