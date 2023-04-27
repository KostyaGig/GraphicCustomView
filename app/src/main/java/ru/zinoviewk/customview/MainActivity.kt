package ru.zinoviewk.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

fun Any.log(message: String) {
    Log.d("zinoviewkska", message)
}

class MainActivity : AppCompatActivity() {

    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.counter)

        findViewById<CustomButton>(R.id.custom_btn).onClickListener {
            counter++
            text.text = counter.toString()
        }
    }
}