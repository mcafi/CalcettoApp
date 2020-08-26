package com.mcafi.calcetto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcafi.calcetto.model.Match

class MapFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private val db = initializeDatabase()
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_map, viewGroup, false)
        val mMapView = v.findViewById(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        // il getMapAsync chiama la onMapReady
        mMapView.getMapAsync(this)

        return v
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.414165, 8.942184), 13F))
        db.collection("partite")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val match = document.toObject(Match::class.java)
                        if (match.participants.size < match.available) {
                            val marker = googleMap.addMarker(MarkerOptions()
                                    .position(LatLng(match.place.lat, match.place.lng))
                                    .title(match.matchDate.toString() + ", " + (match.available - match.participants.size) + " posti disponibili"))
                            marker.tag = document.id
                        }
                    }
                }
        map.setOnInfoWindowClickListener(this)
        enableMyLocation()
    }

    private fun initializeDatabase(): FirebaseFirestore {
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
        return db
    }

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13F))
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.414165, 8.942184), 13F))
                        }
                    }

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermissions(Array(2) { Manifest.permission.ACCESS_FINE_LOCATION; Manifest.permission.ACCESS_COARSE_LOCATION }, 42)
        }
    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        val viewMatchIntent = Intent(activity, MatchViewActivity::class.java)
        viewMatchIntent.putExtra("MATCH_ID", marker.tag as String)
        startActivity(viewMatchIntent)
    }

}