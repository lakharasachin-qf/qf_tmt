package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowAmenitiesListBinding
import com.themarkettheory.user.newmodels.overview.Amenity

class AmenitiesListAdapter(val context: Context, val data: List<Amenity>) :
    RecyclerView.Adapter<AmenitiesListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0

    //    var images = intArrayOf(R.drawable.ic_amenities_gym,R.drawable.ic_amenities_yoga,R.drawable.ic_amenities_zumba,R.drawable.ic_amenities_cycling,
//        R.drawable.ic_amenities_boxing,R.drawable.ic_amenities_martial_arts,R.drawable.ic_amenities_aerobics)
    var titles =
        arrayOf("Gym", "Yoga Classes", "Zumba", "Cycling", "Boxing", "Martial arts", "Aerobics")

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAmenitiesListBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_amenities_list, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(data[position].image).into(holder.binding!!.iv)
        holder.binding!!.tvService.setText(data[position].name)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAmenitiesListBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAmenitiesListBinding>(itemView)

        }
    }

}