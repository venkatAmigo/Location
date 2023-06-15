package com.example.permissions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts
        .RequestPermission()){
            isGranted ->
        if(isGranted){
            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show()
        }else{

            Toast.makeText(this, "not granted", Toast.LENGTH_SHORT).show()
        }
    }
}