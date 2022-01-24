package com.themarkettheory.user.ui.dialog.dialogPopupList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowPopupTypeBinding
import com.themarkettheory.user.interfaces.ListClickListener

class AdapterDialogPopupList(private val listener: ListClickListener) :
    RecyclerView.Adapter<AdapterDialogPopupList.ViewHolder>() {

    private var dataItems = ArrayList<clsPopupDialogList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RowPopupTypeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_popup_type,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val strDataItems = dataItems[position]
        holder.binding!!.tvPopupDialogTitle.text = strDataItems.strTitle
        holder.itemView.setOnClickListener {
            listener.onClickListener(holder.itemView, position, strDataItems)
        }
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }

    fun addPopupDataItems(data: ArrayList<clsPopupDialogList>) {
        this.dataItems.clear()
        this.dataItems.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: RowPopupTypeBinding? = null

        init {
            binding = DataBindingUtil.bind(view)
        }
    }
}