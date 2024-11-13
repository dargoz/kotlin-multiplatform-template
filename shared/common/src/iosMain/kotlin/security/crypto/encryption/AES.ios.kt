package security.crypto.encryption

import platform.Foundation.*
import platform.Security.*

actual class AES {
    actual fun encrypt(data: ByteArray, key: ByteArray, salt: ByteArray, iv: ByteArray): ByteArray {
        val dataPtr = data.refTo(0)
        val keyPtr = key.refTo(0)
        val ivPtr = iv.refTo(0)
        val encryptedData = ByteArray(data.size + kCCBlockSizeAES128)
        val encryptedDataPtr = encryptedData.refTo(0)
        val numBytesEncrypted = nativeHeap.alloc<size_tVar>()

        val status = CCCrypt(
            CCOperation(kCCEncrypt),
            CCAlgorithm(kCCAlgorithmAES),
            CCOptions(kCCOptionPKCS7Padding),
            keyPtr, key.size.convert(),
            ivPtr,
            dataPtr, data.size.convert(),
            encryptedDataPtr, encryptedData.size.convert(),
            numBytesEncrypted.ptr
        )
        if (status != kCCSuccess) throw IllegalArgumentException("Encryption failed")
        return encryptedData.copyOfRange(0, numBytesEncrypted.value.toInt())
    }

    actual fun decrypt(data: ByteArray, key: ByteArray, salt: ByteArray, iv: ByteArray): ByteArray {
        val dataPtr = data.refTo(0)
        val keyPtr = key.refTo(0)
        val ivPtr = iv.refTo(0)
        val decryptedData = ByteArray(data.size)
        val decryptedDataPtr = decryptedData.refTo(0)
        val numBytesDecrypted = nativeHeap.alloc<size_tVar>()

        val status = CCCrypt(
            CCOperation(kCCDecrypt),
            CCAlgorithm(kCCAlgorithmAES),
            CCOptions(kCCOptionPKCS7Padding),
            keyPtr, key.size.convert(),
            ivPtr,
            dataPtr, data.size.convert(),
            decryptedDataPtr, decryptedData.size.convert(),
            numBytesDecrypted.ptr
        )
        if (status != kCCSuccess) throw IllegalArgumentException("Decryption failed")
        return decryptedData.copyOfRange(0, numBytesDecrypted.value.toInt())
    }

}