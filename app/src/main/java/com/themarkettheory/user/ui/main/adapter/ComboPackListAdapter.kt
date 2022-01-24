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
import com.themarkettheory.user.databinding.RowComboPackBinding
import com.themarkettheory.user.model.SaloonSpaList


class ComboPackListAdapter(
    val context: Activity,
    val data: List<SaloonSpaList>,
    val listener: (SaloonSpaList) -> Unit
) :
    RecyclerView.Adapter<ComboPackListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowComboPackBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_combo_pack, p0, false)

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
        Glide.with(context).load(data[getItemViewType(position)].image).into(holder.binding!!.iv)

        if (data[getItemViewType(position)].discountedPrice.isNullOrEmpty()) {
            holder.binding!!.rlPrice.visibility = View.GONE
        } else {
            holder.binding!!.rlPrice.visibility = View.VISIBLE
            holder.binding!!.tvPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].mrpPrice
            )
            holder.binding!!.tvFinalPrice.setText(
                data[getItemViewType(position)].currency + data[getItemViewType(
                    position
                )].discountedPrice
            )
        }
        holder.binding!!.btnBookNow.setOnClickListener {
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowComboPackBinding? = null

        init {
            binding = DataBindingUtil.bind<RowComboPackBinding>(itemView)

        }
    }

}