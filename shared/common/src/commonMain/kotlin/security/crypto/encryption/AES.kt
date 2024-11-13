package security.crypto.encryption

expect class AES {
    fun encrypt(data: ByteArray, key: ByteArray, salt: ByteArray, iv: ByteArray): ByteArray

    fun decrypt(data: ByteArray, key: ByteArray, salt: ByteArray, iv: ByteArray): ByteArray
}