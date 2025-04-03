package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import melo.maciel.splitapp.databinding.LoginLayoutBinding


class LoginActivity : ComponentActivity(), View.OnClickListener {
    lateinit var binding: LoginLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LoginLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(this)
        binding.btnMakeCadastro.setOnClickListener(this)

    }

    override fun onClick(v:View?) {

        when (v?.id) {
            R.id.btn_login -> {

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
}
