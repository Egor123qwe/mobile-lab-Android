package com.example.mobile_lab_android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mobile_lab_android.databinding.FragmentProfileBinding
import com.example.mobile_lab_android.models.ProfileModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: Auth by viewModels()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadProfileData()

        binding.btnEditSave.setOnClickListener {
            if (isEditing) {
                saveProfileData()
            }
            toggleEditingMode()
        }

        binding.btnSignOut.setOnClickListener {
            authViewModel.signOut() // Используем метод из ViewModel для выхода
            Toast.makeText(requireContext(), "Выход выполнен", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        return binding.root
    }

    private fun loadProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val profile = ProfileModel.fromMap(document.data ?: emptyMap())

                    binding.etName.setText(profile.name)
                    binding.etEmail.setText(profile.email)
                    binding.etDateOfBirth.setText(profile.dateOfBirth)
                    binding.etPhoneNumber.setText(profile.phoneNumber)
                    binding.etAddress.setText(profile.address)
                    binding.etBio.setText(profile.bio)
                    binding.etOccupation.setText(profile.occupation)
                    binding.etWebsite.setText(profile.website)
                    binding.etSocialMedia.setText(profile.socialMedia)
                    binding.etAdditionalInfo.setText(profile.additionalInfo)
                } else {
                    Toast.makeText(requireContext(), "Профиль не найден", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val profile = ProfileModel(
            name = binding.etName.text.toString(),
            email = binding.etEmail.text.toString(),
            dateOfBirth = binding.etDateOfBirth.text.toString(),
            phoneNumber = binding.etPhoneNumber.text.toString(),
            address = binding.etAddress.text.toString(),
            bio = binding.etBio.text.toString(),
            occupation = binding.etOccupation.text.toString(),
            website = binding.etWebsite.text.toString(),
            socialMedia = binding.etSocialMedia.text.toString(),
            additionalInfo = binding.etAdditionalInfo.text.toString()
        )

        db.collection("users").document(userId).set(profile.toMap())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        user.delete()
            .addOnSuccessListener {
                db.collection("users").document(user.uid).delete()
                authViewModel.signOut()
                Toast.makeText(requireContext(), "Аккаунт удалён", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleEditingMode() {
        isEditing = !isEditing
        listOf(
            binding.etName,
            binding.etEmail,
            binding.etDateOfBirth,
            binding.etPhoneNumber,
            binding.etAddress,
            binding.etBio,
            binding.etOccupation,
            binding.etWebsite,
            binding.etSocialMedia,
            binding.etAdditionalInfo
        ).forEach { it.isEnabled = isEditing }

        binding.btnEditSave.text = if (isEditing) "Сохранить" else "Редактировать"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}