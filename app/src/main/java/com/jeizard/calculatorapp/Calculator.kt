package com.jeizard.calculatorapp

import com.jeizard.calculatorapp.Constants.DECIMAL_FORMAT_PATTERN
import com.jeizard.calculatorapp.Constants.EMPTY_STRING
import com.jeizard.calculatorapp.Constants.OPERATOR_ADD
import com.jeizard.calculatorapp.Constants.OPERATOR_CLOSE_BRACKET
import com.jeizard.calculatorapp.Constants.OPERATOR_DIVIDE_REGULAR
import com.jeizard.calculatorapp.Constants.OPERATOR_DOT
import com.jeizard.calculatorapp.Constants.OPERATOR_MULTIPLY_REGULAR
import com.jeizard.calculatorapp.Constants.OPERATOR_OPEN_BRACKET
import com.jeizard.calculatorapp.Constants.OPERATOR_SUBTRACT
import com.jeizard.calculatorapp.Constants.ERROR_UNKNOWN_OPERATOR
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack

class Calculator {
    fun calculate(expression: String): String {
        try {
            if (expression.isEmpty()) return EMPTY_STRING

            val rpn = convertToRPN(expression)
            val result = evaluateRPN(rpn)

            return if (result.isWholeNumber()) {
                result.toInt().toString()
            } else {
                DecimalFormat(DECIMAL_FORMAT_PATTERN, DecimalFormatSymbols(Locale.US)).format(result)
            }
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    private fun convertToRPN(expression: String): List<String> {
        val rpn = mutableListOf<String>()
        val operators = Stack<Char>()
        var number = EMPTY_STRING
        var lastCharWasOperator = true

        fun processNumber() {
            if (number.isNotEmpty()) {
                rpn.add(number)
                number = EMPTY_STRING
            }
        }

        for (char in expression) {
            when {
                char.isDigit() || char == OPERATOR_DOT -> {
                    number += char
                    lastCharWasOperator = false
                }
                char == OPERATOR_SUBTRACT && (lastCharWasOperator && (operators.isEmpty() || operators.peek() == OPERATOR_OPEN_BRACKET)) -> {
                    number += char
                    lastCharWasOperator = false
                }
                char == OPERATOR_OPEN_BRACKET -> {
                    processNumber()
                    operators.push(char)
                    lastCharWasOperator = true
                }
                char == OPERATOR_CLOSE_BRACKET -> {
                    processNumber()
                    while (operators.isNotEmpty() && operators.peek() != OPERATOR_OPEN_BRACKET) {
                        rpn.add(operators.pop().toString())
                    }
                    operators.pop()
                    lastCharWasOperator = false
                }
                char in (OPERATOR_ADD.toString() + OPERATOR_SUBTRACT + OPERATOR_MULTIPLY_REGULAR + OPERATOR_DIVIDE_REGULAR) -> {
                    processNumber()
                    while (operators.isNotEmpty() && precedence(char) <= precedence(operators.peek())) {
                        rpn.add(operators.pop().toString())
                    }
                    operators.push(char)
                    lastCharWasOperator = true
                }
            }
        }
        processNumber()
        while (operators.isNotEmpty()) {
            rpn.add(operators.pop().toString())
        }
        return rpn
    }

    private fun precedence(op: Char): Int {
        return when (op) {
            OPERATOR_ADD, OPERATOR_SUBTRACT -> 1
            OPERATOR_MULTIPLY_REGULAR, OPERATOR_DIVIDE_REGULAR -> 2
            else -> 0
        }
    }

    private fun evaluateRPN(rpn: List<String>): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            when {
                token.toDoubleOrNull() != null -> stack.push(token.toDouble())
                token in (OPERATOR_ADD.toString() + OPERATOR_SUBTRACT + OPERATOR_MULTIPLY_REGULAR + OPERATOR_DIVIDE_REGULAR) -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(when (token) {
                        OPERATOR_ADD.toString() -> a + b
                        OPERATOR_SUBTRACT.toString() -> a - b
                        OPERATOR_MULTIPLY_REGULAR.toString() -> a * b
                        OPERATOR_DIVIDE_REGULAR.toString() -> a / b
                        else -> throw IllegalArgumentException("$ERROR_UNKNOWN_OPERATOR $token")
                    })
                }
            }
        }
        return stack.pop()
    }

    private fun Double.isWholeNumber(): Boolean {
        return this.isFinite() && this == this.toInt().toDouble()
    }
}
