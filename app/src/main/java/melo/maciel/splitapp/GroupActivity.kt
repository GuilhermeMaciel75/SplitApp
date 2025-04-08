package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.databinding.GroupLayoutBinding
import okhttp3.OkHttpClient
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
        val groupName = intent.getStringExtra("GROUP_NAME")
        val groupDescription = intent.getStringExtra("GROUP_DESCRIPTION")
        val groupParticipants = intent.getIntExtra("GROUP_PARTICIPANTS", 0)
        login = intent.getStringExtra("login")
        participants = intent.getStringArrayListExtra("GROUP_PARTICIPANTS")


        binding.idGroup.text = groupId
        binding.nameGroup.text = groupName
        binding.descriptionGroup.text = groupDescription
        binding.partipantsGroup.text = groupParticipants.toString()

        Log.d("GROUP-MAIN-APP", "login: ${login}")
        Log.d("GROUP-MAIN-APP", "groupId: ${groupId}")

        binding.btnAddSpent.setOnClickListener(this)
        binding.btnViewExtract.setOnClickListener(this)
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