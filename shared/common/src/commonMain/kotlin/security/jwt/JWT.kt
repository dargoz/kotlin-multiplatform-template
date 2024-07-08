package security.jwt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.jsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class JWT(token: String) {
    var header: Map<String, String> = emptyMap()
        private set

    private var value: JsonElement? = null

    var signature: String = ""
        private set

    init {
        decode(token)
    }

    private fun decode(token: String) {
        val parts = splitToken(token)
        val payloadDecoded = base64Decoded(parts[1])
        value = Json.parseToJsonElement(payloadDecoded)
        header = parseJson<Map<String, String>>(base64Decoded(parts[0]))
        val payload = parseJson<JWTPayload>(payloadDecoded)
        signature = parts[2]
        println("signature = $signature")

    }

    private fun splitToken(token: String): List<String> {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw DecodeException("The token was expected to have 3 parts, but got ${parts.size}.")
        }
        return parts
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun base64Decoded(base64String: String): String {
        val bytes = Base64.UrlSafe.decode(base64String)
        return bytes.decodeToString()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> parseJson(jsonString: String): T {
        val json = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            ignoreUnknownKeys = true
        }
        val jsonElement = json.decodeFromString<T>(jsonString)
        return jsonElement
    }

    fun getClaim(forName: String): Claim? {
        val claim = value?.let {
            it.jsonObject[forName]?.let { element ->
                Claim(element)
            }
        }
        return claim
    }
}