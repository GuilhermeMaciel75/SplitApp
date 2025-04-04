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
import melo.maciel.splitapp.databinding.LoginLayoutBinding
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


class LoginActivity : ComponentActivity(), View.OnClickListener {
    lateinit var binding: LoginLayoutBinding
    lateinit var apiInterface: Api_Interface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LoginLayoutBinding.inflate(layoutInflater)
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


        binding.btnLogin.setOnClickListener(this)
        binding.btnMakeCadastro.setOnClickListener(this)

    }

    override fun onClick(v:View?) {

        when (v?.id) {
            R.id.btn_login -> {
                val login = binding.login.text.toString()
                val senha = binding.pwd.text.toString()

                if (login.isNotEmpty() && senha.isNotEmpty()) {
                    // Realizar a requisição de login
                    loginUser(login, senha)
                } else {
                    Toast.makeText(this, "Por favor, preencha os campos de login e senha", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btn_make_cadastro -> {
                try {
                    val intent: Intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    // Captura qualquer erro relacionado à Intent ou à navegação
                    Log.d("Login-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        val call = apiInterface.login(username, password)

        call.enqueue(object : Callback<API_DATA> {
            override fun onResponse(call: Call<API_DATA>, response: Response<API_DATA>) {
                if (response.isSuccessful) {
                    // Sucesso
                    val apiDatares = response.body()
                    Toast.makeText(applicationContext, "Login bem-sucedido", Toast.LENGTH_LONG).show()
                    Log.d("Login-APP", "Login bem-sucedido")

                    // Volta para a tela de Login
                    try {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Captura qualquer erro relacionado à Intent ou à navegação
                        Log.d("Login-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                    }

                } else {
                    // Erro na resposta
                    Toast.makeText(applicationContext, "Erro no login: ${response.message()}", Toast.LENGTH_LONG).show()
                    Log.d("Login-APP", "Erro no login: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<API_DATA>, t: Throwable) {
                // Falha na requisição
                Toast.makeText(applicationContext, "Falha na requisição: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.d("Login-APP", "Falha na requisição: ${t.localizedMessage}")
            }
        })
    }
}

