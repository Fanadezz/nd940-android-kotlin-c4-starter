package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseUserLiveData: LiveData<FirebaseUser>() {

    //Auth Instance
    private val auth = FirebaseAuth.getInstance()

    //Auth State Listener
    val stateListener = FirebaseAuth.AuthStateListener {
//set LiveData user to current user
        value = auth.currentUser
    }

    override fun onActive() {
        super.onActive()
       //Listen when app is in foregroung
        auth.addAuthStateListener(stateListener)
    }

    override fun onInactive() {
        super.onInactive()

        //remove stateListener when app is in the backgroung
        auth.removeAuthStateListener(stateListener)
    }
}