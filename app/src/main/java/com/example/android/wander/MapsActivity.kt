package com.example.android.wander

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val TAG = MapsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latitude = 80.823670
        val longitude = 20.357667

        // Add a marker in Sydney and move the camera
        val customLatLong = LatLng(latitude, longitude)
        val zoomLevel = 8.5f

        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions().apply {
            image(BitmapDescriptorFactory.fromResource(R.drawable.android))
            position(customLatLong, overlaySize)
        }

        map.apply {
            addMarker(MarkerOptions().position(customLatLong))
            moveCamera(CameraUpdateFactory.newLatLngZoom(customLatLong, zoomLevel))

            setMapLongClick()
            setPoiClick()
            setMapStyle()

            addGroundOverlay(androidOverlay)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // Private
    private fun GoogleMap.setMapStyle() {
        try {
            val success = setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this@MapsActivity,
                            R.raw.map_style
                    )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed!")
            }

        } catch (error: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", error)
        }
    }

    private fun GoogleMap.setMapLongClick() {
        setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    latLng.latitude,
                    latLng.longitude
            )

            addMarker(
                    MarkerOptions().apply {
                        position(latLng)
                        title(getString(R.string.dropped_pin))
                        snippet(snippet)
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    }
            )
        }
    }

    private fun GoogleMap.setPoiClick() {
        setOnPoiClickListener { poi ->
            addMarker(
                    MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name)

            ).apply {
                showInfoWindow()
            }
        }
    }
}