package com.carlosdevs.dentalcare

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Se definen las variables que se van a usar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager
    private lateinit var vibrator: Vibrator

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /* Se llama a la función onBottomNavigation */
        onBottomNavigation()

        /* Se instancia la propiedad de vibrator */
        vibrator = getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator

        /* Configurar el color de la barra de estado */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        }
        /* Configurar el color de la barra de navegación */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        }

        /* Configuración del Toolbar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        /* Ocultar el título DentalCare que trae por defecto la barra toolbar */
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /* Se implementa la navegación del icono user dentro del toolbar */
        val toolbarUser = findViewById<ImageView>(R.id.toolbar_user)
        toolbarUser.setOnClickListener {

            // Vibrar al presionar el botón
            vibrate()

            openFragment(UserFragment())
        }

        /* Se instancia el bottomNavigationView (se inicia) */
        bottomNavigationView = findViewById(R.id.botton_navigation)

        /* Se incicia el drawerLayout donde esta el menu lateral */
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        /* Se inicia el drawerLayout y se cierra respectivamente */
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        /*Color del icono de la barra de navegación*/
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.green)

        /* Se devuelve del drawerLayout al Fragment home con el botón de retroceso */
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
        // Recuperar la información del usuario
        auth.currentUser?.let { user ->
            getUserData(user.uid)
        }
    }

    /* Se configura la navegación del bottomNavigationView (la barra inferior) */
    private fun onBottomNavigation() {
        bottomNavigationView = findViewById(R.id.botton_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            // Vibrar al presionar el botón
            vibrate()

            when (menuItem.itemId) {
                R.id.button_nav_home -> openFragment(HomeFragment())
                R.id.button_nav_location -> openFragment(UbicationFragment())
                R.id.button_nav_assitant -> openFragment(AsistenteVirtualFragment())
                R.id.button_nav_phone -> openFragment(ContactoFragment())
            }
            true  /* devuelve true para indicar que el evento se ha consumido */
        }
    }

    /* Se configura la navegación del drawerLayout y Toolbar (la barra superior) */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Vibrar al presionar el botón
        vibrate()

        when (item.itemId) {
            R.id.nav_home -> openFragment(HomeFragment())
            R.id.nav_usuario -> openFragment(UserFragment())
            R.id.nav_cita -> openFragment(CitaFragment())
            R.id.nav_historialClinico -> openFragment(HistorialClinicoFragment())
            R.id.nav_pagos -> openFragment(PagosFragment())
            R.id.nav_especialistas -> openFragment(EspecialistasFragment())
            R.id.nav_reportes -> openFragment(ReportesFragment())
            R.id.nav_cancelarCita -> openFragment(CancelarCitaFragment())
            R.id.nav_logout -> {

                // Eliminar el estado de la sesión
                val sharedPref = getSharedPreferences("DentalCare", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("Sesión iniciada", false)
                    apply()
                }

                // Redirigir al LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Para que no se pueda volver a la pantalla de home con el botón "atrás"
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true   /* devuelve true para indicar que el evento se ha consumido */
    }

    /* Se instancia el evento de abrir y cerrar el drawerLayout */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /* Reemplaza el contenido del contenedor de fragmentos con un nuevo fragmento. */
    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null)
        transaction.commit()
    }

    /* Función que almacena la lógica para la vibración */
    private fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }

    // Función que permite obtener la información del usuario
    private fun getUserData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(userData::class.java)
                    if (user != null) {
                        Log.d("HomeActivity", "User data retrieved: $user")

                        // Actualizar la UI con la información del usuario
                        updateHeader(user)
                        updateImageToolbar(user)
                    }
                } else {
                    Log.d("HomeActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.d("HomeActivity", "Error getting user data", e)
            }
    }
    private fun updateHeader(user: userData) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)

        val headerName = headerView.findViewById<TextView>(R.id.header_name)
        val headerEmail = headerView.findViewById<TextView>(R.id.header_email)
        val headerImage = headerView.findViewById<ImageView>(R.id.header_image)

        // Obtener el correo electrónico del usuario desde FirebaseAuth
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        headerName.text = user.name
        headerEmail.text = userEmail ?: "Email no disponible"

        // Verificar la URL de la imagen
        Log.d("updateHeader", "URL: ${user.photoUrl}")

        // Cargar la imagen en NavHeader usando Glide
        if (!user.photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.nav_user) // Imagen por defecto mientras se carga
                .error(R.drawable.nav_user) // Imagen por defecto en caso de error
                .circleCrop() // Recortar en forma circular
                .into(headerImage)
        } else {
            // Si no hay URL, usar imagen por defecto
            headerImage.setImageResource(R.drawable.nav_user)
        }
    }
    // Cargar la imagen en toolbar_user usando Glide
    private fun updateImageToolbar(user: userData) {
        val ToolbarImage: ImageView = findViewById(R.id.toolbar_user)

        if (!user.photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.nav_user)
                .circleCrop()
                .into(ToolbarImage)
        } else {
            // Si no hay URL, usar una imagen por defecto
            ToolbarImage.setImageResource(R.drawable.nav_user)
        }
    }
}