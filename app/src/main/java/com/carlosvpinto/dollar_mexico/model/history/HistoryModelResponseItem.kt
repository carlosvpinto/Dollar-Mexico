package com.carlosvpinto.dollar_mexico.model.history

data class HistoryModelResponseItem(
    var historical: List<Historical>,
    var id: Int,
    var name: String
)