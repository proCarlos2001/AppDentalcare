package com.carlosdevs.dentalcare

data class userData(
    val userId: String,
    val name: String,
    val number: String,
    val gender: String,
    val photoUrl: String
){
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "")
}
