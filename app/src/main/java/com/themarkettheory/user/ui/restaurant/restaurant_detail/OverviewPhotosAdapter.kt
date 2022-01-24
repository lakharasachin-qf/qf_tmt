package com.themarkettheory.user.ui.restaurant.restaurant_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowOverviewSeeAllPhotosBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.overview.Image

class OverviewPhotosAdapter(private val listener: ListClickListener) :
    RecyclerView.Adapter<OverviewPhotosAdapter.Holder>() {

    private val imageList = ArrayList<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_overview_see_all_photos, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val data = imageList[position]
            if (data.image.isNullOrEmpty()) {
                holder.binding!!.ivOverViewSeeAllPhotos.setImageDrawable(
                    ContextCompat.getDrawable(Config.mainActivity!!, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(data.image)
                    .error(R.drawable.ic_camera)
                    .into(holder.binding?.ivOverViewSeeAllPhotos)
            }
            holder.binding?.ivOverViewSeeAllPhotos?.setOnClickListener {
                listener.onClickListener(holder.itemView, position, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun addImages(imageList: ArrayList<Image>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<RowOverviewSeeAllPhotosBinding>(view)
    }
}