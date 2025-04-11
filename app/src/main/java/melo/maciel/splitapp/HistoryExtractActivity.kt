package melo.maciel.splitapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.ExtractInfo
import melo.maciel.splitapp.API.HistorySpent
import melo.maciel.splitapp.API.Participant
import melo.maciel.splitapp.API.PostSpent
import melo.maciel.splitapp.Adapter.AdapterHistory
import melo.maciel.splitapp.Adapter.AdapterSpent
import melo.maciel.splitapp.databinding.ExtractHistoryLayoutBinding
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

class HistoryExtractActivity : ComponentActivity() {
    lateinit var binding: ExtractHistoryLayoutBinding
    lateinit var apiInterface: Api_Interface

    private var login: String? = null
    private var groupId: String? = null

    private var historyCall: Call<HistorySpent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ExtractHistoryLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebendo valores da intent
        groupId = intent.getStringExtra("GROUP_ID")
        login = intent.getStringExtra("login")

        binding.historyNameGroup.text = intent.getStringExtra("GROUP_NAME")

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

        // Inicializando apiInterface
        apiInterface = retrofit.create(Api_Interface::class.java)

        // Usando view binding para acessar o RecyclerView
        val recyclerViewGroups = binding.recyclerViewHistory
        recyclerViewGroups.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val listSpents: MutableList<PostSpent> = mutableListOf()

        // Configurando o Adapter
        val adapterGroup = AdapterHistory(this, listSpents)
        recyclerViewGroups.adapter = adapterGroup


        // Chamada à API para obter todos os grupos
        historyCall = apiInterface.getExtractGroup(login.toString(), groupId.toString())
        historyCall?.enqueue(object : Callback<HistorySpent> {
            override fun onResponse(call: Call<HistorySpent>, response: Response<HistorySpent>) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                if (response.isSuccessful) {
                    val groupResponse = response.body()
                    Log.d("HISTORY-MAIN-APP", "$groupResponse")
                    if (groupResponse != null) {
                        listSpents.clear()
                        listSpents.addAll(groupResponse.spents)
                        adapterGroup.notifyDataSetChanged()
                    }
                } else {
                    Log.d("HISTORY-MAIN-APP", "Erro ao buscar grupos: ${response.message()}")
                    Toast.makeText(applicationContext, "Erro ao buscar grupos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistorySpent>, t: Throwable) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                Log.d("HISTORY-MAIN-APP", "Falha na chamada da API: ${t.localizedMessage}")
                Toast.makeText(applicationContext, "Falha na chamada da API", Toast.LENGTH_SHORT).show()
            }
        })

    }
}