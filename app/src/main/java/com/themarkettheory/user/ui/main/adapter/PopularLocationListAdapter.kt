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
import com.themarkettheory.user.databinding.RowPopularLocationListBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.PopularLocation

class PopularLocationListAdapter(
    val context: Activity,
    val data: List<PopularLocation>,
    val listener: ListClickListener
) : RecyclerView.Adapter<PopularLocationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RowPopularLocationListBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.row_popular_location_list,
                parent,
                false
            )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageURL = data[position].image!!
        holder.binding!!.tvPopularLocationListName.text = data[position].address!!.trim()
        if (imageURL.isNullOrEmpty()) {
            holder.binding!!.ivPopularLocationListImage.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_camera)
            )
        } else {
            Picasso.get().load(imageURL!!)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.ivPopularLocationListImage)
        }

        holder.binding!!.rlPopularLocationsList.setOnClickListener {
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
        internal var binding: RowPopularLocationListBinding? = null
        val view = itemView

        init {
            binding = DataBindingUtil.bind<RowPopularLocationListBinding>(itemView)
        }
    }

}