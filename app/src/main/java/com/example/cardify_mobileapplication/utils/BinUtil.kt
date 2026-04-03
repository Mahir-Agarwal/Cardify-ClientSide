package com.example.cardify_mobileapplication.utils

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object BinUtil {

    data class CardDetails(val bankName: String, val cardType: String)

    private val client = OkHttpClient()
    private val gson = Gson()

    // Data model for https://data.handyapi.com/bin/{bin}
    private class HandyBinResponse {
        var Status: String? = null
        var Scheme: String? = null
        var Type: String? = null
        var Issuer: String? = null
    }

    /**
     * Resolves Bank Name and Card Type by querying a robust public BIN API online.
     * Takes advantage of async coroutines to prevent UI freezing.
     */
    suspend fun resolveCardOnline(pan: String): CardDetails = withContext(Dispatchers.IO) {
        val cleanPan = pan.replace(Regex("[\\s-]"), "")
        if (cleanPan.length < 8) return@withContext fallback(cleanPan)

        // Modern cards use up to 8 digits for an accurate BIN match
        val bin = cleanPan.take(8) 
        
        val request = Request.Builder()
            .url("https://data.handyapi.com/bin/$bin")
            // No strict headers needed for handyapi, very mobile-friendly
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext fallback(cleanPan)

                val bodyInfo = response.body?.string() ?: return@withContext fallback(cleanPan)
                val binResponse = gson.fromJson(bodyInfo, HandyBinResponse::class.java)
                
                if (binResponse.Status != "SUCCESS") {
                    return@withContext fallback(cleanPan)
                }

                var rawBankName = binResponse.Issuer
                if (rawBankName.isNullOrBlank()) {
                    rawBankName = fallback(cleanPan).bankName 
                }

                val rawType = binResponse.Type?.uppercase() ?: "CREDIT"
                val finalType = if (rawType == "DEBIT" || rawType == "CREDIT") rawType else "CREDIT"
                
                return@withContext CardDetails(rawBankName, finalType)
            }
        } catch (e: Exception) {
            return@withContext fallback(cleanPan)
        }
    }

    private fun fallback(cleanPan: String): CardDetails {
        val bankName = when {
            cleanPan.startsWith("4") -> "VISA"
            cleanPan.startsWith("5") || cleanPan.startsWith("2") -> "MASTERCARD"
            cleanPan.startsWith("34") || cleanPan.startsWith("37") -> "AMEX"
            cleanPan.startsWith("60") || cleanPan.startsWith("65") -> "RUPAY"
            cleanPan.startsWith("6") -> "DISCOVER"
            else -> "UNRECOGNIZED"
        }
        return CardDetails(bankName, "CREDIT")
    }
}
