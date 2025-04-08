package melo.maciel.splitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.GroupInfo
import melo.maciel.splitapp.API.Participant
import melo.maciel.splitapp.Adapter.AdapterGroup
import melo.maciel.splitapp.Adapter.AdapterSpent
import melo.maciel.splitapp.databinding.GroupLayoutBinding
import melo.maciel.splitapp.databinding.RegisterSpentLayoutBinding

class RegisterSpentActivity: ComponentActivity() {
    lateinit var binding: RegisterSpentLayoutBinding
    lateinit var apiInterface: Api_Interface

    private var login: String? = null
    private var groupId: String? = null
    private var participants: ArrayList<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = RegisterSpentLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebendo valores da intent
        groupId = intent.getStringExtra("GROUP_ID")
        login = intent.getStringExtra("login")
        participants = intent.getStringArrayListExtra("GROUP_PARTICIPANTS")

        // Usando view binding para acessar o RecyclerView
        val recyclerViewGroups = binding.recyclerViewSpent
        recyclerViewGroups.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val listParticipants: MutableList<Participant> = mutableListOf()

        if (!participants.isNullOrEmpty()) {
            val percentage = 100.0 / participants!!.size

            for (participantName in participants!!) {
                val participant = Participant(name = participantName, percentage = percentage)
                listParticipants.add(participant)
            }
        }

        // Configurando o Adapter
        val adapterGroup = AdapterSpent(this, listParticipants)
        recyclerViewGroups.adapter = adapterGroup
    }
}