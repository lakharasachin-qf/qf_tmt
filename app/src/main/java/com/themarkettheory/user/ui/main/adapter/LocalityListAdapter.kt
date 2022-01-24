package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowLocalityBinding
import com.themarkettheory.user.model.LocalityModel
import java.util.*

class LocalityListAdapter(
    val context: Context,
    val list: ArrayList<LocalityModel>,
    val listClickListener: ListClickListener
) :
    RecyclerView.Adapter<LocalityListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowLocalityBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_locality, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.obj = list[position]
        holder.view.setOnClickListener {
            listClickListener.onClickListener(holder.itemView, position, list[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowLocalityBinding? = null
        val view = itemView

        init {
            binding = DataBindingUtil.bind<RowLocalityBinding>(itemView)

        }
    }


}