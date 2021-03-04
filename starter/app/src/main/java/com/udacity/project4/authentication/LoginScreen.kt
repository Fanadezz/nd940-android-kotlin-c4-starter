package com.udacity.project4.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.registerForActivityResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import kotlinx.android.synthetic.main.fragment_login_screen.*
import timber.log.Timber


class LoginScreen : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()

    private val authResultLauncher = registerForActivityResult(AuthResultContract()) {

        handleResponse(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authStateLiveData.observe(viewLifecycleOwner) {

            when (it) {

                LoginViewModel.AuthStateEnum.AUTHENTICATED -> {

                    loginButton.setOnClickListener {
                        findNavController().navigate(R.id.reminderListFragment)

                    }


                }
                else -> {
                    loginButton.setOnClickListener {
                        authResultLauncher.launch(SIGN_IN_RESULT_CODE)

                    }

                    // findNavController().navigate(R.id.loginScreen)

                }

            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_screen, container, false)
    }

    private fun handleResponse(idpResponse: IdpResponse?) {


        if (idpResponse == null || idpResponse.error != null) {

            Timber.i("Login Unsuccessful")
        } else {
            Timber.i("Login Successful")
        }
    }

    companion object {
        private const val SIGN_IN_RESULT_CODE = 1001
    }

}