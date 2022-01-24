package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowGymListBinding
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity

class GymListAdapter(val context: Context) :
    RecyclerView.Adapter<GymListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0

    //    var images = intArrayOf(R.drawable.ic_resaturant1,R.drawable.ic_restaurant2,R.drawable.ic_restaurant3,R.drawable.ic_restaurant4)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowGymListBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_gym_list, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]

        holder.binding!!.ll.setOnClickListener {
            context.startActivity(
                Intent(context, VendorDetailActivity::class.java)
                    .putExtra("category", Constants.GYM)
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowGymListBinding? = null

        init {
            binding = DataBindingUtil.bind<RowGymListBinding>(itemView)

        }
    }

}