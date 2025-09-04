package com.tech.vircle.ui.home.settings.profile


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.tech.vircle.R
import com.tech.vircle.data.model.ProfileModelClass

class SettingsAdapter(
    private val list: List<ProfileModelClass>,
    private val callback: OnItemClickListener
) : RecyclerView.Adapter<SettingsAdapter.ProfileViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(position: Int, item: ProfileModelClass)
        fun onSwitchChanged(position: Int, item: ProfileModelClass, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_profile_list_tem, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvHeader: AppCompatTextView = itemView.findViewById(R.id.tvHeader)
        private val ivClock: AppCompatImageView = itemView.findViewById(R.id.ivClock)
        private val switchVisibility: SwitchCompat = itemView.findViewById(R.id.switchVisibility)

        fun bind(item: ProfileModelClass, pos: Int) {
            tvHeader.text = item.name

            when (item.type) {
                1 -> {
                    ivClock.visibility = View.VISIBLE
                    switchVisibility.visibility = View.GONE
                }
                2 -> {
                    ivClock.visibility = View.GONE
                    switchVisibility.visibility = View.VISIBLE
                    switchVisibility.post {
                        switchVisibility.isChecked = item.isChecked
                    }
                }
                else -> {
                    ivClock.visibility = View.GONE
                    switchVisibility.visibility = View.GONE
                }
            }

            // 🔹 Handle row click
            itemView.setOnClickListener {
                callback.onItemClicked(pos, item)
            }

            // 🔹 Handle switch toggle
            switchVisibility.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                callback.onSwitchChanged(pos, item, isChecked)
            }
        }
    }
}
