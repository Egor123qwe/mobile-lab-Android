package com.example.mobile_lab_android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_lab_android.models.ReviewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _reviews = MutableStateFlow<List<ReviewModel>>(emptyList())
    val reviews: StateFlow<List<ReviewModel>> get() = _reviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    /**
     * Загружает отзывы для конкретного товара
     */
    fun loadReviews(productId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val snapshot = db.collection("products").document(productId)
                    .collection("reviews")
                    .orderBy("timestamp")
                    .get()
                    .await()

                val reviewsList = snapshot.documents.mapNotNull { it.toObject(ReviewModel::class.java) }
                _reviews.value = reviewsList
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Добавляет новый отзыв к товару
     */
    fun addReview(productId: String, rating: Int, comment: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val userName = fetchUserName(userId) ?: "Аноним"

            val reviewData = mapOf(
                "userId" to userId,
                "userName" to userName,
                "rating" to rating,
                "comment" to comment,
                "timestamp" to Timestamp.now()
            )

            try {
                db.collection("products").document(productId)
                    .collection("reviews")
                    .add(reviewData)
                    .await()

                loadReviews(productId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка добавления: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Получает имя пользователя по его ID
     */
    private suspend fun fetchUserName(userId: String): String? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            document.getString("name")
        } catch (e: Exception) {
            null
        }
    }
}