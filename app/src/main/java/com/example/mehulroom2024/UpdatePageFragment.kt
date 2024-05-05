package com.example.mehulroom2024

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mehulroom2024.databinding.FragmentProfilePageBinding
import com.example.mehulroom2024.databinding.FragmentUpdatePageBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


class UpdatePageFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePageBinding
    private val args by navArgs<UpdatePageFragmentArgs>()
    private lateinit var viewModel: UserViewModel
    private var imagePath: String? = null
    private var gender: String = "Female"
    private val passwordPattern =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        setUpListener()
        return binding.root

    }

    private fun setUpListener() {

        showUserData()

        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            gender = when (checkedId) {
                R.id.maleRadioButton -> "Male"
                R.id.femaleRadioButton -> "Female"
                else -> "Female"
            }
        }

        binding.birthDate.setOnClickListener {
            showDatePicker()
        }

        binding.imageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.updateButton.setOnClickListener {
            performUpdate()
        }


    }


    private fun showUserData() {
        val user = args.update
        with(binding) {
            firstName.setText(user.firstName)
            lastName.setText(user.lastName)
            email.setText(user.email)
            mobileNumber.setText(user.mobileNumber)
            birthDate.setText(user.date.toString())
            Glide.with(imageView.context).load(user.image).into(imageView)
        }
    }

    private fun getImageFilePath(imageUri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            requireActivity().contentResolver.query(imageUri, filePathColumn, null, null, null)
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

        gender: String?,
        mobileNumber: String,
    ): Boolean {

        return when {

            firstName.isEmpty() -> {
                binding.firstName.error = "This Field is Required"
                false
            }
            lastName.isEmpty() -> {
                binding.lastName.error = "This field is Required"
                false
            }
            email.isEmpty() -> {
                binding.email.error = "Enter is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.email.error = "Enter Valid Email"
                false
            }
            password.isEmpty() -> {
                binding.password.error = "Password is required"
                false
            }
            !passwordPattern.matcher(password).matches() -> {
                binding.password.error = "Enter Valid Email Password Like Password123#"
                false
            }
            confirmPassword.isEmpty() -> {
                binding.confirmPassword.error = "Confirm password is required"
                false
            }
            confirmPassword != password -> {
                binding.confirmPassword.error = "Password Don't match"
                false
            }
            date.isEmpty() -> {
                showSnackBar("Please select Birthdate")
                false
            }
            /*  imagePath.isNullOrEmpty() -> {
                  showSnackBar("Please select Image")
                  false
              }*/
            gender.isNullOrEmpty() -> {
                showSnackBar("Please Select Birthdate")
                false
            }
            mobileNumber.isEmpty() -> {
                binding.mobileNumber.error = "Enter Mobile Number"
                false
            }


            else -> true
        }
    }

    private fun performUpdate() {
        with(binding) {
            val firstName = firstName.text.toString().trim()
            val lastName = lastName.text.toString().trim()
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            val confirmPassword = confirmPassword.text.toString().trim()
            val mobileNumber = mobileNumber.text.toString().trim()
            val date = birthDate.text.toString().trim()

            if (checkAllFields(
                    firstName, lastName, email, password, confirmPassword, date,

                    gender, mobileNumber
                )
            ) {

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val localDate = LocalDate.parse(date, formatter)

                val updatedImagePath = if (imagePath.isNullOrBlank()) {
                    args.update.image // Use existing image path if imagePath is null or blank
                } else {
                    imagePath // Use newly selected image path
                }

                val user = User(
                    args.update.id,
                    firstName,
                    lastName,
                    email,
                    password,
                    localDate,
                    updatedImagePath.orEmpty(),
                    gender,
                    mobileNumber
                )
                viewModel.updateUserData(user)

                showSnackBar("Update Successfully")
                findNavController().navigate(R.id.action_updatePageFragment_to_loginPageFragment)

            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}