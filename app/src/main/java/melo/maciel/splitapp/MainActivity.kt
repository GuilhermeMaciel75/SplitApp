package melo.maciel.splitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import melo.maciel.splitapp.databinding.MainLayoutBinding

class MainActivity: ComponentActivity(), View.OnClickListener {
    lateinit var binding: MainLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCriaGrupo.setOnClickListener(this)
        binding.btnEntraGrupo.setOnClickListener(this)
    }

    override fun onClick(v:View?) {

        when (v?.id) {
            R.id.btn_cria_grupo -> {
                try {
                    val intent: Intent = Intent(this, CreateGroupActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    // Captura qualquer erro relacionado à Intent ou à navegação
                    Log.d("MAIN-APP", "Erro ao iniciar a Activity: ${e.localizedMessage}")
                }
            }

            R.id.btn_entra_grupo -> {

            }
        }
    }
}