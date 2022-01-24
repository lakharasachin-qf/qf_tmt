package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.OfferDatum
import java.util.*


class PromoCodeListAdapter(
    val context: Activity,
    val data: List<OfferDatum>,
    val listener: (OfferDatum) -> Unit
) :
    RecyclerView.Adapter<PromoCodeListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    val display: Display = context.windowManager.defaultDisplay
    val width = display.width
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowPromoCodeBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_promo_code, p0, false)

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

        holder.binding!!.btnApply.setOnClickListener {
            listener(data[getItemViewType(position)])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowPromoCodeBinding? = null

        init {
            binding = DataBindingUtil.bind<RowPromoCodeBinding>(itemView)

        }
    }

}