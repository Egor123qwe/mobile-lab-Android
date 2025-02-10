package com.example.mobile_lab_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobile_lab_android.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

       replaceFragment(ProductsFragment())

        binding.btnProfile.setOnClickListener {
            replaceFragment(ProfileFragment())
        }

        binding.btnProducts.setOnClickListener {
            replaceFragment(ProductsFragment())
        }

        binding.btnFavorites.setOnClickListener {
            replaceFragment(FavoritesFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}