package util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldHaveLength

class UUIDJvmTest : FunSpec() {
    init {
        test("uuids should be in be type 4 format") {
            generateUUID().value.shouldHaveLength(36)
        }
    }
}