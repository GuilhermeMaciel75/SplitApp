package melo.maciel.splitapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import melo.maciel.splitapp.API.PostSpent
import melo.maciel.splitapp.R

class AdapterHistory (private val context: Context, val spent: MutableList<PostSpent>) : RecyclerView.Adapter<AdapterHistory.HistoryViewHolder>() {
    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeSpent = itemView.findViewById<TextView>(R.id.history_spent_type)
        val descriptionSpent = itemView.findViewById<TextView>(R.id.history_description_spent)
        val paidBy = itemView.findViewById<TextView>(R.id.history_paid_by)
        val totalValue = itemView.findViewById<TextView>(R.id.history_total_value)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val item_history = LayoutInflater.from(context).inflate(R.layout.history_info_layout, parent, false)
        val holder = HistoryViewHolder(item_history)

        return holder

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.typeSpent.text = spent[position].type_spent
        holder.descriptionSpent.text = spent[position].spent_description
        holder.paidBy.text = spent[position].login_user
        holder.totalValue.text = spent[position].spent_value.toString()

    }

    override fun getItemCount(): Int = spent.size
}
