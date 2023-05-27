package com.isaquesoft.despesas.presentation.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.AssinaturaActivityBinding
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.activity.ConfirmaAssinaturaActivity.Companion.COMPRA_PENDENTE
import com.isaquesoft.despesas.utils.CustomToast

class AssinaturaActivity : AppCompatActivity() {

    private lateinit var billingClient: BillingClient
    private val binding by lazy { AssinaturaActivityBinding.inflate(layoutInflater) }
    private var planoMonth = false
    private var planoSemestral = true
    private var compraPendende = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        title = getString(R.string.version_pro_title)

        setupRadioButton()

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                    // O usuário cancelou a compra
                    CustomToast(this, getString(R.string.cancelado_pelo_usuario)).show()
                } else {
                    // Ocorreu um erro durante a compra
                }
            }
            .build()
        connectToGooglePlayBilling()
    }

    private fun connectToGooglePlayBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                connectToGooglePlayBilling()
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    getProducDetails()
                }
            }
        })
    }

    private fun getProducDetails() {
        val productIds = arrayListOf<String>()
        productIds.add("assinatura_teste")
        productIds.add("sem_anuncio_teste")

        val getProductDetailsQuery = SkuDetailsParams.newBuilder()
            .setSkusList(productIds)
            .setType(BillingClient.SkuType.SUBS)
            .build()
        billingClient.querySkuDetailsAsync(
            getProductDetailsQuery,
        ) { p0, p1 ->
            if (p0.responseCode == BillingResponseCode.OK && p1 != null) {
                binding.proBtnAssinar.setOnClickListener {
                    val planoEscolhido = p1[0]
                    billingClient.launchBillingFlow(
                        this@AssinaturaActivity,
                        BillingFlowParams.newBuilder().setSkuDetails(planoEscolhido).build(),
                    )
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Processar a compra aqui
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // A compra foi concluída com sucesso
            SharedPreferences(this).setVerifyCompraAssinatura(true)
            goToConfirmaActivity()

            if (!purchase.isAcknowledged) {
                // Se necessário, faça a confirmação da compra
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        // A compra foi confirmada com sucesso
                        // Execute as ações necessárias, como atualizar a interface do usuário ou conceder acesso ao conteúdo pago
                        SharedPreferences(this).setVerifyCompraAssinatura(true)
                    } else {
                        // Ocorreu um erro ao confirmar a compra
                    }
                }
            } else {
                // A compra já foi confirmada anteriormente
                // Execute as ações necessárias, como atualizar a interface do usuário ou conceder acesso ao conteúdo pago
                SharedPreferences(this).setVerifyCompraAssinatura(true)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            // A compra está pendente
            // Isso geralmente ocorre para compras assíncronas, como assinaturas, que requerem processamento adicional
            // Você pode atualizar a interface do usuário para indicar que a compra está em andamento
            compraPendende = true
            goToConfirmaActivity()
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            // O estado da compra é desconhecido
            // Lide com esse caso de acordo com a sua lógica de negócio
        }
    }

    private fun goToConfirmaActivity() {
        Intent(this, ConfirmaAssinaturaActivity::class.java).apply {
            this.putExtra(COMPRA_PENDENTE, compraPendende)
            startActivity(this)
            finish()
        }
    }

    private fun setupRadioButton() {
        binding.proRadioSemestral.isChecked = planoSemestral
        binding.proRadioMonth.isChecked = !planoSemestral

        binding.proMonthContainer.setOnClickListener {
            setRadioButtonState(true)
        }

        binding.proSemestralContainer.setOnClickListener {
            setRadioButtonState(false)
        }
    }

    private fun setRadioButtonState(isMonthSelected: Boolean) {
        val blueColor = getColor(R.color.blue_600)
        val grayColor = Color.parseColor("#8C8C8C")

        binding.proRadioMonth.setTextColor(if (isMonthSelected) blueColor else grayColor)
        binding.proMonthValue.setTextColor(if (isMonthSelected) blueColor else grayColor)
        binding.proRadioSemestral.setTextColor(if (isMonthSelected) grayColor else blueColor)
        binding.proSemestralValue.setTextColor(if (isMonthSelected) grayColor else blueColor)
        binding.proMonthContainer.setBackgroundResource(if (isMonthSelected) R.drawable.circular_pro_selected else R.drawable.circular_pro)
        binding.proSemestralContainer.setBackgroundResource(if (isMonthSelected) R.drawable.circular_pro else R.drawable.circular_pro_selected)
        binding.proRadioSemestral.isChecked = !isMonthSelected
        binding.proRadioMonth.isChecked = isMonthSelected

        planoMonth = isMonthSelected
        planoSemestral = !isMonthSelected
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }
}
