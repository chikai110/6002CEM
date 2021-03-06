package com.example.a6002cem

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class LocationFragment : Fragment() {
    private var sharedPreferences: SharedPreferences? = null

    // member views
    private var mLatitudeText: TextView? = null
    private var mLongitudeText: TextView? = null
    private var mLocationText: TextView? = null
    private var mLocateButton: Button? = null
    private var mTemperatureText: TextView? = null

    // member variables that hold location info
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGeocoder: Geocoder? = null
    private var mLocationProvider: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_location, container, false)

        mLatitudeText = view.findViewById<View>(R.id.latitude_text) as TextView
        mLongitudeText = view.findViewById<View>(R.id.longitude_text) as TextView
        mLocationText = view.findViewById<View>(R.id.current_location) as TextView
        mLocateButton = view.findViewById<View>(R.id.locate) as Button
        mTemperatureText = view.findViewById<View>(R.id.temperature) as TextView

        // below are placeholder values used when the app doesn't have the permission
        mLatitudeText!!.text = "Latitude not available yet"
        mLongitudeText!!.text = "Longitude not available yet"
        mLocationText!!.text = ""
        mTemperatureText!!.text = "Temperature Not available yet"


        // Check the location and GPS permission
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String?, Boolean?>> { result: Map<String?, Boolean?> ->
                val fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                )
                val coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                )
                if (fineLocationGranted != null && fineLocationGranted) {
                    // Precise location access granted.
                    mLocationText!!.text = "permission granted"
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Only approximate location access granted.
                    mLocationText!!.text = "permission granted"
                } else {
                    // No location access granted.
                    mLocationText!!.text = "permission not granted"
                }
            }
        )
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 10
        mLocationRequest!!.fastestInterval = 5
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // call the GPS start Function
        view.findViewById<Button>(R.id.locateOnOff).setOnClickListener {
            onStartClicked(view)
        }

        // Call the Locate the address
        view.findViewById<Button>(R.id.locate).setOnClickListener {
            onLocateClicked(view)

            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?

            // Added Cinema location Markers
            val currentLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            val cinema1 = LatLng(22.3981, 113.9746)
            val cinema2 = LatLng(22.3988, 113.9754)

            // Async map
            supportMapFragment!!.getMapAsync { googleMap ->
                // Initialize marker options
                var markerOptionsList = ArrayList<MarkerOptions>()
                // Set the title for the cinema markers
                markerOptionsList.add(MarkerOptions().position(currentLocation).title(mLocationText!!.text as String?))
                markerOptionsList.add(MarkerOptions().position(cinema1).title("Paris London New York Milano Cinema"))
                markerOptionsList.add(MarkerOptions().position(cinema2).title("Hyland Theatre"))
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))
                // Add marker on map
                markerOptionsList.forEach {
                    googleMap.addMarker(it)}
            }
        }

        return view
    }

    // Start GPS Function
    private fun onStartClicked (v : View) {
        mLocationProvider = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        mLocationText!!.text = "Started updating location..."
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLocationProvider!!.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallBack, Looper.getMainLooper()
        )
    }

    // Get the location address by Geocoder
    private fun onLocateClicked (v : View) {

        mGeocoder = Geocoder(requireContext())
        try {
            // Only 1 address is needed here.
            val addresses = mGeocoder!!.getFromLocation(
                mLastLocation!!.latitude, mLastLocation!!.longitude, 1
            )
            if (addresses.size == 1) {
                val address = addresses[0]
                // Combine address into single line
                val addressLines = StringBuilder()
                if (address.maxAddressLineIndex > 0) {
                    for (i in 0 until address.maxAddressLineIndex) {
                        addressLines.append(
                            """
        ${address.getAddressLine(i)}
        
        """.trimIndent()
                        )
                    }
                } else {
                    addressLines.append(address.getAddressLine(0))
                }
                mLocationText!!.text = addressLines
                sharedPreferences = requireActivity().getSharedPreferences("SharedPreMain", Context.MODE_PRIVATE)
                // Store the countryName for movie filtering
                val editor = sharedPreferences!!.edit()
                editor.putString(MainActivity.CURRENT_LOCATION, addresses[0].countryName.toString())
                editor.commit()
                // Call the get weather API function
                getWeatherAPI(addresses[0].countryName)
            } else {
                mLocationText!!.text = "WARNING! Geocoder returned more than 1 addresses!"
            }
        } catch (e: Exception) {
        }
    }

    private var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            // Set the result to the UI view
            mLastLocation = result.lastLocation
            mLatitudeText!!.text = mLastLocation!!.latitude.toString()
            mLongitudeText!!.text = mLastLocation!!.longitude.toString()
        }
    }

    // Get Weather data and display the temperature from openweathermap Web API
    private fun getWeatherAPI(location: String) {

        // Use the Thread Executor for waiting api responses
        val callable = Callable { val json =
            URL("https://api.openweathermap.org/data/2.5/weather?q=$location&units=metric&appid=4cf7f6610d941a1ca7583f50e7e41ba3").readText()
            Log.d("Weather Report", json)
            val jsonObj = JSONObject(json)
            val main = jsonObj.getJSONObject("main")
            return@Callable main.getString("temp")+"??C"
        }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        mTemperatureText!!.text = future.get()

    }
}