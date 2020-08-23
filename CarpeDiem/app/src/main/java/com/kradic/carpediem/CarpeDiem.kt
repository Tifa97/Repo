package com.kradic.carpediem

import android.app.Application
import com.mazenrashed.printooth.Printooth

//Klasa koja extenda Application, potrebna za inicijalizaciju Printootha
class CarpeDiem: Application() {
    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}