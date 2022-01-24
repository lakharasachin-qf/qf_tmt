package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.RetailMenuList
import com.themarkettheory.user.ui.main.activity.AttireDetailActivity
import kotlinx.android.synthetic.main.fragment_attire_menu.*

class AttireCategoryListAdapter(val context: Activity, val data: List<RetailMenuList>) :
    RecyclerView.Adapter<AttireCategoryListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowAttireCategoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_attire_category, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        holder.binding!!.tvCategory.setText(data[getItemViewType(position)].name)
        if (data[getItemViewType(position)].menu.isNullOrEmpty()) {
            holder.binding!!.ll.visibility = View.GONE
        } else {
            holder.binding!!.ll.visibility = View.VISIBLE
        }
        val attireMenuListAdapter =
            AttireMenuListAdapter(context, data[getItemViewType(position)].menu!!) {
                context.startActivity(
                    Intent(context, AttireDetailActivity::class.java)
                        .putExtra("id", it.id.toString())
                        .putExtra("serviceId", data[getItemViewType(position)].serviceId.toString())
                )
            }
        holder.binding!!.rvAttireMenu.adapter = attireMenuListAdapter
        holder.binding!!.rvAttireMenu.layoutManager = GridLayoutManager(context, 2)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowAttireCategoryBinding? = null

        init {
            binding = DataBindingUtil.bind<RowAttireCategoryBinding>(itemView)

        }
    }

}