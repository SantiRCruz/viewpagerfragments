package com.example.proof2.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.example.proof2.R
import com.example.proof2.databinding.FragmentMapBinding
import com.example.proof2.databinding.FragmentQrBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar


class MapFragment : Fragment(R.layout.fragment_map) {
    private lateinit var binding: FragmentMapBinding
    private lateinit var map: GoogleMap
    private val registerPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Snackbar.make(
                    binding.root,
                    "You must have the permission activated",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                enableLocation()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapBinding.bind(view)
        childFragmentManager.findFragmentById(R.id.map)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            enableLocation()
            createLines()
//            createMarker()
        }
    }

    @SuppressLint("MissingPermission")
    private fun createLines() {
        val loc = (requireContext().getSystemService(LOCATION_SERVICE) as LocationManager).getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val latLng = LatLng(loc!!.latitude,loc.longitude)
        val marker = MarkerOptions().position(latLng).title("your location").icon(BitmapDescriptorFactory.fromResource(R.drawable.right))
        map.addMarker(marker)
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f))


        val latLng1 = LatLng(4.604329536135835, -74.10019374771956)
        val marker1 = MarkerOptions().position(latLng1).title("location 2").icon(BitmapDescriptorFactory.fromResource(R.drawable.info))
        map.addMarker(marker1)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,18f))

        val lines = PolylineOptions()
            .color(getColor(requireContext(),R.color.black))
            .width(4f)
            .add(latLng)
            .add(LatLng(4.604329536135835, -74.10019374771956))


        map.addPolyline(lines)

    }

    private fun createMarker() {
        val coordinates = LatLng(28.043893, -16.539329)
        val maker = MarkerOptions().position(coordinates).title("corona epicenter")
        map.addMarker(maker)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f), 2500, null)
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            registerPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


}