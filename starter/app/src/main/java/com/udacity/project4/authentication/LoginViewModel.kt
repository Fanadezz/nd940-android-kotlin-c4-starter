package com.udacity.project4.authentication



import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LoginViewModel: ViewModel() {


    enum class AuthStateEnum{

        AUTHENTICATED, UNAUTHENTICATED
    }


    val authStateLiveData = FirebaseUserLiveData().map {
        if (it != null) {
            AuthStateEnum.AUTHENTICATED
        }
        else{

            AuthStateEnum.UNAUTHENTICATED
        }
    }

}