package com.themarkettheory.user.ui.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowNumberOfGuestsBinding
import java.util.*
import kotlin.collections.ArrayList


class GuestListAdapterChild(
    val context: Activity,
    val count: Int,
    val isAdult: Boolean,
    val listener: () -> Unit
) :
    RecyclerView.Adapter<GuestListAdapterChild.ViewHolder>() {
    private var currentPosition: Int = -1
    var flags = arrayOfNulls<Boolean>(count)
    var firstTime: Boolean = false

    init {
        Arrays.fill(flags, false)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowNumberOfGuestsBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_number_of_guests, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.obj = list[position]
        holder.binding!!.tvGuest.setText((position + 1).toString())

        if (flags[position]!!) {
            holder.binding!!.tvGuest.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_selected)
            holder.binding!!.tvGuest.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding!!.tvGuest.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_unselected)
            holder.binding!!.tvGuest.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.slate_gray
                )
            )
        }

        holder.binding!!.tvGuest.setOnClickListener {
            currentPosition = position
            var flag = flags[position]
            Arrays.fill(flags, false)
            flags[position] = flag
            flags[position] = !flags[position]!!
            notifyDataSetChanged()
            listener()
        }
    }

    @JvmName("getFlags1")
    fun getFlags(): Array<Boolean?> {
        return flags
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowNumberOfGuestsBinding? = null

        init {
            binding = DataBindingUtil.bind<RowNumberOfGuestsBinding>(itemView)

        }
    }

}