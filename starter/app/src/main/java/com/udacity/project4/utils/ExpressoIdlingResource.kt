package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource

/* This singleton class lives in the actual app code to track wheter long running tasks
* are still working. It is a Singleton class with Idling resource inside it.
*
* It is Singleton class so that we can use it anywhere in the code */
object EspressoIdlingResource {

    /*allows you to increment and decrement a counter such that when the counter
    * is greater than zero the app is considered working when less the zero the
    * app is idle*/
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {

        if (!countingIdlingResource.isIdleNow){
            countingIdlingResource.decrement()
        }
    }
}

/*Typing out EspressoIdlingResource.increment and decrement and sorrounging thing with try-catch
* statements is a lot of boiler plate and you might forget to call increment and decrement
* to solve this issue we add and inline function*/


inline fun <T> wrapsEspressoIdlingResource( function:() -> T):T {

    EspressoIdlingResource.increment() //set app as busy

    return try {
        function()
    }finally {
        EspressoIdlingResource.decrement() // set app as idle
    }
}
