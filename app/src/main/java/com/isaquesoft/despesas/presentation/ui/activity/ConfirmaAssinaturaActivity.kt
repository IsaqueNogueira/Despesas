package com.isaquesoft.despesas.presentation.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.ConfirmaAssinaturaActivityBinding

class ConfirmaAssinaturaActivity : AppCompatActivity() {

    companion object {
        const val COMPRA_PENDENTE = "compra_pendente"
    }

    private var compraPendente: Boolean = false
    private val binding by lazy { ConfirmaAssinaturaActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Despesas_NoActionBar)
        setContentView(binding.root)

        compraPendente = intent.getBooleanExtra(COMPRA_PENDENTE, false)

        if (compraPendente) {
            binding.confirmaAssinaturaParabens.visibility = View.GONE
            binding.confirmaAssinaturaParabensMessage.visibility = View.GONE
            binding.confirmaAssinaturaAnimationView.visibility = View.GONE
            binding.confirmaAssinaturaProcessando.visibility = View.VISIBLE
            binding.confirmaAssinaturaAnimationViewProcessando.visibility = View.VISIBLE
            binding.confirmaAssinaturaProcessandoMessage.visibility = View.VISIBLE
        }

        binding.confirmaAssinaturaClose.setOnClickListener {
            finish()
        }
    }
}
