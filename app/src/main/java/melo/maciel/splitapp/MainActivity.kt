package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.GetGroup
import melo.maciel.splitapp.API.GroupInfo
import melo.maciel.splitapp.API.GroupLogin
import melo.maciel.splitapp.API.GroupResponse
import melo.maciel.splitapp.Adapter.AdapterGroup
import melo.maciel.splitapp.databinding.MainLayoutBinding
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

class MainActivity : ComponentActivity(), View.OnClickListener {
    lateinit var binding: MainLayoutBinding
    lateinit var apiInterface: Api_Interface
    private var groupsCall: Call<GroupResponse>? = null

    private var login: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login = intent.getStringExtra("login")

        if (login != null) {
            Log.d("MAIN-APP", "Login recebido: $login")
        } else {
            login = "Null"
            Log.d("MAIN-APP", "Nenhum login foi passado")
        }

        binding.btnCriaGrupo.setOnClickListener(this)
        binding.btnEntraGrupo.setOnClickListener(this)


        // Usando view binding para acessar o RecyclerView
        val recyclerViewGroups = binding.recyclerViewGroups
        recyclerViewGroups.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Configurando Adapter
        val listGroups: MutableList<GroupInfo> = mutableListOf()
        val adapterGroup = AdapterGroup(this, listGroups, login!!)
        recyclerViewGroups.adapter = adapterGroup

        // Configurando Retrofit e API Interface (mesma lógica usada na LoginActivity)
        val trustAllCertificates: TrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        }

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(trustAllCertificates), java.security.SecureRandom())

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates as X509TrustManager)
            .hostnameVerifier { hostname, session -> true }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000") // URL base da sua API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit.create(Api_Interface::class.java)

        // Chamada à API para obter todos os grupos
        groupsCall = apiInterface.getUSerGroups(login.toString())
        groupsCall?.enqueue(object : Callback<GroupResponse> {
            override fun onResponse(call: Call<GroupResponse>, response: Response<GroupResponse>) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                if (response.isSuccessful) {
                    val groupResponse = response.body()
                    Log.d("MainActivity", "$groupResponse")
                    if (groupResponse != null) {
                        listGroups.clear()
                        listGroups.addAll(groupResponse.groups)
                        adapterGroup.notifyDataSetChanged()
                    }
                } else {
                    Log.d("MainActivity", "Erro ao buscar grupos: ${response.message()}")
                    Toast.makeText(applicationContext, "Erro ao buscar grupos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                // Verifica se a Activity ainda está ativa
                if (isFinishing || isDestroyed) return

                Log.d("MainActivity", "Falha na chamada da API: ${t.localizedMessage}")
                Toast.makeText(applicationContext, "Falha na chamada da API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cria_grupo -> {
                try {
                    val intent = Intent(this, CreateGroupActivity::class.java)
                    intent.putExtra("login", login)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.d("MAIN-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                }
            }
            R.id.btn_entra_grupo -> {
                try {
                    val intent = Intent(this, JoinGroupActivity::class.java)
                    intent.putExtra("login", login)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.d("MAIN-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onDestroy() {
        groupsCall?.cancel() // Cancela a chamada pendente para evitar callbacks após a Activity ser destruída
        super.onDestroy()
    }
}
