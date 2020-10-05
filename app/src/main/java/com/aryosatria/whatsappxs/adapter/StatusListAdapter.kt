package com.aryosatria.whatsappxs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aryosatria.whatsappxs.R
import com.aryosatria.whatsappxs.fragments.StatusListFragment
import com.aryosatria.whatsappxs.listener.StatusItemClickListener
import com.aryosatria.whatsappxs.util.StatusListElement
import com.aryosatria.whatsappxs.util.populateImage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_status.*

class StatusListAdapter(val statusList: ArrayList<StatusListElement>):
        RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>() {

    private var clickListener: StatusItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StatusListViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_status,parent,false)
    )

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: StatusListViewHolder, position: Int) {
        holder.bindItem(statusList[position],clickListener)
    }

    fun onRefresh() {
        statusList.clear()
        notifyDataSetChanged()
    }

    fun addElement(element: StatusListElement) {
        statusList.add(element)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: StatusItemClickListener) {
        clickListener = listener
        notifyDataSetChanged()
    }

    class StatusListViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView),LayoutContainer {

        fun bindItem(statusElement: StatusListElement, listener: StatusItemClickListener?) {
            populateImage(
                img_status_photo.context,
                statusElement.userUrl,
                img_status_photo,
                R.drawable.ic_user
            )
            txt_status_name.text = statusElement.userName
            txt_status_time.text = statusElement.statusTime
            itemView.setOnClickListener {
                listener?.onItemClicked(statusElement)
            }
        }
    }
}