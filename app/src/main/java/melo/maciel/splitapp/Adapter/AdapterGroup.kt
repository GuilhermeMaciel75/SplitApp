package melo.maciel.splitapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import melo.maciel.splitapp.API.GroupInfo
import melo.maciel.splitapp.R

class AdapterGroup(private val context: Context, private val groups:MutableList<GroupInfo>): RecyclerView.Adapter<AdapterGroup.GroupViewHolder>() {
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_group = itemView.findViewById<TextView>(R.id.id_group)
        val name_group = itemView.findViewById<TextView>(R.id.name_group)
        val group_description = itemView.findViewById<TextView>(R.id.description_group)
        val partipants_group = itemView.findViewById<TextView>(R.id.partipants_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val item_group = LayoutInflater.from(context).inflate(R.layout.group_info_layout, parent, false)
        val holder = GroupViewHolder(item_group)
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.name_group.text = groups[position].group_name
        holder.id_group.text = groups[position].id
        holder.group_description.text = groups[position].group_description
        holder.partipants_group.text = groups[position].group_number_participants.toString()
    }

    override fun getItemCount(): Int = groups.size
}