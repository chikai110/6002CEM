package com.example.a6002cem

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LocationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // member views
    private var mLatitudeText: TextView? = null
    private var mLongitudeText: TextView? = null
    private var mTimeText: TextView? = null
    private var mOutput: TextView? = null
    private var mLocateButton: Button? = null

    // member variables that hold location info
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGeocoder: Geocoder? = null
    private var mLocationProvider: FusedLocationProviderClient? = null

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_location, container, false)

        mLatitudeText = view.findViewById<View>(R.id.latitude_text) as TextView
        mLongitudeText = view.findViewById<View>(R.id.longitude_text) as TextView
        mTimeText = view.findViewById<View>(R.id.time_text) as TextView
        mLocateButton = view.findViewById<View>(R.id.locate) as Button
        mOutput = view.findViewById<View>(R.id.output) as TextView

        // below are placeholder values used when the app doesn't have the permission
        mLatitudeText!!.text = "Latitude not available yet"
        mLongitudeText!!.text = "Longitude not available yet"
        mTimeText!!.text = "Time not available yet"
        mOutput!!.text = ""
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
                    // permissionOk = true;
                    mTimeText!!.text = "permission granted"
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Only approximate location access granted.
                    // permissionOk = true;
                    mTimeText!!.text = "permission granted"
                } else {
                    // permissionOk = false;
                    // No location access granted.
                    mTimeText!!.text = "permission not granted"
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

        onStartClicked(view)

        view.findViewById<Button>(R.id.locate).setOnClickListener {
            onLocateClicked(view)

            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?

            val currentLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            val cinema1 = LatLng(22.3981, 113.9746)
            val cinema2 = LatLng(22.3988, 113.9754)

            // Async map
            supportMapFragment!!.getMapAsync { googleMap ->
                // Initialize marker options
                var markerOptionsList = ArrayList<MarkerOptions>()
                markerOptionsList.add(MarkerOptions().position(currentLocation).title(mOutput!!.text as String?))
                markerOptionsList.add(MarkerOptions().position(cinema1).title("Paris London New York Milano Cinema"))
                markerOptionsList.add(MarkerOptions().position(cinema2).title("Hyland Theatre"))
                // Remove all marker
//                googleMap.clear()
                // Animating to zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15F))
                // Add marker on map
                markerOptionsList.forEach {
                    googleMap.addMarker(it)}
            }
        }

        return view
    }

    private fun onStartClicked (v : View) {
        mLocationProvider = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        mTimeText!!.text = "Started updating location"
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

    private fun onLocateClicked (v : View) {
        mGeocoder = Geocoder(requireContext())
        try {
            // Only 1 address is needed here.
            val addresses = mGeocoder!!.getFromLocation(
                mLastLocation!!.latitude, mLastLocation!!.longitude, 1
            )
            if (addresses.size == 1) {
                val address = addresses[0]
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
                mOutput!!.text = addressLines
            } else {
                mOutput!!.text = "WARNING! Geocoder returned more than 1 addresses!"
            }
        } catch (e: Exception) {
        }
    }

    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            mLastLocation = result.lastLocation
            mLatitudeText!!.text = mLastLocation!!.latitude.toString()
            mLongitudeText!!.text = mLastLocation!!.longitude.toString()
            mTimeText!!.text = DateFormat.getTimeInstance().format(Date())
        }
    }

    companion object {
        var REQUEST_LOCATION = 1
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}