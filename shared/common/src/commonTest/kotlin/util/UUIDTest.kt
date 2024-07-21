package util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize

/**
 *  The IntelliJ Kotest plugin does not support running common, native or JS tests
 *  directly from the IDE using the green run icons.
 *  Only execution via gradle is supported.
 */
class UUIDTest : FunSpec({

    test("uuids should be somewhat unique!") {
        List(100) { generateUUID() }.toSet().shouldHaveSize(100)
    }

})
