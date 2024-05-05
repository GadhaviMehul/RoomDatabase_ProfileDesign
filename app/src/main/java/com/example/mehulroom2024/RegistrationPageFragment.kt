package com.example.mehulroom2024

import android.app.DatePickerDialog
import android.icu.util.MeasureUnit.EM
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mehulroom2024.databinding.FragmentRegistrationPageBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


class RegistrationPageFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationPageBinding
    private var gender: String = "female"
    private var imagePath: String? = null

    private val passwordPattern =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")

    private lateinit var viewModel: UserViewModel

    companion object {
        private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentRegistrationPageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setUpListener()
        return binding.root
    }

    private fun setUpListener() {

        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            gender = when (checkedId) {
                R.id.maleRadioButton -> "male"
                R.id.femaleRadioButton -> "female"
                else -> "female" // Default to female if none selected
            }
        }

        binding.imageView.setOnClickListener {
            pickImage.launch("image/*")

        }
        binding.birthDate.setOnClickListener {
            showDatePicker()
        }

        binding.signup.setOnClickListener {
            addUserData()
        }

    }

    private fun getImageFilePath(imageUri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(imageUri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val imagePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return imagePath
    }

    private fun loadImage(imagePath: String) {
        Glide.with(requireContext()).load(imagePath)
            .into(requireView().findViewById(R.id.imageView))
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri ->
                imagePath = getImageFilePath(selectedImageUri)

                if (imagePath != null) {
                    loadImage(imagePath!!)
                }
            }
        }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val formattedMonth = (monthOfYear + 1).toString().padStart(2, '0')
                val formattedDay = dayOfMonth.toString().padStart(2, '0')
                val date = "$year-$formattedMonth-$formattedDay"
                binding.birthDate.setText(date)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun checkAllFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        date: String,
        imagePath: String?,
        gender: String?,
        mobileNumber: String

    ): Boolean {

        return when {
            firstName.isEmpty() -> {
                binding.firstName.error = "This field is required"
                false
            }

            lastName.isEmpty() -> {
                binding.lastName.error = "This field is required"
                false
            }
            email.isEmpty() -> {
                binding.email.error = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.email.error = "Enter Valid Email"
                false
            }
            password.isEmpty() -> {
                binding.password.error = "Password is required"
               // binding.iPassword.passwordVisibilityToggleDrawable =null
                false
            }
            !passwordPattern.matcher(password).matches() -> {
                binding.password.error = "Enter Valid Email Password Like Password123#"
                false
            }

            confirmPassword.isEmpty() -> {
                binding.confirmPassword.error = "Confirm Password is required"
                false
            }
            confirmPassword != password -> {
                binding.confirmPassword.error = "Password Don't Match"
                false
            }
            date.isEmpty() -> {
                showSnackbar(getString(R.string.error_select_date))
                false
            }
            gender.isNullOrEmpty() -> {
                showSnackbar(getString(R.string.error_select_gender))
                false
            }

            imagePath.isNullOrEmpty() -> {
                showSnackbar(getString(R.string.error_select_image))
                false
            }
            mobileNumber.isEmpty() -> {
                binding.mobileNumber.error = "Enter Mobile Number"
                false
            }
            // Add image extension validation if needed
            /* !isValidImageExtension(imagePath) -> {
                 showSnackbar(getString(R.string.error_invalid_image_format))
                 false
             }*/
            else -> true
        }
    }

    private fun addUserData() {
        with(binding) {
            val firstName = firstName.text.toString()
            val lastName = lastName.text.toString()
            val email = email.text.toString()
            val password = password.text.toString()
            val mobileNumber = mobileNumber.text.toString()
            val date = birthDate.text.toString()
            val confirmPassword = confirmPassword.text.toString()

            if (checkAllFields(
                    firstName,
                    lastName,
                    email,
                    password,
                    confirmPassword,
                    date,
                    imagePath,
                    gender,
                    mobileNumber
                )
            ) {
                val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN))
                val user = User(
                    0,
                    firstName,
                    lastName,
                    email,
                    password,
                    localDate,
                    imagePath.orEmpty(),
                    gender,
                    mobileNumber
                )
                viewModel.addUserData(user)

                findNavController().navigate(R.id.action_registrationPageFragment_to_loginPageFragment)

            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}





