package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowSearchPopularBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.model.PopularPlace
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity

class PopularSearchListAdapter(val context: Context, val data: List<PopularPlace>) :
    RecyclerView.Adapter<PopularSearchListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowSearchPopularBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_search_popular, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]

        holder.binding!!.ll.setOnClickListener {
            Config.isMenuFragmentComingFrom = ""
            context.startActivity(
                Intent(context, VendorDetailActivity::class.java)
                    .putExtra("vendorTitle", data[position].title)
                    .putExtra("serviceId", data[position].id.toString())
                    .putExtra("category", data[position].category_id.toString())
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowSearchPopularBinding? = null

        init {
            binding = DataBindingUtil.bind<RowSearchPopularBinding>(itemView)

        }
    }

}