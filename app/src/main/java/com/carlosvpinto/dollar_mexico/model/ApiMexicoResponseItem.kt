package com.carlosvpinto.dollar_mexico.model

data class ApiMexicoResponseItem(
    var buy: Double,
    var date: String,
    var image: String,
    var name: String,
    var sell: Double
)