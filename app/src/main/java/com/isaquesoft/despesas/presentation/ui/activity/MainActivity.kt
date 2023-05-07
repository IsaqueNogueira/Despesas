package com.isaquesoft.despesas.presentation.ui.activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
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

        // Verifique se a permissão para exibir notificações já foi concedida
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            // A permissão ainda não foi concedida, solicite-a

            setupDialogPermission()
        }

        startService(Intent(this, AlarmService::class.java))
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
            NavigationUI.onNavDestinationSelected(item, navController)
        }
    }
}
