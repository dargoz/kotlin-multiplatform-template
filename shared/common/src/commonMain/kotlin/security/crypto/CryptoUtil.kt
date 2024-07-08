package security.crypto

expect class CryptoUtil constructor() {

     fun rs256(token: String): String

}