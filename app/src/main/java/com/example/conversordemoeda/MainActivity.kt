package com.example.conversordemoeda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conversordemoeda.model.Asset
import com.example.conversordemoeda.model.Currency
import com.example.conversordemoeda.ui.AssetAdapter
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var assetAdapter: AssetAdapter
    private lateinit var rvAssets: RecyclerView
    private lateinit var currency: String
    private lateinit var amount: BigDecimal

    val initialAssets = listOf(
        Asset(Currency.BTC, BigDecimal("0.5000")),
        Asset(Currency.USD, BigDecimal("50000.00")),
        Asset(Currency.BRL, BigDecimal("100000.00"))
    )

    private val convertLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val code = data.getStringExtra("extra_result_code") ?: return@registerForActivityResult
            val amt  = data.getStringExtra("extra_result_amt")  ?: return@registerForActivityResult
            val amtInput  = data.getStringExtra("extra_converted_amt")  ?: return@registerForActivityResult

            val converted = amt.toBigDecimalOrNull() ?: return@registerForActivityResult
            val amtConverted = amtInput.toBigDecimalOrNull() ?: return@registerForActivityResult

            val asset = initialAssets.find { it.currency.name == code }
            if (asset != null) {
                asset.amount = asset.amount.add(converted)
                assetAdapter.notifyDataSetChanged()
            }

            val assetInitial = initialAssets.find { it.currency.name == currency }
            if (assetInitial != null) {
                assetInitial.amount = assetInitial.amount.subtract(amtConverted)
                assetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvAssets = findViewById(R.id.rvAssets)

        assetAdapter = AssetAdapter(initialAssets) { asset ->
            val intent = Intent(this, ConvertActivity::class.java).apply {
                currency =  asset.currency.name
                amount = asset.amount
                putExtra("extra_currency", asset.currency.name)
                putExtra("extra_amount",   asset.amount.toPlainString())
            }
            convertLauncher.launch(intent)
        }
        rvAssets.adapter = assetAdapter
        rvAssets.layoutManager = LinearLayoutManager(this)

    }
}