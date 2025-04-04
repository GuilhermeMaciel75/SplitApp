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
import melo.maciel.splitapp.API.GroupData
import melo.maciel.splitapp.API.UserData
import melo.maciel.splitapp.databinding.CreateGroupLayoutBinding
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

class CreateGroupActivity: ComponentActivity(), View.OnClickListener  {
    lateinit var binding: CreateGroupLayoutBinding
    lateinit var apiInterface: Api_Interface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = CreateGroupLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.btnCadastroGroup.setOnClickListener(this)

    }

    override fun onClick(v:View?) {

        when (v?.id) {
            R.id.btn_cadastro_group -> {
                val name = binding.nomeGrupo.text.toString()
                val description = binding.descricaoGrupo.text.toString()
                val pwd = binding.pwdGroup.text.toString()
                val pwd_confirm = binding.pwdConfirmacaoGroup.text.toString()
                val n_participants = binding.numeroParticipantesGrupo.text.toString()

                if (name.isNotEmpty() && description.isNotEmpty() && pwd.isNotEmpty() && pwd_confirm.isNotEmpty() && n_participants.isNotEmpty()) {
                    if (pwd == pwd_confirm) {
                        val pwd_cript = Encryption().sha256(pwd)
                        val userDataGroup = GroupData(name, description, pwd_cript, n_participants.toInt())
                        registerGroupPost(userDataGroup)
                    } else {
                        Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                        Log.d("GroupRegister-APP", "As senhas não coincidem")
                    }
                } else {
                    Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                    Log.d("GroupRegister-APP", "Por favor, preencha todos os campos")
                }
            }
        }
    }

    private fun registerGroupPost(userData: GroupData) {
        val call = apiInterface.register(userData)

        call.enqueue(object : Callback<API_DATA> {
            override fun onResponse(call: Call<API_DATA>, response: Response<API_DATA>) {
                if (response.isSuccessful) {
                    // Sucesso
                    val apiDatares = response.body()
                    Toast.makeText(applicationContext, "Cadastro bem-sucedido", Toast.LENGTH_LONG).show()
                    Log.d("GroupRegister-APP", "Cadastro bem-sucedido")

                    // Volta para a tela de Login
                    try {
                       val intent = Intent(this@CreateGroupActivity, GroupActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Captura qualquer erro relacionado à Intent ou à navegação
                        Log.d("GroupRegister-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                    }

                } else {
                    // Erro na resposta
                    Toast.makeText(applicationContext, "Erro ao registrar usuário: ${response.message()}", Toast.LENGTH_LONG).show()
                    Log.d("GroupRegister-APP", "Erro ao registrar usuário: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<API_DATA>, t: Throwable) {
                // Falha na requisição
                Toast.makeText(applicationContext, "Falha na requisição: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.d("GroupRegister-APP", "Falha na requisição: ${t.localizedMessage}")
            }
        })
    }
}