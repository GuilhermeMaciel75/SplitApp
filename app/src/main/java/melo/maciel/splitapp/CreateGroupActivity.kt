package melo.maciel.splitapp

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import melo.maciel.splitapp.databinding.CreateGroupLayoutBinding
import melo.maciel.splitapp.databinding.MainLayoutBinding

class CreateGroupActivity: ComponentActivity(), View.OnClickListener  {
    lateinit var binding: CreateGroupLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = CreateGroupLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onClick(v:View?) {

        when (v?.id) {
            R.id.btn_cadastro_group -> {

            }
        }
    }
}