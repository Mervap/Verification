package itmo.analyzer.ast

import itmo.analyzer.testName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.io.File
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun javaUtilArrayDeque(testInfo: TestInfo) = doTest(testInfo)

    private fun doTest(testInfo: TestInfo) {
        val testName = testInfo.testName
        val src = File("src/test/testData/parser/$testName.java")
        val after = File("src/test/testData/parser/$testName.ast")
        assertEquals(after.readText(), src.parseJavaCode().printAST())
    }
}
