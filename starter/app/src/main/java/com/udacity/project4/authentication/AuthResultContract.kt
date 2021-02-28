package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R


/*sending an Int to NextActivity through an intent in the method createIntent
 and expecting to get a result (IdpResponse) with parseResult function.*/

class AuthResultContract : ActivityResultContract<Int, IdpResponse>() {

    private val providers =
        listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(), AuthUI.IdpConfig.EmailBuilder().build()
        )

    companion object {

        const val INPUT_KEY = "input_key"
    }

    /*create an intent to send an Input Int*/
    override fun createIntent(context: Context, input: Int?): Intent {

        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.ic_location)
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(true)
                .build()
                .apply {
                    putExtra(INPUT_KEY, input)
                }


    }

    /*Convert result obtained from Activity.onActivityResult(int, int, Intent) to O*/
    override fun parseResult(resultCode: Int, intent: Intent?): IdpResponse? {

        return when (resultCode) {

            Activity.RESULT_OK -> IdpResponse.fromResultIntent(intent)
            else -> null

        }

    }
}