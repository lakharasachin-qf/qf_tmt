package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPopularLocationBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.PopularLocation

class PopularLocationNamesAdapter(
    val context: Activity,
    val data: List<PopularLocation>,
    val listener : ListClickListener
    ) : RecyclerView.Adapter<PopularLocationNamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RowPopularLocationBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_popular_location, parent, false)
        return ViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding!!.objPopularLocationName = data[position]

        /*if (position % 2 == 0) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = Utils.dpToPx(16.0f, context.resources.displayMetrics)
            params.rightMargin = Utils.dpToPx(8.0f, context.resources.displayMetrics)
            params.topMargin = Utils.dpToPx(20.0f, context.resources.displayMetrics)
            holder.binding?.rlPopularLocations!!.layoutParams = params
        } else {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = Utils.dpToPx(8.0f, context.resources.displayMetrics)
            params.topMargin = Utils.dpToPx(20.0f, context.resources.displayMetrics)
            holder.binding?.rlPopularLocations!!.layoutParams = params
        }*/

        val imageURL = data[position].image!!
        /*Glide.with(context).load(imageURL).into(holder.binding!!.ivPopularLocationImage)*/
        if (imageURL.isEmpty()) {
            holder.binding!!.ivPopularLocationImage.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_camera)
            )
        } else {
            Picasso.get().load(imageURL)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.ivPopularLocationImage)
        }

        holder.binding!!.rlPopularLocations.setOnClickListener {
            listener.onClickListener(holder.view, position, data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPopularLocationBinding? = null
        val view = itemView

        init {
            binding = DataBindingUtil.bind<RowPopularLocationBinding>(itemView)
        }
    }

}