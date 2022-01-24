package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowTotalPointsNewBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.totalpoints.NewTotalPointData

class NewTotalPointAdapter(
    val context: Activity,
    private val listener: ListClickListener,
    private val imageListener: ListClickListener,
    private val clPointListener: ListClickListener
) :
    RecyclerView.Adapter<NewTotalPointAdapter.ViewHolder>() {

    private var totalPointsDataList = ArrayList<NewTotalPointData>()

    private var totalPointsDataListFilter = ArrayList<NewTotalPointData>()

    var colors = arrayOf("#2fb8ee", "#ffbe00", "#19b254", "#8230ff", "#da532f", "#2fb8ee")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_total_points_new, FrameLayout(parent.context), false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val totalPointNewData = totalPointsDataList[position]
        val currency = totalPointNewData.serviceDetails!!.currency!!.toString().trim()
        if (position != 0) {
            holder.view.translationY =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -3.0f * position.toFloat(),
                    context.resources.displayMetrics
                )
        }
        // setting up the restaurant image
        if (totalPointNewData.serviceDetails!!.image.isNullOrEmpty()) {
            holder.bindingRowTotalPoints.ivRowTotalPointsImage.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_camera
                )
            )
        } else {
            Picasso.get().load(totalPointNewData.serviceDetails!!.image!!)
                .error(R.drawable.ic_camera)
                .into(holder.bindingRowTotalPoints.ivRowTotalPointsImage)
        }

        //setting up the restarurant name
        holder.bindingRowTotalPoints.tvTotalPointRestarurantName.text =
            totalPointNewData.serviceDetails!!.title!!.toString().trim()

        // setting up the total bill amount
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.bindingRowTotalPoints.tvTotalPointsBillAmount.text =
                Html.fromHtml(
                    "Restaurant Total Bill: <b>${currency} ${totalPointNewData.total!!}</b>",
                    Html.FROM_HTML_MODE_COMPACT
                )
        } else {
            @Suppress("DEPRECATION")
            holder.bindingRowTotalPoints.tvTotalPointsBillAmount.text =
                Html.fromHtml("Restaurant Total Bill: <b>${currency} ${totalPointNewData.total!!}</b>")
        }


        // setting up the individual points
        holder.bindingRowTotalPoints.tvTotalPoints.text =
            "${totalPointNewData.points!!}"


        // onclick listener on point
        holder.bindingRowTotalPoints.clTotalPoints.setOnClickListener {
            listener.onClickListener(holder.view, position, totalPointNewData)
        }

        // onclick listener on image view
        holder.bindingRowTotalPoints.ivRowTotalPointsImage.setOnClickListener {
            imageListener.onClickListener(holder.view, position, totalPointNewData)
        }

        // setting up the onlick click listner on middle constraint layout
        holder.bindingRowTotalPoints.clMiddle.setOnClickListener {
            clPointListener.onClickListener(holder.view, position, totalPointNewData)
        }
        // setting the background
        holder.bindingRowTotalPoints.clTotalPointMainLayout.background.setColorFilter(
            Color.parseColor(colors[position]),
            android.graphics.PorterDuff.Mode.SRC_ATOP
        )
    }

    override fun getItemCount(): Int {
        return totalPointsDataList.size
    }

    fun addTotalPointList(alPoints: ArrayList<NewTotalPointData>) {
        try {
            if (alPoints.isNotEmpty()) {
                this.totalPointsDataList.clear()
                this.totalPointsDataListFilter.clear()
                this.totalPointsDataList = alPoints
                this.totalPointsDataListFilter.addAll(alPoints)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun filter(searchText: String) {
        try {
            totalPointsDataList.clear()
            if (searchText.isEmpty()) {
                totalPointsDataList.addAll(totalPointsDataListFilter)
            } else {
                for (i in totalPointsDataListFilter.indices) {
                    if (totalPointsDataListFilter[i].serviceDetails!!.title!!.lowercase()
                            .contains(searchText)
                    ) {
                        totalPointsDataList.add(totalPointsDataListFilter[i])
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val bindingRowTotalPoints: RowTotalPointsNewBinding =
            DataBindingUtil.bind(itemView)!!

    }
}