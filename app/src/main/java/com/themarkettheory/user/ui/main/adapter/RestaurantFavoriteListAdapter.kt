package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowNewFavoriteListBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.map.Restaurant

class RestaurantFavoriteListAdapter(
    private val listener: ListClickListener,
    private val favoriteListener: ListClickListener
) : RecyclerView.Adapter<RestaurantFavoriteListAdapter.Holder>() {
    //General
    private var restaurantFavoriteList = ArrayList<Restaurant>()
    private var restaurantFavoriteListFilter = ArrayList<Restaurant>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_new_favorite_list,
            FrameLayout(parent.context),
            false
        )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val rowData = restaurantFavoriteList[position]
            //Restaurant Image
            if (rowData.image.isNullOrEmpty()) {
                holder.rowNewFavoriteListBinding.ivRestaurantFavoriteListImage.setImageDrawable(
                    ContextCompat.getDrawable(Config.mainActivity!!, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(rowData.image!!.trim())
                    .error(R.drawable.ic_camera)
                    .into(
                        holder.rowNewFavoriteListBinding.ivRestaurantFavoriteListImage
                    )
            }
            //Restaurant Title
            holder.rowNewFavoriteListBinding.tvRestaurantFavoriteListName.text =
                rowData.title!!.trim()
            //Restaurant Address
            holder.rowNewFavoriteListBinding.tvRestaurantFavoriteListAddress.text =
                rowData.address!!.trim()
            //Ratings
            holder.rowNewFavoriteListBinding.RestaurantFavoriteListRating.rating =
                rowData.rating!!.trim().toFloat()
            //Rating Count
            holder.rowNewFavoriteListBinding.tvRestaurantFavoriteListRateText.text =
                "(${rowData.totalRating!!.trim()})"


            //View Click Listener
            holder.view.setOnClickListener {
                listener.onClickListener(holder.view, position, rowData)
            }

            //Favorite Click Listner
            holder.rowNewFavoriteListBinding.ivRestaurantFavoriteListHeart.setOnClickListener {
                favoriteListener.onClickListener(holder.view, position, rowData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return restaurantFavoriteList.size
    }

    fun updateRestaurantFavoriteList(position: Int) {
        try {
            if (restaurantFavoriteList.isNotEmpty()) {
                restaurantFavoriteList.removeAt(position)
                notifyItemRemoved(position)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setRestaurantFavoriteList(alFavorite: ArrayList<Restaurant>) {
        try {
            if (alFavorite.isNotEmpty()) {
                this.restaurantFavoriteList.clear()
                this.restaurantFavoriteListFilter.clear()
                this.restaurantFavoriteList.addAll(alFavorite)
                this.restaurantFavoriteListFilter.addAll(alFavorite)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val rowNewFavoriteListBinding = DataBindingUtil.bind<RowNewFavoriteListBinding>(itemView)!!
    }
}