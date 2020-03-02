package com.example.testlocationservice

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import java.util.*
import kotlin.collections.ArrayList


class TrackerService : Service() {

    private val TAG = "`TrackerService`"


    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
        locationService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "Service onBind")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
    }

    private fun locationService() {
        Log.i(TAG, "location service")
        val request = LocationRequest()
        request.interval = 120000
        request.fastestInterval = 120000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val client = LocationServices.getFusedLocationProviderClient(this)
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permission granted")
            // Request location updates and when an update is
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    val location = locationResult!!.lastLocation
                    val androidID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
                    if (location != null) {
                        Log.d(TAG, "location update longitude & latitude ${location.longitude} ${location.latitude} ")
                        Log.d(TAG, "device Id $androidID")
                        val geocoder = Geocoder(applicationContext, Locale.getDefault())
                        var addresses: List<Address>? = null
                        try {

                            addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                        } catch (e: Exception) {

                        }
                        if (addresses == null || addresses.isEmpty()) {

                        } else {
                            val address = addresses[0]
                            val addressFragments = ArrayList<String>()
                            for (i in 0..address.maxAddressLineIndex) {
                                addressFragments.add(address.getAddressLine(i))
                            }
                            Log.d(TAG, "location update${addressFragments}")
                    }
                }}
            }, null)
        }else{

            Log.d(TAG, "permission not granted")
        }
    }
}
