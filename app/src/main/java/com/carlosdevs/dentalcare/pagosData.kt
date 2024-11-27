package com.carlosdevs.dentalcare

// Clase de datos para representar un pago
data class pagosData(
    val idPago: String? = null,
    val userId: String? = null,
    val motivo: String = "",
    val monto: Int = 0,
    val metodoPago: String? = null,
    val fechaPago: String? = null,
    val nombre: String = "",
    val correo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "Pagado"
)
