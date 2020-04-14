package calc

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class CalcTest {
    @Test
    fun positiveTests() {
        testPositive(2.0, "1 + 1")
        testPositive(9.0, " 5  + 8 / 2 ")
        testPositive(60.0, "(12  * 5)")
        testPositive(12.5, "(12.5)")
        testPositive(12.5, "12.5  ")
        testPositive(11.5, "(12 * 5  ) - (45 + ( 24 / 12 + 1.5))")
        testPositive(11.5, "12  *   5  -   (45  +  24 / 12  +  1.5)")
        testPositive(-1.0, " 1 / -1")
        testPositive(1.0, "-1 / -1")
        testPositive(Double.NEGATIVE_INFINITY, "-1 / 0")
        testPositive(-2.0, "-1 +  -1")
        testPositive(-2.0, "(-1) + -1 / (1)")
    }

    @Test
    fun negativeTests() {
        testNegative("-1 +")
        testNegative(" ")
        testNegative("2 + --2")
        testNegative("x - 1")
        testNegative("5 // 2")
        testNegative("(5 + 2")
        testNegative("()")
        testNegative("1 + 2) - 5")
        testNegative(")(")
    }

    private fun testPositive(expected: Double, expression: String) {
        assertEquals(expected, evaluate(expression), 1e-4)
    }

    private fun testNegative(expression: String) {
        assertFailsWith<ExpressionException> { evaluate(expression) }
    }

}