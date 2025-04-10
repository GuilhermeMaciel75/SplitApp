package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import melo.maciel.splitapp.API.Api_Interface
import melo.maciel.splitapp.API.Participant
import melo.maciel.splitapp.API.PostSpent
import melo.maciel.splitapp.Adapter.AdapterSpent
import melo.maciel.splitapp.databinding.RegisterSpentLayoutBinding
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
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import melo.maciel.splitapp.API.API_DATA
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class RegisterSpentActivity : ComponentActivity(), View.OnClickListener {
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

        if (groupId != null) {
            Log.d("RegisterSpent-APP", "Group recebido: $groupId")
        } else {
            groupId = "Null"
            Log.d("RegisterSpent-APP", "Nenhum Group foi passado")
        }

        // Usando view binding para acessar o RecyclerView
        val recyclerViewGroups = binding.recyclerViewSpent
        recyclerViewGroups.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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

        binding.btnSaveSpent.setOnClickListener(this)

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

        Log.d("RegisterSpent-APP", "login: ${login}")

        binding.totalValueSpent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                // Não precisamos implementar aqui
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, after: Int) {
                // Verificar se a checkbox "Dividir Igualmente" está marcada
                if (binding.checkboxDivideEqually.isChecked) {
                    val spentValueString = charSequence.toString()

                    val spentValue = if (spentValueString.isNotEmpty()) {
                        spentValueString.toDoubleOrNull() ?: 0.0
                    } else {
                        0.0
                    }

                    // Se houver participantes e a checkbox estiver marcada, dividimos o valor igualmente
                    if (spentValue > 0 && listParticipants.isNotEmpty()) {
                        val equalShare = spentValue / listParticipants.size

                        // Atualiza os valores dos participantes
                        listParticipants.forEach { participant ->
                            participant.value = equalShare
                        }

                        // Notificar o Adapter para atualizar a visualização
                        adapterGroup.notifyDataSetChanged()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // Não precisamos implementar aqui
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save_spent -> {
                // Obter os dados dos participantes digitados
                val updatedParticipants =
                    (binding.recyclerViewSpent.adapter as AdapterSpent).participants

                val spentValueString = binding.totalValueSpent.text.toString()

                val spentValue = if (spentValueString.isNotEmpty()) {
                    spentValueString.toDoubleOrNull() ?: 0.0 // Caso a conversão falhe, define 0.0
                } else {
                    0.0 // Caso o campo esteja vazio, define 0.0
                }

                // Criando o objeto PostSpent
                val postSpent = PostSpent(
                    id_group = groupId.orEmpty(),
                    type_spent = binding.typeSpent.text.toString(),
                    login_user = login.orEmpty(),
                    spent_description = binding.spentDescription.text.toString(),
                    spent_value = spentValue,
                    participants_spent = updatedParticipants
                )

                // Chama a função para registrar o gasto
                registerSpentPost(postSpent)
            }
        }
    }


    private fun registerSpentPost(postSpent: PostSpent) {
        // Verifique se a apiInterface foi inicializada
        if (!::apiInterface.isInitialized) {
            Log.e("RegisterSpent-APP", "API Interface não inicializada")
            return
        }

        val call = apiInterface.registerSpent(postSpent)

        call.enqueue(object : Callback<API_DATA> {
            override fun onResponse(call: Call<API_DATA>, response: Response<API_DATA>) {
                if (response.isSuccessful) {
                    // Sucesso
                    val apiDataRes = response.body()
                    Toast.makeText(
                        applicationContext,
                        "Gasto registrado com sucesso",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("RegisterSpent-APP", "Gasto registrado com sucesso")

                    // Caso queira fazer alguma ação após o sucesso, como redirecionar o usuário
                    try {
                        val intent = Intent(this@RegisterSpentActivity, GroupActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Captura qualquer erro relacionado à Intent ou navegação
                        Log.d(
                            "RegisterSpent-APP",
                            "Erro ao iniciar a Activity: ${e.localizedMessage}"
                        )
                    }

                } else {
                    // Erro na resposta
                    Toast.makeText(
                        applicationContext,
                        "Erro ao registrar gasto: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("RegisterSpent-APP", "Erro ao registrar gasto: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<API_DATA>, t: Throwable) {
                // Falha na requisição
                Toast.makeText(
                    applicationContext,
                    "Falha na requisição: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("RegisterSpent-APP", "Falha na requisição: ${t.localizedMessage}")
            }
        })
    }
}

