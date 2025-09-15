package com.tech.vircle.ui.home.chat.adpter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tech.vircle.R
import com.tech.vircle.data.model.Message
import com.tech.vircle.utils.BindingUtils

class ChatAdapter(
    var recyclerView: RecyclerView
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var itemList: MutableList<Message?> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSenderName: AppCompatTextView = view.findViewById(R.id.tvSenderName)
        val tvSenderTime: AppCompatTextView = view.findViewById(R.id.tvSenderTime)
        val tvReceiverName: AppCompatTextView = view.findViewById(R.id.tvReceiverName)
        val tvReceiverTime: AppCompatTextView = view.findViewById(R.id.tvReceiverTime)
        val clReceiver: ConstraintLayout = view.findViewById(R.id.clReceiver)
        val clSender: ConstraintLayout = view.findViewById(R.id.clSender)
        val tvSHowDate: AppCompatTextView = view.findViewById(R.id.tvSHowDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_chat_item_simple, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position] ?: return
        val dateLabel = BindingUtils.getDateLabel(item.createdAt)
        if (position == 0) {
            holder.tvSHowDate.visibility = View.VISIBLE
            holder.tvSHowDate.text = dateLabel
        } else {
            val prevItem = itemList[position - 1]
            val prevDateLabel = BindingUtils.getDateLabel(prevItem?.createdAt)
            if (!dateLabel.isNullOrEmpty() && dateLabel == prevDateLabel) {
                // Same date → hide header
                holder.tvSHowDate.visibility = View.GONE
            } else {
                holder.tvSHowDate.visibility = View.VISIBLE
                holder.tvSHowDate.text = dateLabel
            }
        }

        // Message binding
        if (item.type == "user") {
            holder.clSender.visibility = View.VISIBLE
            holder.clReceiver.visibility = View.GONE
            holder.tvSenderName.text = item.message
            holder.tvSenderTime.text = BindingUtils.formatUtcToLocalTime(item.createdAt)
        } else {
            holder.clSender.visibility = View.GONE
            holder.clReceiver.visibility = View.VISIBLE
            holder.tvReceiverName.text = item.message
            holder.tvReceiverTime.text = BindingUtils.formatUtcToLocalTime(item.createdAt)
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
        notifyItemRangeInserted(0, newDataList?.size ?: 0)
    }


    fun addToListSendMessage(list: List<Message?>?) {
        val newDataList: List<Message?>? = list
        if (newDataList != null) {
            val initialSize = itemList.size
            itemList.addAll(newDataList)
            notifyItemRangeInserted(
                initialSize, newDataList.size
            )
            val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager
            linearLayoutManager?.let {
                val firstVisible = it.findFirstVisibleItemPosition()
                val firstView = it.findViewByPosition(firstVisible)
                val offset = firstView?.top ?: 0
                it.scrollToPositionWithOffset(firstVisible + newDataList.size, offset)
            }

        }
    }


}
