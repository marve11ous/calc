package calc

import kotlin.system.exitProcess

/*
Create a simple calculator that given a string of operators (), +, -, *, / and numbers separated by spaces
returns the value of that expression

Example: evaluate("1 + 1") => 2
*/

fun main() {
    println(
        "Calculate the value of expression.\n" +
                "Operators (), +, -, *, / and numbers separated by spaces.\n" +
                "Enter 'exit' to finish."
    )
    while (true) {
        print("Enter expression: ")
        val input = readLine()
        if (input == "exit") {
            exitProcess(0)
        }
        try {
            println("Answer: " + evaluate(input!!))
        } catch (e: Exception) {
            println("Error: " + e.message)
        }
    }
}

private val INNER_EXPRESSION_REGEX = Regex("\\(([0-9-+*/.\\s]+)\\)")
private val OPERATION_EXPRESSION_REGEX = listOf(getOperationRegex("*/"), getOperationRegex("+-"))
private val SEPARATOR_REGEX = Regex("\\s+")

private const val OPERATION_PLACEHOLDER = "{OP}"
private const val OPERATION_EXPRESSION_PATTERN_TEMPLATE = "([0-9.]+\\s+[$OPERATION_PLACEHOLDER]\\s+)+[0-9.]+"
private fun getOperationRegex(operators: String) =
    Regex(OPERATION_EXPRESSION_PATTERN_TEMPLATE.replace(OPERATION_PLACEHOLDER, operators))

class ExpressionException(msg: String) : RuntimeException(msg)

fun evaluate(str: String): Double {
    var expression = str.trim()
    var simpleExpressions: Sequence<MatchResult> = emptySequence()
    while ({ simpleExpressions = INNER_EXPRESSION_REGEX.findAll(expression); simpleExpressions }().any()) {
        simpleExpressions.forEach { matchResult ->
            expression = expression.replace(
                matchResult.groupValues[0],
                parseSimple(matchResult.groupValues[1])
            )
        }
    }
    return calculate(parseSimple(expression))
}

private fun parseSimple(str: String): String {
    var expression = str.trim()
    OPERATION_EXPRESSION_REGEX.forEach { regex ->
        if (expression.toDoubleOrNull() != null) {
            return expression
        }

        val operationExpressions = regex.findAll(expression)
        operationExpressions.forEach { matchResult ->
            expression =
                expression.replace(
                    matchResult.groupValues[0],
                    calculate(matchResult.groupValues[0]).toString()
                )
        }
    }
    return expression
}

private fun calculate(str: String): Double {
    var result = str.toDoubleOrNull()
    if (result != null) {
        return result
    }
    val iterator = str.split(SEPARATOR_REGEX).iterator()
    validate(iterator.hasNext())
    result = iterator.next().toDoubleOrNull()
    validate(result != null)
    while (iterator.hasNext()) {
        result = calculateNextSimple(result!!, iterator)
    }
    return result!!
}

private fun calculateNextSimple(result: Double, iterator: Iterator<String>): Double {
    validate(iterator.hasNext())
    val operator = iterator.next()
    validate(iterator.hasNext())
    val operand = iterator.next().toDoubleOrNull()
    validate(operand != null)
    return when (operator) {
        "*" -> result * operand!!
        "/" -> result / operand!!
        "+" -> result + operand!!
        "-" -> result - operand!!
        else -> throw ExpressionException("Unsupported operator")
    }
}

private fun validate(isValid: Boolean) {
    if (!isValid) {
        throw ExpressionException("Incorrect expression")
    }
}
