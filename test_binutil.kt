import okhttp3.OkHttpClient
import okhttp3.Request

fun main() {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://lookup.binlist.net/45717360")
        .header("Accept-Version", "3")
        .build()
        
    try {
        client.newCall(request).execute().use { response ->
            println(response.code)
            println(response.body?.string())
        }
    } catch (e: Exception) {
        println(e)
    }
}
