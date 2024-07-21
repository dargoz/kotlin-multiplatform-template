package util

actual fun generateUUID(): UUID {
    return UUID(java.util.UUID.randomUUID().toString())
}