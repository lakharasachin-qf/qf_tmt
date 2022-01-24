package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowNearestBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.Nearby
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity


class NearestListAdapter(
    val context: Activity,
    private val listener: ListClickListener,
    val data: List<Nearby>
) :
    RecyclerView.Adapter<NearestListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width

    //    var images = intArrayOf(R.drawable.ic_nearest1,R.drawable.ic_nearest2,R.drawable.icnearest3,R.drawable.icnearest4)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowNearestBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_nearest, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

//        holder.binding!!.iv.setImageResource(images[position])
        /*if (position % 2 == 0) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = Utils.dpToPx(16.0f, context.resources.displayMetrics)
            params.rightMargin = Utils.dpToPx(8.0f, context.resources.displayMetrics)
            params.topMargin = Utils.dpToPx(20.0f, context.resources.displayMetrics)
            holder.binding?.ll!!.layoutParams = params
        } else {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = Utils.dpToPx(8.0f, context.resources.displayMetrics)
            params.topMargin = Utils.dpToPx(20.0f, context.resources.displayMetrics)
            holder.binding?.ll!!.layoutParams = params
        }*/

        /*Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)*/

        if (data[position].image.isNullOrEmpty()) {
            holder.binding!!.iv.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_camera
                )
            )
        } else {
            Picasso.get().load(data[position].image)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.iv)
        }

        holder.binding!!.ll.setOnClickListener {
            listener.onClickListener(holder.binding!!.ll, position, data[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNearestBinding? = null

        init {
            binding = DataBindingUtil.bind<RowNearestBinding>(itemView)

        }
    }

}