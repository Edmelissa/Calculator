package com.example.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.calculator.databinding.ActivityMainBinding
import java.math.BigInteger

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var action = Operation.NONE

    private var symbolAdditional = ' '
    private var symbolSubtraction = ' '
    private var symbolDivision = ' '
    private var symbolMultiplication = ' '
    private var symbolEqual = ' '

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        symbolAdditional = getString(R.string.button_addition_name)[0]
        symbolSubtraction = getString(R.string.button_subtraction_name)[0]
        symbolDivision = getString(R.string.button_division_name)[0]
        symbolMultiplication = getString(R.string.button_multiplication_name)[0]
        symbolEqual = getString(R.string.button_equals_name)[0]

        with(binding) {
            buttonAddition.setOnClickListener {
                action = Operation.ADD
                changeOperation(binding, it)
            }

            buttonSubtraction.setOnClickListener {
                action = Operation.SUB
                changeOperation(binding, it)
            }

            buttonDivision.setOnClickListener {
                action = Operation.DIV
                changeOperation(binding, it)
            }

            buttonMultiplication.setOnClickListener {
                action = Operation.MUL
                changeOperation(binding, it)
            }

            buttonEquals.setOnClickListener {
                changeResultNumber(binding)
            }

            buttonAbout.setOnClickListener {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        with(binding) {
            outState.putAll(
                bundleOf(
                    NUMBER1_KEY to editTextNumber1.text.toString(),
                    NUMBER2_KEY to editTextNumber2.text.toString(),
                    RESULT_NUMBER_KEY to textViewResultNumber.text.toString(),
                    OPERATION_KEY to action.symbol
                )
            )
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        with(binding){
            editTextNumber1.setText(savedInstanceState.getString(NUMBER1_KEY) ?: "")
            editTextNumber2.setText(savedInstanceState.getString(NUMBER2_KEY) ?: "")
            textViewResultNumber.text = savedInstanceState.getString(RESULT_NUMBER_KEY) ?: ""

            changeColorActiveButton(binding, savedInstanceState.getChar(OPERATION_KEY))
        }

    }

    private fun clearAllColor(binding : ActivityMainBinding){
        with(binding){
            buttonAddition.setBackgroundColor(getColor(R.color.pink))
            buttonDivision.setBackgroundColor(getColor(R.color.pink))
            buttonSubtraction.setBackgroundColor(getColor(R.color.pink))
            buttonMultiplication.setBackgroundColor(getColor(R.color.pink))
        }
    }

    private fun changeOperation(binding : ActivityMainBinding, it : View){
        clearAllColor(binding)
        it.setBackgroundColor(getColor(R.color.blue))
    }

    private fun changeResultNumber(binding : ActivityMainBinding){
        val number1 = binding.editTextNumber1.text.toString().toBigIntegerOrNull()
        val number2 = binding.editTextNumber2.text.toString().toBigIntegerOrNull()

        var result = number1.toString() + action.symbol + number2.toString() + symbolEqual

        if(number1 != null && number2 != null) {
            if(action.symbol != ' ') {
                if(action.symbol != '/' || (action.symbol == '/' && number2 != 0.toBigInteger())) {
                    result += action.operation(number1, number2).toString()
                }
                else{
                    result = getString(R.string.error_division_by_zero)
                }
            }
            else{
                result = getString(R.string.error_operation)
            }
        }
        else if(number1 == null && number2 == null){
            result = getString(R.string.error_operands)
        }
        else if(number1 == null){
            result = getString(R.string.error_operand1)
        }
        else {
            result = getString(R.string.error_operand2)
        }

        binding.textViewResultNumber.text = result
    }

    private fun changeColorActiveButton(binding : ActivityMainBinding, symbol : Char){
        with(binding) {
            when (symbol) {
                symbolAdditional -> {
                    buttonAddition.setBackgroundColor(getColor(R.color.blue))
                    action = Operation.ADD
                }
                symbolMultiplication -> {
                    buttonMultiplication.setBackgroundColor(getColor(R.color.blue))
                    action = Operation.MUL
                }
                symbolDivision -> {
                    buttonDivision.setBackgroundColor(getColor(R.color.blue))
                    action = Operation.DIV
                }
                symbolSubtraction -> {
                    buttonSubtraction.setBackgroundColor(getColor(R.color.blue))
                    action = Operation.SUB
                }
            }
        }
    }

    enum class Operation(
        val operation : (a: BigInteger, b: BigInteger) -> BigInteger,
        val symbol: Char
    ){
        ADD(operation = { a, b -> a.add(b)}, symbol = '+'),
        SUB(operation = { a, b -> a.subtract(b)}, symbol = '-'),
        MUL(operation = { a, b -> a.multiply(b)}, symbol = '*'),
        DIV(operation = { a, b -> a.divide(b)}, symbol = '/'),
        NONE({ _, _ -> BigInteger.ZERO}, symbol = ' ')
    }

    companion object{
        const val RESULT_NUMBER_KEY = "resultNumber"
        const val NUMBER1_KEY = "number1"
        const val NUMBER2_KEY = "number2"
        const val OPERATION_KEY = "operation"
    }
}