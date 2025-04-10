package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.ExtractInfo
import melo.maciel.splitapp.API.GroupResponse
import melo.maciel.splitapp.API.ParticipantInfo
import melo.maciel.splitapp.Adapter.AdapterExtract
import melo.maciel.splitapp.Adapter.AdapterSpent
import melo.maciel.splitapp.databinding.GroupLayoutBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class GroupActivity: ComponentActivity(), View.OnClickListener  {
    lateinit var binding: GroupLayoutBinding
    lateinit var apiInterface: Api_Interface

    private var login: String? = null
    private var groupId: String? = null
    private var participants: ArrayList<String>? = null

    private var groupsCall: Call<ExtractInfo>? = null
    private var groupInfoCall: Call<GroupResponse>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = GroupLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cria conexão com a API

        // Criar um cliente OkHttp que ignora o SSL
        val trustAllCertificates: TrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        }

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustAllCertificates), java.security.SecureRandom())

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates as X509TrustManager)
            .hostnameVerifier { hostname, session -> true }  // Ignora verificação de hostname
            .build()

        // Inicializando Retrofit com o cliente OkHttp personalizado
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")  // Substitua pela URL base da sua API
            .client(client)  // Usando o cliente com verificação SSL desabilitada
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit.create(Api_Interface::class.java)
        groupId = intent.getStringExtra("GROUP_ID")
        login = intent.getStringExtra("login")

        /*
        groupId = intent.getStringExtra("GROUP_ID")
        val groupName = intent.getStringExtra("GROUP_NAME")
        val groupDescription = intent.getStringExtra("GROUP_DESCRIPTION")
        val groupParticipants = intent.getIntExtra("GROUP_PARTICIPANTS_QTD", 0)
        login = intent.getStringExtra("login")
        participants = intent.getStringArrayListExtra("GROUP_PARTICIPANTS")


        binding.idGroup.text = groupId
        binding.nameGroup.text = groupName
        binding.descriptionGroup.text = groupDescription
        binding.partipantsNumberGroup.text = groupParticipants.toString()

        Log.d("GROUP-MAIN-APP", "login: ${login}")
        Log.d("GROUP-MAIN-APP", "groupId: ${groupId}")

         */

        binding.btnAddSpent.setOnClickListener(this)
        binding.btnViewExtract.setOnClickListener(this)

        // Usando view binding para acessar o RecyclerView
        val recyclerViewGroups = binding.recyclerViewExtract
        recyclerViewGroups.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val listParticipants: MutableList<ParticipantInfo> = mutableListOf()

        // Configurando o Adapter
        val adapterGroup = AdapterExtract(this, listParticipants)
        recyclerViewGroups.adapter = adapterGroup

        // Chamada à API para as infos do grupo

        // Chamada à API para obter todos os grupos
        groupInfoCall = apiInterface.getInfoGroup(login.toString(), groupId.toString())
        groupInfoCall?.enqueue(object : Callback<GroupResponse> {
            override fun onResponse(call: Call<GroupResponse>, response: Response<GroupResponse>) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                if (response.isSuccessful) {
                    val groupResponse = response.body()
                    Log.d("GROUP-MAIN-APP", "$groupResponse")
                    if (groupResponse != null) {
                        login = intent.getStringExtra("login")
                        participants = groupResponse.groups[0].group_participants as ArrayList<String>


                        binding.idGroup.text = groupResponse.groups[0].id
                        binding.nameGroup.text = groupResponse.groups[0].group_name
                        binding.descriptionGroup.text = groupResponse.groups[0].group_description
                        binding.partipantsNumberGroup.text = groupResponse.groups[0].group_number_participants.toString()
                    }
                } else {
                    Log.d("GROUP-MAIN-APP", "Erro ao buscar infos do grupos: ${response.message()}")
                    Toast.makeText(applicationContext, "Erro ao buscar infos do grupos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                Log.d("GROUP-MAIN-APP", "Falha na chamada da API das info group: ${t.localizedMessage}")
                Toast.makeText(applicationContext, "Falha na chamada da API das info group", Toast.LENGTH_SHORT).show()
            }
        })

        // Chamada à API para obter todos os grupos
        groupsCall = apiInterface.getExtract(login.toString(), groupId.toString())
        groupsCall?.enqueue(object : Callback<ExtractInfo> {
            override fun onResponse(call: Call<ExtractInfo>, response: Response<ExtractInfo>) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                if (response.isSuccessful) {
                    val groupResponse = response.body()
                    Log.d("GROUP-MAIN-APP", "$groupResponse")
                    if (groupResponse != null) {
                        listParticipants.clear()
                        listParticipants.addAll(groupResponse.participants)
                        binding.userSpent.text = groupResponse.total_spent.toString()
                        adapterGroup.notifyDataSetChanged()
                    }
                } else {
                    Log.d("GROUP-MAIN-APP", "Erro ao buscar grupos: ${response.message()}")
                    Toast.makeText(applicationContext, "Erro ao buscar grupos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExtractInfo>, t: Throwable) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                Log.d("GROUP-MAIN-APP", "Falha na chamada da API: ${t.localizedMessage}")
                Toast.makeText(applicationContext, "Falha na chamada da API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v:View?) {

        when (v?.id) {

            R.id.btn_add_spent -> {
                try {
                    val intent = Intent(this, RegisterSpentActivity::class.java)
                    intent.putExtra("login", login)
                    intent.putExtra("GROUP_ID", groupId)
                    intent.putStringArrayListExtra("GROUP_PARTICIPANTS", participants)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.d("GROUP-MAIN-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                }
            }

            R.id.btn_view_Extract -> {

            }
        }
    }
}