package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPackageBinding
import com.themarkettheory.user.model.GymList


class PackageListAdapter(
    val context: Activity,
    val data: List<GymList>,
    val listener: (GymList) -> Unit
) :
    RecyclerView.Adapter<PackageListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowPackageBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_package, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = data[position]
        Glide.with(context).load(data[getItemViewType(position)].image)
            .into(holder.binding!!.ivOffer)

        if (data[getItemViewType(position)].discountedPrice.isNullOrEmpty()) {
            holder.binding!!.rlPrice.visibility = View.GONE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].price + "/" + data[getItemViewType(position)].duration
            )
        } else {
            holder.binding!!.rlPrice.visibility = View.VISIBLE
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].discountedPrice + "/" + data[getItemViewType(position)].duration
            )
            holder.binding!!.tvAmount.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].price + "/" + data[getItemViewType(position)].duration
            )
        }
        holder.binding!!.rl.setOnClickListener {
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPackageBinding? = null

        init {
            binding = DataBindingUtil.bind<RowPackageBinding>(itemView)

        }
    }

}