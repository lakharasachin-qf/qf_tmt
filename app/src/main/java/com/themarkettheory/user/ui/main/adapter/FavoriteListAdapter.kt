package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowFavoritesBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.model.FavoriteServicesDatum
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity

class FavoriteListAdapter(
    val context: Context,
    val data: ArrayList<FavoriteServicesDatum>,
    val listener: (FavoriteServicesDatum) -> Unit
) :
    RecyclerView.Adapter<FavoriteListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowFavoritesBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_favorites, p0, false)

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
        holder.binding!!.rating.rating = data[getItemViewType(position)].rating!!.toFloat()

        Glide.with(context).load(data[getItemViewType(position)].image)
            .error(R.drawable.ic_camera)
            .into(holder.binding!!.ivVendor)
        holder.binding!!.rlFavorite.setOnClickListener {
            listener(data[getItemViewType(position)])
            data.removeAt(getItemViewType(position))
            notifyItemRemoved(getItemViewType(position))
        }

        holder.binding!!.rl.setOnClickListener {
            Config.isMenuFragmentComingFrom = ""
            context.startActivity(
                Intent(context, VendorDetailActivity::class.java)
                    .putExtra("category", data[getItemViewType(position)].category_id.toString())
                    .putExtra("serviceId", data[getItemViewType(position)].id.toString())
                    .putExtra("vendorTitle", data[getItemViewType(position)].title)
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowFavoritesBinding? = null

        init {
            binding = DataBindingUtil.bind<RowFavoritesBinding>(itemView)

        }
    }

}