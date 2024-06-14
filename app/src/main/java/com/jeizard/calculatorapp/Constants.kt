package com.jeizard.calculatorapp

object Constants {
    const val OPERATOR_ADD = '+'
    const val OPERATOR_SUBTRACT = '-'
    const val OPERATOR_MULTIPLY = '×'
    const val OPERATOR_DIVIDE = '÷'
    const val OPERATOR_POWER = '^'
    const val OPERATOR_DOT = '.'
    const val OPERATOR_OPEN_BRACKET = '('
    const val OPERATOR_CLOSE_BRACKET = ')'
    const val OPERATOR_PERCENT = '%'

    const val OPERATOR_DIVIDE_REGULAR = '/'
    const val OPERATOR_MULTIPLY_REGULAR = '*'

    const val ZERO = '0'
    const val ONE = '1'
    const val TWO = '2'
    const val THREE = '3'
    const val FOUR = '4'
    const val FIVE = '5'
    const val SIX = '6'
    const val SEVEN = '7'
    const val EIGHT = '8'
    const val NINE = '9'
    const val INFINITY = '∞'

    const val REPLACE_PERCENT = "%*"
    const val REPLACE_PERCENT_VALUE = "/100"

    const val REGEX_OPERATORS = """[-+*/()]"""
    const val REGEX_PERCENT = "%(?=\\d)"

    const val DECIMAL_FORMAT_PATTERN = "#.##########"

    const val EMPTY_STRING = ""
    const val STATE_RESULT_FIELD = "Result Field Text"

    const val ERROR_UNKNOWN_OPERATOR = "Unknown operator: "
    const val ERROR_NUMBER_LENGTH = "Число не может иметь больше 15 цифр"
    const val ERROR_DECIMAL_LENGTH = "Число не может иметь больше 10 цифр после точки"
    const val ERROR_ACTION_IMPOSSIBLE = "Действие невозможно"
    const val ERROR_DIVIDE_BY_ZERO = "На 0 делить нельзя!"
}
