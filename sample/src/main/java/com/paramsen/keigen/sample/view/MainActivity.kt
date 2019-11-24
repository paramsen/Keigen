package com.paramsen.keigen.sample.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paramsen.keigen.Matrix
import com.paramsen.keigen.sample.R

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val m = Matrix(3,3,2f)
        println("=== ${m[3, 3]}")
    }
}
