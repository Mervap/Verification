package itmo.analyzer

import org.junit.jupiter.api.TestInfo
import java.util.*

val TestInfo.testName: String
    get() = testMethod.get().name.replaceFirstChar { it.titlecase(Locale.getDefault()) }

val TestInfo.lowercaseTestName: String
    get() = testMethod.get().name.replaceFirstChar { it.lowercase(Locale.getDefault()) }
