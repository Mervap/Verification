package itmo.analyzer.analyze

import itmo.analyzer.lowercaseTestName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.test.assertEquals

class WrongPackageFounderTest {

    @Test
    fun withProblems(testInfo: TestInfo) = doTest(testInfo) {
        """
            itmo/test/Helper.java: 1 problems founded
              (line 1,col 9): Unexpected package for 'itmo/test/Helper.java': itmo.test.model
    
            itmo/test/model/User.java: 1 problems founded
              (line 1,col 9): Unexpected package for 'itmo/test/model/User.java': itmo.test
        """.trimIndent()
    }

    @Test
    fun withoutProblems(testInfo: TestInfo) = doTest(testInfo) {
        "No any problems found!"
    }

    @Test
    fun badProject(testInfo: TestInfo) = doTest(testInfo) {
        "No java project files found. Check that '$it' is correct java project directory"
    }

    private fun doTest(testInfo: TestInfo, expected: (String) -> String) {
        val path = Path.of("src/test/testData/packageFinder/${testInfo.lowercaseTestName}").absolutePathString()
        assertEquals(expected(path).trim(), collectProjectProblems(path).trim())
    }
}
