package melo.maciel.splitapp

import android.os.Bundle
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

class GroupActivity: ComponentActivity() {
    lateinit var binding: GroupLayoutBinding
    lateinit var apiInterface: Api_Interface

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

    }
}