package com.example.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    val LOCATION_REQUEST_CODE = 100
    lateinit var statusView:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusView = findViewById(R.id.text_view)

        if(checkPermission()){
            setupLocationListner()
        }else{
           askPermission()
        }

    }

    fun isLocationEnabled(context: Context): Boolean {
        val gfgLocationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gfgLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || gfgLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun setupLocationListner(){
        if(isLocationEnabled(this)) {
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
           /*
           // TO get current location
           val task = fusedLocationProviderClient.getCurrentLocation(CurrentLocationRequest
                .Builder().build(),null)
            task.addOnSuccessListener {
                statusView.text =it.latitude.toString()
            }
            task.addOnFailureListener {

            }*/
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(1000)
                .build()
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        for (location in locationResult.locations) {
                            statusView.text = location.latitude.toString()
                            Log.e("LOC lat", location.latitude.toString())
                            Log.e("LOC long", location.longitude.toString())
                        }
                        // Things don't end here
                        // You may also update the location on your web app
                    }
                },
                Looper.getMainLooper()
            )
        }else{
            AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("required")
                .setCancelable(false)
                .setPositiveButton("Enable Now") { _, _ ->
                    this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
    }
    }
        /*val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            statusView.text = it?.latitude?.toString()
        }
        task.addOnFailureListener {
            statusView.text = it.localizedMessage
        }*/

    fun askPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission
            .ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_REQUEST_CODE)
    }
    fun checkPermission():Boolean{
        val check = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        return check == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.size > 0 ) {
                val fine  = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val coarse = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if(fine){
                    setupLocationListner()
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                }else{
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Permission to access the location is required.")
                                .setTitle("Permission required")

                                    builder.setPositiveButton("OK"
                                    ) { dialog, id ->
                               askPermission()
                            }

                            val dialog = builder.create()
                        dialog.show()
                    } else {
                       askPermission()
                    }

                }
                // The permission was granted, so you can access the protected data or functionality.
            } else {

                // The permission was denied, so you cannot access the protected data or functionality.
            }
        }
    }
}