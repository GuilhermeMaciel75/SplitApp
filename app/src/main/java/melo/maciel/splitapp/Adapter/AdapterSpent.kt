package melo.maciel.splitapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import melo.maciel.splitapp.API.Participant
import melo.maciel.splitapp.R

class AdapterSpent(private val context: Context, private val participants:MutableList<Participant>): RecyclerView.Adapter<AdapterSpent.SpentViewHolder>() {

    inner class SpentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particpant_name = itemView.findViewById<TextView>(R.id.spent_partipant_name)
        val partipant_value = itemView.findViewById<EditText>(R.id.spent_partipant_value)
        val partipant_percentege = itemView.findViewById<EditText>(R.id.spent_partipant_percentenge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpentViewHolder {
        val item_group = LayoutInflater.from(context).inflate(R.layout.bill_spent_layout, parent, false)
        val holder = SpentViewHolder(item_group)

        return  holder
    }

    override fun onBindViewHolder(holder: SpentViewHolder, position: Int) {
        holder.particpant_name.text = participants[position].name
        holder.partipant_value.setText(String.format("%.2f", participants[position].value))
        holder.partipant_percentege.setText(String.format("%.2f", participants[position].percentage))
    }

    override fun getItemCount(): Int = participants.size

}