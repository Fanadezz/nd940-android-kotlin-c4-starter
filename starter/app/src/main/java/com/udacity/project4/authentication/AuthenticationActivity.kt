package com.udacity.project4.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }


    private fun handleResponse(idpResponse: IdpResponse?){

        if ( idpResponse == null ||idpResponse.error != null){

           Toast.makeText(this, "Error: $idpResponse.error", Toast.LENGTH_LONG).show()
        }else

            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_LONG).show()
    }
}
