package melo.maciel.splitapp.Adapter

import android.content.Context
import android.graphics.Color
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
        return ExtractViewHolder(item_group)
    }

    override fun onBindViewHolder(holder: ExtractViewHolder, position: Int) {
        val participant = extractParticipants[position]
        holder.particpant_name.text = participant.participantName
        val balance = participant.participantBalance

        // Formatação do valor com duas casas decimais
        holder.partipant_value.text = "R$ " + String.format("%.2f", balance)

        // Verifica se o saldo é negativo ou positivo e muda a cor do texto
        if (balance < 0) {
            holder.partipant_value.setTextColor(Color.RED)  // Cor vermelha para valores negativos
        } else {
            holder.partipant_value.setTextColor(Color.GREEN)  // Cor verde para valores positivos
        }
    }

    override fun getItemCount(): Int = extractParticipants.size
}
