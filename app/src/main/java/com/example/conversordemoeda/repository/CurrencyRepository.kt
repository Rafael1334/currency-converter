package com.example.conversordemoeda.repository


import com.example.conversordemoeda.api.ApiClient
import com.example.conversordemoeda.model.Currency
import java.math.BigDecimal
import java.math.RoundingMode

object CurrencyRepository {
    suspend fun convert(
        from: Currency,
        to: Currency,
        amount: BigDecimal
    ): BigDecimal {
        var pair: String
        var pairResponse: String
        if(to.code == "BTC"){
            pair = "${to.code}-${from.code}".uppercase()
            pairResponse = "${to.code}${from.code}".uppercase()
        }else{
            pair  = "${from.code}-${to.code}".uppercase()
            pairResponse = "${from.code}${to.code}".uppercase()
        }

        try {
            val response = ApiClient.service.getRate(pair,
                            "ed368fa2f8d21e7a41fdef3201b9f408fa58b58611a2d09774a31ec334e506c4")
            val rate = BigDecimal(response[pairResponse]?.bid)
            if(to.code == "BTC"){
                return amount.divide(rate, to.decimals, RoundingMode.HALF_UP)
            }else{
                return amount.multiply(rate).setScale(to.decimals, RoundingMode.HALF_UP)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return BigDecimal.ZERO
    }
}