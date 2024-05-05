package com.example.mehulroom2024

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mehulroom2024.databinding.FragmentLoginPageBinding
import com.google.android.material.snackbar.Snackbar

class LoginPageFragment : Fragment() {
    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var viewModel: UserViewModel
    private val text: String by lazy { getString(R.string.dont_have_account) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val spannableString = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                findNavController().navigate(R.id.action_loginPageFragment_to_registrationPageFragment)
            }
        }

        spannableString.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.spannable.text = spannableString
        binding.spannable.movementMethod = LinkMovementMethod.getInstance()

        binding.buttonLogin.setOnClickListener {
            userLogin()
        }
        return binding.root
    }

    private fun userLogin() {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()


        viewModel.login(email, password).observe(viewLifecycleOwner) { user ->
            if (user != null) {
                showSnackBar("Login Successfully")
                val action =
                    LoginPageFragmentDirections.actionLoginPageFragmentToProfilePageFragment(user)
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Enter valid email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}