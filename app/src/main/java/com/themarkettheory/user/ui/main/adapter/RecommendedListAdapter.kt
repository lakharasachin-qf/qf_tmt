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
import com.themarkettheory.user.databinding.RowRecommendedBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.FavoriteListener
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.Restaurant
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import java.util.*

class RecommendedListAdapter(
    val context: Context,
    private val rowListener: ListClickListener,
    val listener: (Restaurant) -> Unit
) : RecyclerView.Adapter<RecommendedListAdapter.ViewHolder>() {

    private val data = ArrayList<Restaurant>()
    private lateinit var favoriteListener: FavoriteListener

    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    fun addRecommendedList(recommendedList: ArrayList<Restaurant>) {
        this.data.clear()
        this.data.addAll(recommendedList)
        this.notifyDataSetChanged()
    }

    fun addFavoriteListener(listener: FavoriteListener) {
        this.favoriteListener = listener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowRecommendedBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_recommended, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data[position].isRecommanded == 1) {


            holder.binding?.obj = data[position]

            holder.binding!!.rating.rating = data[position].rating!!.toFloat()
            if (data[getItemViewType(position)].isFavourite == 0) {
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
            } else {
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
            }

            holder.binding!!.ivFavorite.setOnClickListener {
                if (PubFun.isInternetConnection(context)) {
                    if (data[getItemViewType(position)].isFavourite == 1) {
                        holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
                    } else {
                        holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
                    }

                    favoriteListener.onMarkFavorite(
                        data[position].title!!,
                        data[position].isFavourite!!.xor(1)
                    )

                    data[getItemViewType(position)].isFavourite =
                        data[getItemViewType(position)].isFavourite!!.xor(1)
                    notifyDataSetChanged()
                }
                listener(data[getItemViewType(position)])
            }

            Glide.with(context).load(data[getItemViewType(position)].image)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.iv)

            holder.binding!!.ll.setOnClickListener {
                rowListener.onClickListener(holder.binding!!.ll, position, data[position])
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowRecommendedBinding? = null

        init {
            binding = DataBindingUtil.bind<RowRecommendedBinding>(itemView)

        }
    }

}