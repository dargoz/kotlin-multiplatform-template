package security.jwt

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*
import kotlin.reflect.KClass


class Claim(var value: JsonElement) {

    /**
     * Get this Claim as a Boolean.
     * If the value isn't of type Boolean, or it can't be converted to a Boolean, null will be returned.
     *
     * @return the value as a Boolean or null.
     */
    fun asBoolean(): Boolean? {
        return try {
            value.jsonPrimitive.boolean
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get this Claim as an Integer.
     * If the value isn't of type Integer or it can't be converted to an Integer, null will be returned.
     *
     * @return the value as an Integer or null.
     */
    fun asInt(): Int? {
        return try {
            value.jsonPrimitive.intOrNull
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get this Claim as an Long.
     * If the value isn't of type Long or it can't be converted to an Long, null will be returned.
     *
     * @return the value as an Long or null.
     */
    fun asLong(): Long? {
        return try {
            value.jsonPrimitive.longOrNull
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get this Claim as a Double.
     * If the value isn't of type Double or it can't be converted to a Double, null will be returned.
     *
     * @return the value as a Double or null.
     */
    fun asDouble(): Double? {
        return try {
            value.jsonPrimitive.doubleOrNull
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get this Claim as a String.
     * If the value isn't of type String or it can't be converted to a String, null will be returned.
     *
     * @return the value as a String or null.
     */
    fun asString(): String? {
        return try {
            value.toString()
        } catch (e: Exception) {
            null
        }
    }

    fun asDate(): LocalDateTime? {
        return null
        /*val ms: Long = value.toString().toLong() * 1000
        return LocalDateTime.parse(input = ms.toString(), format = kotlinx.datetime.format.DateTimeFormat())*/
    }

    @Throws(DecodeException::class)
    fun <T: Any> asList(kClass: KClass<T>): List<T>? {
        try {
            val jsonString = value.toString()
            return emptyList()
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    /**
     * Get this Claim as a Object of type T.
     * If the value isn't of type Object, null will be returned.
     *
     * @return the value as a Object of type T or null.
     * @throws DecodeException if the value can't be converted to a class T.
     */
    inline fun <reified T> asObject(): T? {
        try {
            val jsonString = value.toString()
            return Json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            throw DecodeException(e.message)
        }
    }
}