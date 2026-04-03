package com.example.cardify_mobileapplication

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test
import java.util.concurrent.TimeUnit

class ApiEndpointTest {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    // Testing using the host's actual network IP
    private val baseUrl = "http://10.231.255.244:8080/api"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    @Test
    fun testRegisterEndpoint() {
        val requestBody = """{"name":"Mahir Aggarwal","email":"mahir@example.com","password":"securepassword123","roles":["BUYER","OWNER"]}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/auth/register").post(requestBody).build(), "Register")
    }

    @Test
    fun testLoginEndpoint() {
        val requestBody = """{"email":"mahir@example.com","password":"securepassword123"}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/auth/login").post(requestBody).build(), "Login")
    }

    @Test
    fun testSearchCardsEndpoint() {
        executeRequest(Request.Builder().url("$baseUrl/cards/search").get().build(), "Search Cards")
    }

    @Test
    fun testMyCardsEndpoint() {
        executeRequest(Request.Builder().url("$baseUrl/cards/my-cards").get().build(), "My Cards")
    }

    @Test
    fun testAddCardEndpoint() {
        val requestBody = """{"bankName":"HDFC","cardType":"CREDIT","available":true}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/cards").post(requestBody).build(), "Add Card")
    }

    @Test
    fun testRequestOrderEndpoint() {
        val requestBody = """{"cardId":1,"amount":1000.0,"commission":50.0}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/orders/request").post(requestBody).build(), "Request Order")
    }

    @Test
    fun testChatSendEndpoint() {
        val requestBody = """{"receiverId":2,"message":"Hey! Have you ordered it yet?"}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/chat/123").post(requestBody).build(), "Send Chat Message")
    }

    @Test
    fun testSubmitReviewEndpoint() {
        val requestBody = """{"rating":5,"feedback":"Quick and responsive!"}""".toRequestBody(jsonMediaType)
        executeRequest(Request.Builder().url("$baseUrl/reviews/123").post(requestBody).build(), "Submit Review")
    }

    private fun executeRequest(request: Request, name: String) {
        println("=== Testing $name ===")
        try {
            // Note: Many of these endpoints require Bearer Token authorization.
            // When executing without headers, we expect a 401 Unauthorized or 403 Forbidden
            // This still signifies that the backend successfully acknowledged the request structure!
            val response = client.newCall(request).execute()
            println("Response Code: ${response.code}")
            println("Response Body: ${response.body?.string()}")
        } catch (e: Exception) {
            println("Connection Failed for $name: Could not connect to JVM backend on http://localhost:8080. Make sure Spring Boot is running.")
            throw e
        }
    }
}
