package melo.maciel.splitapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import melo.maciel.splitapp.API.ParticipantInfo
import melo.maciel.splitapp.R

class AdapterExtract(private val context: Context, val extractParticipants: MutableList<ParticipantInfo>): RecyclerView.Adapter<AdapterExtract.ExtractViewHolder>() {

    inner class ExtractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particpant_name = itemView.findViewById<TextView>(R.id.extract_spent_name)
        val partipant_value = itemView.findViewById<TextView>(R.id.extract_spent_value)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtractViewHolder {
        val item_group = LayoutInflater.from(context).inflate(R.layout.extract_spent_layout, parent, false)
        val holder = ExtractViewHolder(item_group)

        return holder
    }

    override fun onBindViewHolder(holder: ExtractViewHolder, position: Int) {
        val participant = extractParticipants[position]
        holder.particpant_name.text = participant.participantName
        holder.partipant_value.setText(String.format("%.2f", participant.participantBalance))

    }

    override fun getItemCount(): Int = extractParticipants.size
}