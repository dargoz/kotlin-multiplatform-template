package security.jwt

class DecodeException : RuntimeException {
    constructor(message: String?) : super(message)

    internal constructor(message: String?, cause: Throwable?) : super(message, cause)
}