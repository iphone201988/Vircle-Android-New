package com.tech.vircle.ui.home.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.tech.vircle.R
import com.tech.vircle.data.api.Constants
import com.tech.vircle.data.model.Chat
import com.tech.vircle.utils.BindingUtils
import com.zerobranch.layout.SwipeLayout


class CustomSwipeChatAdapter(
    private val itemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<CustomSwipeChatAdapter.ViewHolder>() {

    private var itemList: MutableList<Chat?> = ArrayList()
    private var currentlyOpenSwipeLayout: SwipeLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, itemClickListener, position)
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileIcon: ShapeableImageView = view.findViewById(R.id.ivPerson)
        val tvName: AppCompatTextView = view.findViewById(R.id.tvName)
        val tvTime: AppCompatTextView = view.findViewById(R.id.tvTime)
        val tvMessage: AppCompatTextView = view.findViewById(R.id.tvMessage)
        val tvUnReadMessage: AppCompatTextView = view.findViewById(R.id.tvUnReadMessage)
        val constantSwap: ConstraintLayout = view.findViewById(R.id.constantSwap)
        val favConstantLayout: ConstraintLayout = view.findViewById(R.id.favConstantLayout)
        val swipeLayout: SwipeLayout = view.findViewById(R.id.swipeLayout)

        fun bind(item: Chat?, listener: OnItemClickListener, position: Int) {
            item?.let {

                tvName.text = it.contactId?.name
                tvMessage.text = it.lastMessage?.message

                if (it.unreadCount!! > 0) {
                    tvUnReadMessage.visibility = View.VISIBLE
                    tvUnReadMessage.text = it.unreadCount.toString()
                } else {
                    tvUnReadMessage.visibility = View.GONE
                }
                val utcTime = it.lastMessage?.createdAt
                val localTime = BindingUtils.formatUtcToLocalTime(utcTime)
                tvTime.text = localTime

                Glide.with(profileIcon.context)
                    .load(Constants.MEDIA_BASE_URL + it.contactId?.aiAvatar)
                    .placeholder(R.drawable.profilephoto).into(profileIcon)

                constantSwap.setOnClickListener { view ->
                    listener.onItemClick(view, it, position)
                    swipeLayout.close()
                }


                favConstantLayout.setOnClickListener { view ->
                    listener.onItemClick(view, it, position)
                }



                swipeLayout.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {

                    override fun onOpen(direction: Int, isContinuous: Boolean) {
                        if (direction == SwipeLayout.LEFT) {
                            closePreviouslyOpenSwipeLayout()
                            tvTime.visibility = View.INVISIBLE
                            currentlyOpenSwipeLayout = swipeLayout
                        }

                    }

                    override fun onClose() {
                        if (currentlyOpenSwipeLayout == swipeLayout) {
                            tvTime.visibility = View.VISIBLE
                            currentlyOpenSwipeLayout = null
                        }
                    }
                })
            }
        }

        private fun closePreviouslyOpenSwipeLayout() {
            currentlyOpenSwipeLayout?.let { openLayout ->
                // restore previous item's time
                val prevTimeText = openLayout.findViewById<AppCompatTextView>(R.id.tvTime)
                prevTimeText?.visibility = View.VISIBLE
                openLayout.close()
            }
        }

    }


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

    fun getList(): MutableList<Chat?> {
        return itemList
    }

    fun setList(newDataList: List<Chat?>?) {
        itemList.clear()
        if (newDataList != null) {
            itemList.addAll(newDataList)
        }
        notifyDataSetChanged()
    }


    fun addToList(list: List<Chat?>?) {
        val newDataList: List<Chat?>? = list
        if (newDataList != null) {
            val initialSize = itemList.size
            itemList.addAll(newDataList)
            notifyItemRangeInserted(
                initialSize, newDataList.size
            ) // Notify only the newly inserted range
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, item: Chat, position: Int)
    }
}
