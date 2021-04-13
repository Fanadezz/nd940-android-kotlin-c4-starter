package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    //get lazy delegate viewModel
    private val viewModel by viewModels<LoginViewModel>()

    //companion object with the integer input
    companion object {

        const val SIGN_IN_RESULT_CODE = 1001
    }

    //register Activity Result API
    private val authResultLauncher = registerForActivityResult(AuthResultContract()) {

        handleResponse(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        Timber.i("onCreate called")
        //LOGIN_IMPLEMENTATION

        //observe authentication status
        viewModel.authStateLiveData.observe(this) { status ->
            when (status) {

                LoginViewModel.AuthStateEnum.AUTHENTICATED -> {
                    //redirect to RemindersActivity if signed-in using an intent
                    startActivity(Intent(this, RemindersActivity::class.java))
                    finish()
                }
                else -> {

                    //prompt the user to sign in
                    buttonLogin.setOnClickListener {

                        authResultLauncher.launch(SIGN_IN_RESULT_CODE)
                    }

                }
            }
        }


        //          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }


    private fun handleResponse(idpResponse: IdpResponse) {

        if (idpResponse.error != null) {

            //log error if any
            Timber.i("Error: $idpResponse.error")

        } else

        //take the user to RemindersActivity
            startActivity(Intent(this, RemindersActivity::class.java))

    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true);
    }
}
