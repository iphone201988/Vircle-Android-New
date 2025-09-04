package com.tech.vircle.ui.home.chat.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tech.vircle.data.model.Message
import com.tech.vircle.databinding.RvChatDetailsItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatDetailsAdapter(
) : RecyclerView.Adapter<ChatDetailsAdapter.ChatDetailsViewHolder>() {

    private var itemList: MutableList<Message?> = ArrayList()

    inner class ChatDetailsViewHolder(val binding: RvChatDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDetailsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvChatDetailsItemBinding.inflate(inflater, parent, false)
        return ChatDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatDetailsViewHolder, position: Int) {
        val item = itemList[position]
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

//        val displayDate = if (item?.date == todayDate) {
//            "Today"
//        } else {
//            item?.date
//        }


       // holder.binding.tvMessageDate.text = displayDate

//        val adapter = holder.binding.rvChatDetails.adapter as? MessageAdapter
//        if (adapter == null) {
//            holder.binding.rvChatDetails.adapter =
//                MessageAdapter(ArrayList(item?.messages?.reversed() ?: emptyList()))
//        } else {
//            adapter.messageList.clear()
//            adapter.messageList.addAll(item?.messages ?: emptyList())
//            adapter.notifyDataSetChanged()
//        }


    }

    override fun getItemCount() = itemList.size

    fun clearList() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun getList(): MutableList<Message?> {
        return itemList
    }


    fun getMessageAt(position: Int): Message? {
        return itemList[position]
    }

    fun setList(newDataList: List<Message?>?) {
        itemList.clear()
        if (newDataList != null) {
            itemList.addAll(newDataList)
        }
        notifyDataSetChanged()
    }


    fun addToList(list: List<Message?>?) {
        val newDataList: List<Message?>? = list
        if (newDataList != null) {
            itemList.addAll(0, newDataList)
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
