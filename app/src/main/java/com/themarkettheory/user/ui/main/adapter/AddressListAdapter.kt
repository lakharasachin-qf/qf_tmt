package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.*
import com.themarkettheory.user.model.AddressList


class AddressListAdapter(
    val context: Context,
    val data: List<AddressList>,
    val listener: (AddressList, String) -> Unit
) :
    RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    private var currentPosition: Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowListOfAddressBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_list_of_address, p0, false)

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
        if (data[getItemViewType(position)].type == 0) {
            holder.binding!!.tvAddressType.setText("Home")
        } else if (data[getItemViewType(position)].type == 1) {
            holder.binding!!.tvAddressType.setText("Work")
        } else if (data[getItemViewType(position)].type == 2) {
            holder.binding!!.tvAddressType.setText("Office")
        } else if (data[getItemViewType(position)].type == 3) {
            holder.binding!!.tvAddressType.setText("Other")
        }

        if (data[getItemViewType(position)].isDefault == 1) {
            holder.binding!!.tvSetAsDefault.visibility = View.VISIBLE
        } else {
            holder.binding!!.tvSetAsDefault.visibility = View.GONE
        }

        holder.binding!!.llAddress.setOnClickListener {
            listener(data[getItemViewType(position)], "select")
        }

        holder.binding!!.txtAddress.setText(
            data[getItemViewType(position)].houseNumber + " ," + data[getItemViewType(
                position
            )].tower
        )

        holder.binding!!.ivMenu.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(context, R.style.PopupMenu)

            val popup = PopupMenu(wrapper, holder.binding!!.ivMenu)
            popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu())
            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.edit) {
                    listener(data[getItemViewType(position)], "edit")
                }
                if (it.itemId == R.id.delete) {
                    listener(data[getItemViewType(position)], "delete")
                }
                true
            }
            popup.show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowListOfAddressBinding? = null

        init {
            binding = DataBindingUtil.bind<RowListOfAddressBinding>(itemView)

        }
    }

}