package security.jwt

import kotlinx.serialization.Serializable

@Serializable
data class JWTPayload(
    val aud: List<String>? = null,
    val exp: Long? = null,
    val iat: Long? = null,
    val iss: String? = null,
    val jti: String? = null,
    val nbf: Long? = null,
    val sub: String? = null,
)