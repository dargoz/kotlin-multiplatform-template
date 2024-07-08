package security.jwt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder


class JWTDeserializer(override val descriptor: SerialDescriptor) : DeserializationStrategy<JWTPayload> {

    override fun deserialize(decoder: Decoder): JWTPayload {
       return JWTPayload()
    }
}