package com.example.mehulroom2024

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mehulroom2024.databinding.FragmentLoginPageBinding
import com.example.mehulroom2024.databinding.FragmentProfilePageBinding
import com.google.android.material.snackbar.Snackbar


class ProfilePageFragment : Fragment() {
    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var viewModel: UserViewModel
    private val args by navArgs<ProfilePageFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        showProfileData()
        return binding.root
    }


    private fun showProfileData() {

        val user = args.profile
        with(binding) {
            firstName.text = user.firstName
            lastName.text = user.lastName
            mobile.text = user.mobileNumber
            email.text = user.email
            birthDate.text = user.date.toString()
            headerEmail.text = user.email
            Glide.with(imageView.context).load(user.image).into(imageView)
            update.setOnClickListener {
                val action =
                    ProfilePageFragmentDirections.actionProfilePageFragmentToUpdatePageFragment(user)
                findNavController().navigate(action)
            }

            delete.setOnClickListener {
                showDeleteConfirmationDialog(user)
            }

        }
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete User Data")
            setMessage("Are you sure you want to delete this user's data?")
            setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.deleteUserData(user)
                Toast.makeText(requireContext(), "Data deleted successfully!", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_profilePageFragment_to_loginPageFragment)
            }
            setNegativeButton(android.R.string.cancel) { _, _ ->
                Toast.makeText(requireContext(), "Deletion operation canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }.create().show()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}