package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowVendorListBinding
import com.themarkettheory.user.newmodels.map.Restaurant
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity

class VendorListAdapter(val context: Context, var data: ArrayList<Restaurant>) :
    RecyclerView.Adapter<VendorListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0

    //    var spaImages = intArrayOf(R.drawable.ic_spa1,R.drawable.ic_spa2,R.drawable.ic_spa3,R.drawable.ic_spa4)
//    var salonImages = intArrayOf(R.drawable.ic_salon1,R.drawable.ic_salon2,R.drawable.ic_salon3,R.drawable.ic_salon4)
//    var images = intArrayOf(R.drawable.ic_resaturant1,R.drawable.ic_restaurant2,R.drawable.ic_restaurant3,R.drawable.ic_restaurant4)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowVendorListBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_vendor_list, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        if (data.isNullOrEmpty()) {
            return 0
        } else {
            return data.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data!![getItemViewType(position)]

        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)
        holder.binding!!.rating.rating = data[getItemViewType(position)].rating!!.toFloat()
        holder.binding!!.rl.setOnClickListener {
            context.startActivity(
                Intent(context, VendorDetailActivity::class.java)
                    .putExtra("category", data[getItemViewType(position)].categoryId.toString())
                    .putExtra("serviceId", data[getItemViewType(position)].id.toString())
                    .putExtra("vendorTitle", data[getItemViewType(position)].title)
            )
        }

        if (data[position].isOpen == 1) {
            holder.binding!!.tvType.setText(context.getString(R.string.open))
            holder.binding!!.tvType.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    context,
                    R.color.malachite
                )
            );
        } else {
            holder.binding!!.tvType.setText(context.getString(R.string.close))
            holder.binding!!.tvType.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    context,
                    R.color.cinnabar
                )
            );
        }
        /*if(category == Constants.SALON){
            holder.binding!!.iv.setImageResource(salonImages[position % 4])
            holder.binding!!.tvVendorName.setText("Dax Hair & Beauty")
        }else if(category == Constants.SPA){
            holder.binding!!.iv.setImageResource(spaImages[position % 4])
            holder.binding!!.tvVendorName.setText("Orchid the Spa Salon")
        }else{
            holder.binding!!.iv.setImageResource(images[position])
        }*/
    }

    fun updataData(tempData: ArrayList<Restaurant>) {
        data = ArrayList()
        data.addAll(tempData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowVendorListBinding? = null

        init {
            binding = DataBindingUtil.bind<RowVendorListBinding>(itemView)

        }
    }

}