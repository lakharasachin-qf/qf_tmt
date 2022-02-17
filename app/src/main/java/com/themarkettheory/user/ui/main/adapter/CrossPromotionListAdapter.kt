package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowCrossPromotionPartnersBinding
import com.themarkettheory.user.newmodels.overview.CrossPromotion

class CrossPromotionListAdapter(val context: Context, val list: List<CrossPromotion>) :
    RecyclerView.Adapter<CrossPromotionListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowCrossPromotionPartnersBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.row_cross_promotion_partners,
                p0,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        Glide.with(context).load(list[position].image).into(holder.binding!!.iv)
        holder.binding!!.tvName.setText(list[position].title)

        /*holder.binding!!.iv.setOnClickListener {
            context.startActivity(
                Intent(context, VendorDetailActivity::class.java)
                    .putExtra("serviceId", list[position].promotionServiceId.toString())
                    .putExtra("category", list[position].categoryId.toString())
                    .putExtra("vendorTitle", list[position].title)
            )
        }*/
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowCrossPromotionPartnersBinding? = null

        init {
            binding = DataBindingUtil.bind<RowCrossPromotionPartnersBinding>(itemView)

        }
    }

}