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
        val m = Matrix(5, 4, 2.5f)
        val m1 = Matrix(4, 2, 2f)
        val m2 = m * m1
        println("=== ${m2[4, 1]}, ${m.nativePointer}, ${m1.nativePointer}, ${m2.nativePointer}")
        m.dispose()
        m1.dispose()
        m2.dispose()
    }
}
