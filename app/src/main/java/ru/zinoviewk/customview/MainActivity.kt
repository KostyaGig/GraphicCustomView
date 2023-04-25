package ru.zinoviewk.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

fun Any.log(message: String) {
    Log.d("zinoviewkska", message)
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log("oncreate")
    }
}