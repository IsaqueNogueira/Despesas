package com.isaquesoft.despesas.presentation.ui.activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.isaquesoft.despesas.AlarmService
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.ActivityMainBinding
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val estadoAppViewModel: EstadoAppViewModel by viewModel()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController
    private lateinit var adView: AdView
    private var initialLayoutComplete = false
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var billingClient: BillingClient
    private lateinit var manager: ReviewManager
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        estadoAppViewModel.componentes.observe(
            this,
            Observer {
                it?.let {
                    if (it.appBar) {
                        supportActionBar?.show()
                    } else {
                        supportActionBar?.hide()
                    }
                    if (it.temBotaoBack) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    } else {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
            },
        )
        setupBottomMenu()

        carregaAnuncioIntersticial()

        // Verifique se a permissão para exibir notificações já foi concedida
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            // A permissão ainda não foi concedida, solicite-a

            setupDialogPermission()
        }

        startService(Intent(this, AlarmService::class.java))

        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build(),
        )

        verifyAssinaturaInit()

        connectToGooglePlayBilling()

        adView = AdView(this)
        if (!SharedPreferences(this).getVerifyCompraAssinatura()) {
            binding.adViewContainer.addView(adView)
            binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
                if (!initialLayoutComplete) {
                    initialLayoutComplete = true
                    loadBanner()
                }
            }
        }
        manager = ReviewManagerFactory.create(this)
        setInAppReviews()
    }

    private fun setInAppReviews() {
        val enteredTheApp = SharedPreferences(this).getQuantityUseApp()
        if (enteredTheApp <= 15) {
            val somaEnteredTheApp = enteredTheApp + 1
            SharedPreferences(this).setQuantityUseApp(somaEnteredTheApp)
        }
        if (enteredTheApp == 15) {
            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                }
            }
        }
    }

    private fun verifyAssinaturaInit() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            .build()
    }

    private fun connectToGooglePlayBilling() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                connectToGooglePlayBilling()
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    checkSubscriptionStatus()
                }
            }
        })
    }

    private fun checkSubscriptionStatus() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { purchasesResult, purchasesList ->
            if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                // Verificar se o usuário possui uma assinatura ativa
                var hasActiveSubscription = false
                for (purchase in purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged) {
                        // A assinatura foi comprada e confirmada com sucesso
                        hasActiveSubscription = true
                        break
                    }
                }

                if (hasActiveSubscription) {
                    // O usuário possui uma assinatura ativa
                    // Execute as ações necessárias para conceder acesso ao conteúdo pago
                    SharedPreferences(this).setVerifyCompraAssinatura(true)
                } else {
                    // O usuário não possui assinaturas ativas
                    // Execute as ações necessárias para lidar com o usuário sem assinatura
                    SharedPreferences(this).setVerifyCompraAssinatura(false)
                }
            } else {
                // Ocorreu um erro ao verificar as assinaturas do usuário
                // Lide com o erro de acordo com a sua lógica de tratamento de erros
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged) {
            // A assinatura foi comprada e confirmada com sucesso
            // Execute as ações necessárias para conceder acesso ao conteúdo pago
            SharedPreferences(this).setVerifyCompraAssinatura(true)
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            // A assinatura está pendente de confirmação
            // Aguarde a confirmação e execute as ações necessárias quando confirmada
            SharedPreferences(this).setVerifyCompraAssinatura(false)
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            // O estado da assinatura é desconhecido
            // Lide com esse caso de acordo com a sua lógica de negócio
            SharedPreferences(this).setVerifyCompraAssinatura(false)
        }
    }

    private fun setupDialogPermission() {
        val getSharedPreferences = SharedPreferences(this).getNotificationDialogShow()
        if (!getSharedPreferences) {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_permission_notification, null)
            val alertDialog = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()
            alertDialog.setOnShowListener {
                view.apply {
                    val buttonClose = findViewById<Button>(R.id.dialog_permission_negative)
                    val buttonPositive = findViewById<Button>(R.id.dialog_permission_positive)
                    buttonPositive.setOnClickListener {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)

                        alertDialog.dismiss()
                    }

                    buttonClose.setOnClickListener { alertDialog.dismiss() }
                }
            }

            alertDialog.show()
            SharedPreferences(this).setNotificationDialogShow(true)
        }
    }

    private fun setupBottomMenu() {
        navController = Navigation.findNavController(this, R.id.main_activity_nav_host)
        val bottomNavView = binding.bottomNavigationView
        bottomNavView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val mostraIntersticial = SharedPreferences(this).getIntersticialPrincipalExecute()
            if (!mostraIntersticial) {
                if (!SharedPreferences(this).getVerifyCompraAssinatura()) {
                    mInterstitialAd?.show(this)
                    carregaAnuncioIntersticial()
                }
            }
            SharedPreferences(this).setIntersticialPrincipalExecute(true)
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onStop() {
        SharedPreferences(this).setIntersticialPrincipalExecute(false)
        super.onStop()
    }

    override fun onDestroy() {
        adView.destroy()
        SharedPreferences(this).setIntersticialPrincipalExecute(false)
        super.onDestroy()
    }

    private fun loadBanner() {
        adView.adUnitId = AD_UNIT_ID

        adView.setAdSize(adSize)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    companion object {
        private val AD_UNIT_ID = "ca-app-pub-6470587668575312/8245656992"
    }

    private fun carregaAnuncioIntersticial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-6470587668575312/6733264772",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            },
        )
    }
}
