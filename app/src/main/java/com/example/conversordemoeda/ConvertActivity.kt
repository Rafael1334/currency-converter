package com.example.conversordemoeda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import java.math.BigDecimal
import com.example.conversordemoeda.model.Currency
import com.example.conversordemoeda.repository.CurrencyRepository
import kotlinx.coroutines.launch

class ConvertActivity : AppCompatActivity() {
    private lateinit var btnConvert: Button
    private lateinit var btnVoltar: Button

    companion object {
        private const val EXTRA_CURRENCY = "extra_currency"
        private const val EXTRA_AMOUNT = "extra_amount"
        private const val EXTRA_RESULT_CODE = "extra_result_code"
        private const val EXTRA_RESULT_AMT  = "extra_result_amt"
        private const val EXTRA_CONVERTED_AMT  = "extra_converted_amt"

        fun start(context: Context, currency: Currency, amount: BigDecimal) {
            val intent = Intent(context, ConvertActivity::class.java).apply {
                putExtra(EXTRA_CURRENCY, currency.name)
                putExtra(EXTRA_AMOUNT, amount.toPlainString())
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_convert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var initialAmount:BigDecimal = BigDecimal.ZERO
        val spOrigin    = findViewById<TextView>(R.id.spOrigin)
        val spDest      = findViewById<Spinner>(R.id.spDestination)
        val etAmount    = findViewById<EditText>(R.id.etAmount)
        btnConvert      = findViewById<Button>(R.id.btnDoConvert)
        btnVoltar      = findViewById<Button>(R.id.btnVolter)
        val progress    = findViewById<ProgressBar>(R.id.progress)

        intent.getStringExtra(EXTRA_CURRENCY)?.let { code ->
            spOrigin.text = code
            etAmount.setText(intent.getStringExtra(EXTRA_AMOUNT))
            initialAmount = BigDecimal(intent.getStringExtra(EXTRA_AMOUNT))
        }

        val codes = Currency.entries.filter { it.code != spOrigin.text.toString() }.map { it.code }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, codes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDest.adapter   = adapter


        btnConvert.setOnClickListener {
            val fromCode = spOrigin.text as String
            val toCode   = spDest.selectedItem   as String
            val text     = etAmount.text.toString().trim()
            val amount = try {
                BigDecimal(text)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (text.isEmpty()) {
                Toast.makeText(this, "Digite um valor para converter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (initialAmount < amount) {
                Toast.makeText(this, "Você não tem dinheiro suficiente , vá trabalhar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            progress.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val result = CurrencyRepository
                        .convert(Currency.valueOf(fromCode), Currency.valueOf(toCode), amount)
                    Toast
                        .makeText(this@ConvertActivity,
                            "Resultado: $result ${toCode}",
                            Toast.LENGTH_LONG)
                        .show()

                    val data = Intent().apply {
                        putExtra(EXTRA_RESULT_CODE, toCode)
                        putExtra(EXTRA_RESULT_AMT,  result.toPlainString())
                        putExtra(EXTRA_CONVERTED_AMT, amount.toPlainString())
                    }
                    setResult(RESULT_OK, data)
                    finish()

                } catch (e: Exception) {
                    Toast
                        .makeText(this@ConvertActivity,
                            "Erro ao converter: ${e.message}",
                            Toast.LENGTH_LONG)
                        .show()
                } finally {
                    progress.visibility = View.GONE
                }
            }
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}