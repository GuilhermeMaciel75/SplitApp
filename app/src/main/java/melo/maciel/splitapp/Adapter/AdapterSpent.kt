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

class AdapterSpent(private val context: Context, val participants: MutableList<Participant>) : RecyclerView.Adapter<AdapterSpent.SpentViewHolder>() {

    inner class SpentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particpant_name = itemView.findViewById<TextView>(R.id.spent_partipant_name)
        val partipant_value = itemView.findViewById<EditText>(R.id.spent_partipant_value)
        //val partipant_percentege = itemView.findViewById<EditText>(R.id.spent_partipant_percentenge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpentViewHolder {
        val item_group = LayoutInflater.from(context).inflate(R.layout.bill_spent_layout, parent, false)
        val holder = SpentViewHolder(item_group)

        return holder
    }

    override fun onBindViewHolder(holder: SpentViewHolder, position: Int) {
        val participant = participants[position]
        holder.particpant_name.text = participant.name
        holder.partipant_value.setText(String.format("%.2f", participant.value))
        //holder.partipant_percentege.setText(String.format("%.2f", participant.percentage))

        // Atualizar o valor do participante quando o valor for alterado no EditText
        holder.partipant_value.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val updatedValue = holder.partipant_value.text.toString().toDoubleOrNull() ?: 0.0
                participants[position] = participant.copy(value = updatedValue)
            }
        }

        /*
        holder.partipant_percentege.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val updatedPercentage = holder.partipant_percentege.text.toString().toDoubleOrNull() ?: 0.0
                participants[position] = participant.copy(percentage = updatedPercentage)
            }
        }*/
    }

    override fun getItemCount(): Int = participants.size
}
