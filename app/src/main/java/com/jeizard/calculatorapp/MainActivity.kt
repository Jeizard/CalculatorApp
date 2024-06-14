package com.jeizard.calculatorapp

import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jeizard.calculatorapp.databinding.ActivityMainBinding

import com.jeizard.calculatorapp.Constants.OPERATOR_ADD
import com.jeizard.calculatorapp.Constants.OPERATOR_SUBTRACT
import com.jeizard.calculatorapp.Constants.OPERATOR_MULTIPLY
import com.jeizard.calculatorapp.Constants.OPERATOR_DIVIDE
import com.jeizard.calculatorapp.Constants.OPERATOR_POWER
import com.jeizard.calculatorapp.Constants.OPERATOR_DOT
import com.jeizard.calculatorapp.Constants.OPERATOR_OPEN_BRACKET
import com.jeizard.calculatorapp.Constants.OPERATOR_CLOSE_BRACKET
import com.jeizard.calculatorapp.Constants.OPERATOR_PERCENT
import com.jeizard.calculatorapp.Constants.ZERO
import com.jeizard.calculatorapp.Constants.ONE
import com.jeizard.calculatorapp.Constants.TWO
import com.jeizard.calculatorapp.Constants.THREE
import com.jeizard.calculatorapp.Constants.FOUR
import com.jeizard.calculatorapp.Constants.FIVE
import com.jeizard.calculatorapp.Constants.SIX
import com.jeizard.calculatorapp.Constants.SEVEN
import com.jeizard.calculatorapp.Constants.EIGHT
import com.jeizard.calculatorapp.Constants.NINE
import com.jeizard.calculatorapp.Constants.REPLACE_PERCENT
import com.jeizard.calculatorapp.Constants.REPLACE_PERCENT_VALUE
import com.jeizard.calculatorapp.Constants.OPERATOR_DIVIDE_REGULAR
import com.jeizard.calculatorapp.Constants.OPERATOR_MULTIPLY_REGULAR
import com.jeizard.calculatorapp.Constants.REGEX_OPERATORS
import com.jeizard.calculatorapp.Constants.REGEX_PERCENT
import com.jeizard.calculatorapp.Constants.EMPTY_STRING
import com.jeizard.calculatorapp.Constants.ERROR_ACTION_IMPOSSIBLE
import com.jeizard.calculatorapp.Constants.ERROR_DECIMAL_LENGTH
import com.jeizard.calculatorapp.Constants.ERROR_DIVIDE_BY_ZERO
import com.jeizard.calculatorapp.Constants.ERROR_NUMBER_LENGTH
import com.jeizard.calculatorapp.Constants.INFINITY
import com.jeizard.calculatorapp.Constants.STATE_RESULT_FIELD

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var entryText: Editable

    private val operators = listOf(OPERATOR_ADD, OPERATOR_SUBTRACT, OPERATOR_MULTIPLY,
        OPERATOR_DIVIDE, OPERATOR_POWER, OPERATOR_DOT, OPERATOR_OPEN_BRACKET, OPERATOR_PERCENT
    )

    private val calculator = Calculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.entryField.setShowSoftInputOnFocus(false)
        binding.entryField.setSelection(binding.entryField.text.length)
        binding.entryField.requestFocus()
        entryText = binding.entryField.text
        setButtonsClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_RESULT_FIELD, binding.resultField.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.resultField.text = savedInstanceState.getString(STATE_RESULT_FIELD)
        entryText = binding.entryField.text
    }

    private fun setButtonsClickListeners() {
        val buttons = listOf(
            binding.btnBack, binding.btnClear, binding.btnResult,
            binding.btnAdd, binding.btnSubtract, binding.btnMultiply,
            binding.btnDivision, binding.btnPercent,
            binding.btnDot, binding.btnSign, binding.btnBracket,
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        fun appendSymbol(symbol: Char) {
            entryText.insert(binding.entryField.selectionStart, symbol.toString())
        }

        fun appendNumber(number: Char) {
            val str = entryText.toString().substring(0, binding.entryField.selectionStart)

            val lastNumber = str.split(Regex(REGEX_OPERATORS)).lastOrNull()
            if (lastNumber != null && lastNumber == ZERO.toString()) {
                entryText.delete(binding.entryField.selectionStart - 1, binding.entryField.selectionStart)
            }

            val firstNumberAfterCursor = entryText.toString().substring(binding.entryField.selectionStart).split(Regex(REGEX_OPERATORS)).firstOrNull()
            val selectedNumber = (lastNumber ?: EMPTY_STRING) + (firstNumberAfterCursor ?: EMPTY_STRING)

            if(selectedNumber.length >= 15) {
                Toast.makeText(this, ERROR_NUMBER_LENGTH, Toast.LENGTH_SHORT).show()
            }
            else if(selectedNumber.contains(OPERATOR_DOT) && selectedNumber.substringAfter(
                    OPERATOR_DOT, EMPTY_STRING).length >= 10) {
                Toast.makeText(this, ERROR_DECIMAL_LENGTH, Toast.LENGTH_SHORT).show()
            }
            else if(number == ZERO){
                val isCursorAfterOperator = (binding.entryField.selectionStart > 0 &&
                        operators.any { entryText.getOrNull(binding.entryField.selectionStart - 1) == it && it != OPERATOR_DOT }) ||
                        binding.entryField.selectionStart == 0
                val isCursorBeforeDigit = binding.entryField.selectionStart < entryText.length &&
                        entryText[binding.entryField.selectionStart].isDigit()

                if (!isCursorAfterOperator || !isCursorBeforeDigit) {
                    appendSymbol(number)
                }
                else{
                    Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                }
            }
            else {
                appendSymbol(number)
            }
            calculateResult()
        }

        fun appendOperator(operator: Char) {
            val str = entryText.toString().substring(0, binding.entryField.selectionStart)
            if (str.isNotEmpty()) {
                if (operators.any { str.endsWith(it) }) {
                    val lastIndex = str.length - 1
                    val isFirstElement = lastIndex == 0
                    val isPreviousCharOpenBracket = str.getOrNull(lastIndex - 1) == OPERATOR_OPEN_BRACKET

                    if (!isFirstElement && !isPreviousCharOpenBracket) {
                        entryText.delete(binding.entryField.selectionStart - 1, binding.entryField.selectionStart)
                    } else {
                        return
                    }
                }
                val newStr = entryText.toString().substring(0, binding.entryField.selectionStart)
                if (newStr.isNotEmpty()) {
                    appendSymbol(operator)
                    calculateResult()
                }
                else{
                    Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
            }
        }

        buttons.forEach { it.setOnClickListener {view ->
            when(view.id) {
                binding.btnResult.id -> {
                    if(binding.resultField.text.toString().toDoubleOrNull() != null){
                        binding.entryField.setText(binding.resultField.text.toString())
                        binding.entryField.setSelection(binding.entryField.text.length)
                        entryText = binding.entryField.text
                        binding.resultField.text = EMPTY_STRING
                    }
                    else{
                        Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                    }
                }

                binding.btn0.id -> appendNumber(ZERO)
                binding.btn1.id -> appendNumber(ONE)
                binding.btn2.id -> appendNumber(TWO)
                binding.btn3.id -> appendNumber(THREE)
                binding.btn4.id -> appendNumber(FOUR)
                binding.btn5.id -> appendNumber(FIVE)
                binding.btn6.id -> appendNumber(SIX)
                binding.btn7.id -> appendNumber(SEVEN)
                binding.btn8.id -> appendNumber(EIGHT)
                binding.btn9.id -> appendNumber(NINE)

                binding.btnBack.id -> {
                    if (entryText.isNotEmpty() && binding.entryField.selectionStart != 0) {
                        var previousNumber = EMPTY_STRING
                        var nextNumber = EMPTY_STRING

                        val previousStr = entryText.toString().substring(0, binding.entryField.selectionStart - 1)
                        val lastPreviousChar = previousStr.lastOrNull()
                        if(lastPreviousChar != null && lastPreviousChar.isDigit()) {
                            previousNumber = if(previousStr.split(Regex(REGEX_OPERATORS)).last() != EMPTY_STRING){
                                previousStr.split(Regex(REGEX_OPERATORS)).last()
                            } else{
                                previousStr
                            }
                        }

                        val nextStr = entryText.toString().substring(binding.entryField.selectionStart)
                        val nextPreviousChar = previousStr.firstOrNull()
                        if(nextPreviousChar != null && nextPreviousChar.isDigit()) {
                            nextNumber = if(nextStr.split(Regex(REGEX_OPERATORS)).first() != EMPTY_STRING){
                                nextStr.split(Regex(REGEX_OPERATORS)).first()
                            } else{
                                nextStr
                            }
                        }

                        val selectedNumber = previousNumber + nextNumber
                        if(selectedNumber.length >= 15) {
                            Toast.makeText(this, ERROR_NUMBER_LENGTH, Toast.LENGTH_SHORT).show()
                        }
                        else if(selectedNumber.contains(OPERATOR_DOT) && selectedNumber.substringAfter(
                                OPERATOR_DOT, EMPTY_STRING).length >= 10) {
                            Toast.makeText(this, ERROR_DECIMAL_LENGTH, Toast.LENGTH_SHORT).show()
                        }
                        else {
                            entryText.delete(
                                binding.entryField.selectionStart - 1,
                                binding.entryField.selectionStart
                            )

                            while (true) {
                                val nextFirstChar =
                                    entryText.getOrNull(binding.entryField.selectionStart)
                                val nextSecondChar =
                                    entryText.getOrNull(binding.entryField.selectionStart + 1)

                                if (binding.entryField.selectionStart == 0) {
                                    if (nextFirstChar != null && (operators.contains(nextFirstChar) ||
                                                (nextSecondChar != null && nextFirstChar == ZERO && nextSecondChar.isDigit()))
                                    ) {
                                        entryText.delete(
                                            binding.entryField.selectionStart,
                                            binding.entryField.selectionStart + 1
                                        )
                                    } else {
                                        break
                                    }
                                } else {
                                    val prevChar =
                                        entryText.getOrNull(binding.entryField.selectionStart - 1)
                                    if (nextFirstChar != null && nextFirstChar == OPERATOR_DOT && prevChar != null && operators.contains(prevChar)) {
                                        entryText.delete(
                                            binding.entryField.selectionStart,
                                            binding.entryField.selectionStart + 1
                                        )
                                    } else {
                                        if (prevChar != null && nextFirstChar != null &&
                                            operators.contains(prevChar) && operators.contains(
                                                nextFirstChar
                                            )
                                        ) {
                                            entryText.delete(
                                                binding.entryField.selectionStart - 1,
                                                binding.entryField.selectionStart
                                            )
                                        } else if (prevChar != null && operators.contains(prevChar) &&
                                            nextSecondChar != null && nextFirstChar == ZERO && nextSecondChar.isDigit()
                                        ) {
                                            entryText.delete(
                                                binding.entryField.selectionStart,
                                                binding.entryField.selectionStart + 1
                                            )
                                        } else {
                                            break
                                        }
                                    }
                                }
                            }

                            var nearestOperatorIndex =
                                entryText.substring(binding.entryField.selectionStart)
                                    .indexOfFirst { operators.contains(it) && it != OPERATOR_DOT }
                            val nearestDotIndex =
                                entryText.substring(binding.entryField.selectionStart)
                                    .indexOf(OPERATOR_DOT)
                            if (nearestOperatorIndex == -1) {
                                nearestOperatorIndex = entryText.lastIndex
                            }
                            if (nearestDotIndex != -1 && nearestDotIndex < nearestOperatorIndex) {
                                val str =
                                    entryText.toString()
                                        .substring(0, binding.entryField.selectionStart)
                                val lastNumber = str.split(Regex(REGEX_OPERATORS)).last()
                                if (lastNumber.contains(OPERATOR_DOT)) {
                                    entryText.delete(
                                        binding.entryField.selectionStart + nearestDotIndex,
                                        binding.entryField.selectionStart + nearestDotIndex + 1
                                    )
                                }
                            }
                            calculateResult()
                        }
                    }
                }
                binding.btnClear.id -> {
                    entryText.delete(0, entryText.length)
                    binding.resultField.text = EMPTY_STRING
                }
                binding.btnAdd.id -> appendOperator(OPERATOR_ADD)
                binding.btnSubtract.id -> {
                    val str = entryText.toString().substring(0, binding.entryField.selectionStart)
                    if (operators.any { str.endsWith(it) }) {
                        if(!(str.endsWith(OPERATOR_OPEN_BRACKET))){
                            entryText.delete(
                                binding.entryField.selectionStart - 1,
                                binding.entryField.selectionStart
                            )
                        }
                    }
                    appendSymbol(OPERATOR_SUBTRACT)
                    calculateResult()
                }
                binding.btnMultiply.id -> appendOperator(OPERATOR_MULTIPLY)
                binding.btnDivision.id -> appendOperator(OPERATOR_DIVIDE)
                binding.btnPercent.id -> appendOperator(OPERATOR_PERCENT)
                binding.btnDot.id -> {
                    var nearestOperatorIndex = entryText.substring(binding.entryField.selectionStart)
                        .indexOfFirst { operators.contains(it) && it != OPERATOR_DOT}
                    val nearestDotIndex = entryText.substring(binding.entryField.selectionStart).indexOf(OPERATOR_DOT)
                    if(nearestOperatorIndex == -1){
                        nearestOperatorIndex = entryText.lastIndex
                    }
                    if(!(nearestDotIndex != -1 && nearestDotIndex < nearestOperatorIndex)) {
                        val firstNumberAfterCursor = entryText.toString().substring(binding.entryField.selectionStart).split(Regex(REGEX_OPERATORS)).firstOrNull()
                        if(firstNumberAfterCursor != null && firstNumberAfterCursor.length >= 10){
                            Toast.makeText(this, ERROR_DECIMAL_LENGTH, Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val str =
                                entryText.toString().substring(0, binding.entryField.selectionStart)
                            if ((str.isEmpty() || operators.any { str.endsWith(it) }) && !str.endsWith(
                                    OPERATOR_DOT
                                )
                            ) {
                                entryText.insert(
                                    binding.entryField.selectionStart,
                                    "$ZERO$OPERATOR_DOT"
                                )
                            } else {
                                val lastNumber = str.split(Regex(REGEX_OPERATORS)).last()
                                if (!lastNumber.contains(OPERATOR_DOT)) {
                                    appendSymbol(OPERATOR_DOT)
                                } else {
                                    Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            calculateResult()
                        }
                    }
                    else{
                        Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                    }
                }
                binding.btnBracket.id -> {
                    val str = entryText.toString().substring(0, binding.entryField.selectionStart)
                    val selectionStart = binding.entryField.selectionStart
                    val isBetweenDigits = selectionStart > 0 &&
                            selectionStart < entryText.length &&
                            entryText[selectionStart - 1].isDigit() &&
                            entryText[selectionStart].isDigit()

                    if (!isBetweenDigits) {
                        if (str.isEmpty() || operators.any { str.endsWith(it) && it != OPERATOR_DOT }) {
                            appendSymbol(OPERATOR_OPEN_BRACKET)
                        } else if (str[str.lastIndex] != OPERATOR_OPEN_BRACKET &&
                            entryText.count { it == OPERATOR_OPEN_BRACKET } > entryText.count { it == OPERATOR_CLOSE_BRACKET }) {
                            if (selectionStart == entryText.length ||
                                (selectionStart < entryText.length && entryText[selectionStart] != OPERATOR_DOT) &&
                                !str.endsWith(OPERATOR_DOT)) {
                                appendSymbol(OPERATOR_CLOSE_BRACKET)
                                calculateResult()
                            }
                            else{
                                Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this, ERROR_ACTION_IMPOSSIBLE, Toast.LENGTH_SHORT).show()
                    }
                }
                binding.btnSign.id -> {
                    if (entryText.isEmpty()) {
                        entryText.insert(binding.entryField.selectionStart,
                            OPERATOR_OPEN_BRACKET.toString() + OPERATOR_SUBTRACT.toString()
                        )
                    }
                    else if (entryText.toString() == (OPERATOR_OPEN_BRACKET.toString() + OPERATOR_SUBTRACT.toString())){
                        binding.entryField.setText(EMPTY_STRING)
                    }
                    else {
                        val str = entryText.toString().substring(0, binding.entryField.selectionStart)
                        if(str.endsWith(OPERATOR_OPEN_BRACKET.toString() + OPERATOR_SUBTRACT.toString())){
                            entryText.delete(
                                binding.entryField.selectionStart - 2,
                                binding.entryField.selectionStart
                            )
                        }
                        else if(operators.any { str.endsWith(it) }){
                            entryText.insert(binding.entryField.selectionStart, OPERATOR_OPEN_BRACKET.toString() + OPERATOR_SUBTRACT.toString())
                        }
                        else {
                            val lastNumber = str.split(Regex(REGEX_OPERATORS)).lastOrNull()
                            if (!lastNumber.isNullOrBlank()) {
                                val lastIndex = str.lastIndexOf(lastNumber)
                                if (lastIndex > 0 && str[lastIndex - 1] == OPERATOR_SUBTRACT) {
                                    if (entryText.count { it == OPERATOR_OPEN_BRACKET } > entryText.count { it == OPERATOR_CLOSE_BRACKET }) {
                                        entryText.delete(
                                            lastIndex - 2,
                                            lastIndex
                                        )
                                    }
                                } else {
                                    entryText.insert(lastIndex, OPERATOR_OPEN_BRACKET.toString() + OPERATOR_SUBTRACT.toString())
                                }
                            }
                        }
                    }
                    calculateResult()
                }
            }
        }}
    }

    private fun processEntryText(entryText: String): String {
        var processedText = entryText
        if (processedText.isNotEmpty()) {
            processedText = processedText.replace(Regex(REGEX_PERCENT), REPLACE_PERCENT)
            processedText = processedText.replace(OPERATOR_PERCENT.toString(), REPLACE_PERCENT_VALUE)

            while (processedText.isNotEmpty() && operators.contains(processedText.last())) {
                processedText = processedText.dropLast(1)
            }

            processedText = processedText.replace(OPERATOR_DIVIDE.toString(), OPERATOR_DIVIDE_REGULAR.toString())
            processedText = processedText.replace(OPERATOR_MULTIPLY.toString(), OPERATOR_MULTIPLY_REGULAR.toString())
        }
        return processedText
    }

    private fun calculateResult() {
        val entryText = binding.entryField.text.toString()
        val processedExpression = processEntryText(entryText)
        val result = calculator.calculate(processedExpression)
        if(result == INFINITY.toString()){
            binding.resultField.text = ERROR_DIVIDE_BY_ZERO
        }
        else{
            binding.resultField.text = result
        }
    }
}