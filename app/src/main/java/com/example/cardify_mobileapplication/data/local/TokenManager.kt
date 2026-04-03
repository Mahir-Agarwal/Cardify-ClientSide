package com.example.cardify_mobileapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore("user_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_RATING_KEY = doublePreferencesKey("user_rating")
        private val TOTAL_ORDERS_KEY = intPreferencesKey("total_orders")
        private val EARNED_AMOUNT_KEY = doublePreferencesKey("earned_amount")
    }

    val jwtToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[JWT_TOKEN_KEY]
    }

    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val userRating: Flow<Double?> = context.dataStore.data.map { it[USER_RATING_KEY] }
    val totalOrders: Flow<Int?> = context.dataStore.data.map { it[TOTAL_ORDERS_KEY] }
    val earnedAmount: Flow<Double?> = context.dataStore.data.map { it[EARNED_AMOUNT_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun saveProfileStats(rating: Double, orders: Int, earned: Double) {
        context.dataStore.edit { preferences ->
            preferences[USER_RATING_KEY] = rating
            preferences[TOTAL_ORDERS_KEY] = orders
            preferences[EARNED_AMOUNT_KEY] = earned
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
        }
    }
}
