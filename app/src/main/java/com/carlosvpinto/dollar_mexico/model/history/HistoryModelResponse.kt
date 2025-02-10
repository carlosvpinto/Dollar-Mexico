package com.carlosvpinto.dollar_mexico.model.history
data class HistoryModelResponse(
    val historical: List<HistoricalItem>,
    val id: Int,
    val name: String
)

data class HistoricalItem(
    val buy: Double,
    val date: String,
    val sell: Double
)
