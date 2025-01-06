package com.example.calculator

import android.os.Bundle
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var processTextView: TextView
    private lateinit var resultTextView: TextView
    private var currentNumber = ""
    private var currentOperation = ""
    private var firstNumber = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        processTextView = binding.processTextView
        resultTextView = binding.resultTextView

        val buttons = listOf(
            binding.Button0, binding.Button1, binding.Button2,
            binding.Button3, binding.Button4, binding.Button5,
            binding.Button6, binding.Button7, binding.Button8,
            binding.Button9, binding.ButtonAllClear,
            binding.ButtonToggleSign, binding.ButtonDivide,
            binding.ButtonBackspace, binding.ButtonMultiply,
            binding.ButtonMinus, binding.ButtonPlus,
            binding.ButtonEquls, binding.ButtonDot
        )

        buttons.forEach { button ->
            button.setOnClickListener { onButtonClick(button as AppCompatButton) }
        }
    }

    private fun onButtonClick(button: AppCompatButton) {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }

        // 按鈕動畫
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(50)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(50)
                    .start()
            }
            .start()

        when (button.text.toString()) {
            in "0".."9" -> handleNumber(button.text.toString())
            "." -> handleDecimal()
            "+" -> handleOperation("+")
            "-" -> handleOperation("-")
            "*" -> handleOperation("*")
            "/" -> handleOperation("/")
            "=" -> calculateResult()
            "AC" -> clearAll()
            "⌫" -> handleBackspace()
            "+/-" -> toggleSign()
        }
        updateDisplay()
    }

    private fun handleNumber(num: String) {
        currentNumber += num
    }

    private fun handleDecimal() {
        if (!currentNumber.contains(".")) {
            currentNumber += if (currentNumber.isEmpty()) "0." else "."
        }
    }

    private fun handleOperation(op: String) {
        if (currentNumber.isNotEmpty()) {
            if (currentOperation.isEmpty()) {
                firstNumber = currentNumber.toDouble()
                currentOperation = op
                currentNumber = ""
            } else {
                calculateResult()
                currentOperation = op
            }
        }
    }

    private fun calculateResult() {
        if (currentNumber.isNotEmpty() && currentOperation.isNotEmpty()) {
            val secondNumber = currentNumber.toDouble()
            val result = when (currentOperation) {
                "+" -> firstNumber + secondNumber
                "-" -> firstNumber - secondNumber
                "*" -> firstNumber * secondNumber
                "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.POSITIVE_INFINITY
                else -> secondNumber
            }

            firstNumber = result
            currentNumber = formatNumber(result)
            currentOperation = ""
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toInt().toString()
        } else {
            number.toString()
        }
    }

    private fun clearAll() {
        currentNumber = ""
        currentOperation = ""
        firstNumber = 0.0
    }

    private fun handleBackspace() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
        }
    }

    private fun toggleSign() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = if (currentNumber.startsWith("-")) {
                currentNumber.substring(1)
            } else {
                "-$currentNumber"
            }
        }
    }

    private fun updateDisplay() {
        val process = when {
            currentOperation.isEmpty() -> formatNumber(currentNumber.toDoubleOrNull() ?: 0.0)
            currentNumber.isEmpty() -> "${formatNumber(firstNumber)} $currentOperation"
            else -> "${formatNumber(firstNumber)} $currentOperation ${formatNumber(currentNumber.toDouble())}"
        }

        processTextView.text = process
        resultTextView.text = if (currentNumber.isEmpty()) "0" else currentNumber
    }
}

