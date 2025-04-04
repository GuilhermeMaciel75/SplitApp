package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import melo.maciel.splitapp.API.API_DATA
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.GroupLogin
import melo.maciel.splitapp.databinding.JoinGroupLayoutBinding
import melo.maciel.splitapp.databinding.MainLayoutBinding
import melo.maciel.splitapp.encryption.Encryption
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

class JoinGroupActivity: ComponentActivity(), View.OnClickListener  {

    lateinit var binding: JoinGroupLayoutBinding
    lateinit var apiInterface: Api_Interface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = JoinGroupLayoutBinding.inflate(layoutInflater)
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

        binding.btnJoinGroup.setOnClickListener(this)
    }


    override fun onClick(v:View?) {
        when (v?.id) {

            R.id.btn_join_group-> {
                val login = binding.loginGroup.text.toString()
                val pwd = binding.pwdLoginGroup.text.toString()

                if (login.isNotEmpty() && pwd.isNotEmpty()) {
                    // Realizar a requisição de login
                    val pwd_cript = Encryption().sha256(pwd)
                    loginGroup(login, pwd_cript)
                } else {
                    Toast.makeText(this, "Por favor, preencha os campos de login e senha", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun loginGroup(group: String, password: String) {
        val call = apiInterface.loginGroup(group, password)

        call.enqueue(object : Callback<GroupLogin> {
            override fun onResponse(call: Call<GroupLogin>, response: Response<GroupLogin>) {
                if (response.isSuccessful) {
                    // Sucesso
                    val apiDatares = response.body()
                    Toast.makeText(applicationContext, "Login bem-sucedido", Toast.LENGTH_LONG).show()
                    Log.d("JOIN-GROUP-APP", "Login bem-sucedido")

                    // Volta para a tela de Login
                    try {
                        val intent = Intent(this@JoinGroupActivity, GroupActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Captura qualquer erro relacionado à Intent ou à navegação
                        Log.d("JOIN-GROUP-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                    }

                } else {
                    // Erro na resposta
                    Toast.makeText(applicationContext, "Erro no login: ${response.message()}", Toast.LENGTH_LONG).show()
                    Log.d("JOIN-GROUP-APP", "Erro no login: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GroupLogin>, t: Throwable) {
                // Falha na requisição
                Toast.makeText(applicationContext, "Falha na requisição: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.d("JOIN-GROUP-APP", "Falha na requisição: ${t.localizedMessage}")
            }
        })
    }
}