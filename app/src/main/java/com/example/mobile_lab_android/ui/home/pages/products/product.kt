package com.example.mobile_lab_android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.mobile_lab_android.databinding.ActivityProductDetailBinding
import com.example.mobile_lab_android.models.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import androidx.activity.viewModels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_lab_android.databinding.ItemImageSliderBinding

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private val productViewModel: Product by viewModels { ProductFactory(authViewModel = Auth()) }

    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val productName = intent.getStringExtra("product_name") ?: "Неизвестно"
        val productDescription = intent.getStringExtra("product_description") ?: "Описание отсутствует"
        val productImages = intent.getStringArrayListExtra("product_images") ?: ArrayList()
        productId = intent.getStringExtra("product_id") // Убедитесь, что передаете ID товара

        // Устанавливаем данные в TextView
        binding.tvProductName.text = productName
        binding.tvProductDescription.text = productDescription

        // Настройка слайдера с фотографиями
        setupImageSlider(productImages)

        // Загружаем состояние избранного для товара
        loadProductFavoriteStatus()

        // Обработчик нажатия на кнопку "Добавить в избранное"
        binding.btnFavorite.setOnClickListener {
            productId?.let { id ->
                // Переключаем состояние избранного
                productViewModel.toggleFavorite(ProductModel(id = id, name = productName, description = productDescription))

                // Временная задержка для обновления UI
                binding.btnFavorite.text = if (binding.btnFavorite.text == "Добавить в избранное") {
                    "Удалить из избранного"
                } else {
                    "Добавить в избранное"
                }
            }
        }

        // Наблюдаем за изменениями в состоянии избранного
        productViewModel.products.observe(this, { products ->
            val product = products.find { it.id == productId }
            product?.let {
                // Обновляем текст кнопки в зависимости от состояния товара в избранном
                if (it.isFavorite) {
                    binding.btnFavorite.text = "Удалить из избранного"
                } else {
                    binding.btnFavorite.text = "Добавить в избранное"
                }
            }
        })
    }

    // Функция для настройки слайдера
    private fun setupImageSlider(images: List<String>) {
        // Создаем адаптер для ViewPager2
        val adapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = adapter
    }

    // Функция для загрузки состояния товара (в избранном или нет)
    private fun loadProductFavoriteStatus() {
        productId?.let {
            val favoriteProductIds = productViewModel.products.value?.filter { it.isFavorite }?.map { it.id }
            if (favoriteProductIds?.contains(it) == true) {
                binding.btnFavorite.text = "Удалить из избранного"
            } else {
                binding.btnFavorite.text = "Добавить в избранное"
            }
        }
    }
}

class ImageSliderAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Picasso.get().load(imageUrl).into(holder.binding.imageView) // Загружаем изображение в ImageView с помощью Picasso
    }

    override fun getItemCount(): Int = imageUrls.size

    class ImageSliderViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root)
}