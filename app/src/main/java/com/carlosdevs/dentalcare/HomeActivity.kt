package com.carlosdevs.dentalcare

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /* Configuración del Toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        /* Ocultar el título DentalCare que trae por defecto la barra toolbar */
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /* Configurar el color de la barra de estado */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }

        /* Configurar el color de la barra de navegación */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        /*Color del icono de la barra de navegación*/
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.green)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
            R.id.nav_usuario -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, UserFragment())
                    .commit()
            }
            R.id.nav_cita -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CitaFragment())
                    .commit()
            }
            R.id.nav_historialClinico -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HistorialClinicoFragment())
                    .commit()
            }
            R.id.nav_pagos -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PagosFragment())
                    .commit()
            }
            R.id.nav_especialistas -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EspecialistasFragment())
                    .commit()
            }
            R.id.nav_reportes -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReportesFragment())
                    .commit()
            }
            R.id.nav_cancelarCita -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CancelarCitaFragment())
                    .commit()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
