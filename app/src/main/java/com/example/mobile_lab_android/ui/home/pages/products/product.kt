package com.example.mobile_lab_android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_lab_android.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val productName = intent.getStringExtra("product_name") ?: "Неизвестно"
        val productDescription = intent.getStringExtra("product_description") ?: "Описание отсутствует"

        // Устанавливаем данные в TextView
        binding.tvProductName.text = productName
        binding.tvProductDescription.text = productDescription
    }
}