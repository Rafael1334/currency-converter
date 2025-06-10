package com.example.conversordemoeda.model

import java.math.BigDecimal

enum class Currency(val code: String, val decimals: Int) {
    BRL("BRL", 2),
    USD("USD", 2),
    BTC("BTC", 4)
}

data class Asset(
    val currency: Currency,
    var amount: BigDecimal
)