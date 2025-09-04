package com.tech.vircle.ui.home.chat.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tech.vircle.data.model.Message
import com.tech.vircle.databinding.RvReceiverItemBinding
import com.tech.vircle.databinding.RvSenderItemBinding
import com.tech.vircle.utils.BindingUtils

class MessageAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: MutableList<Message?> = ArrayList()

    companion object {
        private const val TYPE_RECEIVER = 0
        private const val TYPE_SENDER = 1
    }

    inner class ReceiverViewHolder(val binding: RvReceiverItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class SenderViewHolder(val binding: RvSenderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position]?.type == "user") TYPE_SENDER else TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_RECEIVER) {
            ReceiverViewHolder(RvReceiverItemBinding.inflate(inflater, parent, false))
        } else {
            SenderViewHolder(RvSenderItemBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = itemList[position]
        when (holder) {
            is ReceiverViewHolder -> {
                holder.binding.tvName.text = message?.message
                val utcTime = message?.createdAt
                val localTime = BindingUtils.formatUtcToLocalTime(utcTime)
                holder.binding.tvTime.text = localTime
            }

            is SenderViewHolder -> {
                holder.binding.tvName.text = message?.message
                val utcTime = message?.createdAt
                val localTime = BindingUtils.formatUtcToLocalTime(utcTime)
                holder.binding.tvTime.text = localTime
            }
        }
    }

    override fun getItemCount() = itemList.size


    fun removeItemAt(position: Int) {
        if (position >= 0 && position < itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position, itemList.size
            ) // Update the range of items that have changed
        }
    }


    fun clearList() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun getList(): MutableList<Message?> {
        return itemList
    }

    fun setList(newDataList: List<Message?>?) {
        itemList.clear()
        if (newDataList != null) {
            itemList.addAll(newDataList)
        }
        notifyDataSetChanged()
    }


    fun addToListMessage(list: List<Message?>?) {
        val newDataList: List<Message?>? = list
        if (newDataList != null) {
            itemList.addAll(0, newDataList.reversed())
        }
        notifyDataSetChanged()
    }


    fun addToListSendMessage(list: List<Message?>?) {
        val newDataList: List<Message?>? = list
        if (newDataList != null) {
            val initialSize = itemList.size
            itemList.addAll(newDataList)
            notifyItemRangeInserted(
                initialSize, newDataList.size
            )
        }
    }

}
