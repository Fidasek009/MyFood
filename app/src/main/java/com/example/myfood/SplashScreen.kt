package com.example.myfood

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myfood.model.Database
import com.example.myfood.model.database

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // load database
        database = Database(this)

        // start main activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
