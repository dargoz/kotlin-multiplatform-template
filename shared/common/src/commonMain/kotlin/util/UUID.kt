package util

data class UUID(val value: String)

expect fun generateUUID(): UUID