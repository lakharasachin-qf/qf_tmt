package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowRecommendedAllBinding
import com.themarkettheory.user.newmodels.home.Restaurant
import java.util.*

class AllRecommendedListAdapter(
    val context: Activity,
    val data: List<Restaurant>,
    val listener: (Restaurant) -> Unit
) :
    RecyclerView.Adapter<AllRecommendedListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    val display: Display = context.windowManager.defaultDisplay
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowRecommendedAllBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_recommended_all, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[getItemViewType(position)]

        holder.binding!!.rating.rating = data[getItemViewType(position)].totalRating!!.toFloat()
        Glide.with(context).load(data[getItemViewType(position)].image)
            .error(R.drawable.ic_camera)
            .into(holder.binding!!.iv)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowRecommendedAllBinding? = null

        init {
            binding = DataBindingUtil.bind<RowRecommendedAllBinding>(itemView)

        }
    }

}