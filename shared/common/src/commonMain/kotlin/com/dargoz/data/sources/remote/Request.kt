package com.dargoz.data.sources.remote

sealed class Request(open val headers: Map<String, String> = emptyMap()) {
    class ApplicationJson(override val headers: Map<String, String>, val json: String): Request()
    class MultiPart(override val headers: Map<String, String>, val file: File): Request()
}

sealed class File(val contentType: String, open val fileName: String = "", open val data: ByteArray) {
    class JPEG(override val fileName: String, override val data: ByteArray): File(contentType = "image/jpeg", data = data)
    class PNG(override val fileName: String, override val data: ByteArray): File(contentType = "image/png", data = data)
    class PDF(override val fileName: String, override val data: ByteArray): File(contentType = "application/pdf", data = data)
}