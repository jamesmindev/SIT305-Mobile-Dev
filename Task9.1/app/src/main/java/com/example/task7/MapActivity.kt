package com.example.task7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.task7.data.AdvertDatabase
import com.example.task7.data.AdvertRepository
import com.example.task7.ui.theme.Task7Theme
import com.example.task7.ui.viewmodel.AdvertViewModel
import com.example.task7.ui.viewmodel.AdvertViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MarkerState

class MapActivity : ComponentActivity() {
    private val advertViewModel: AdvertViewModel by viewModels {
        AdvertViewModelFactory(AdvertRepository(AdvertDatabase.getDatabase(this).advertDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Task7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapScreen(
                        viewModel = advertViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MapScreen(viewModel: AdvertViewModel, modifier: Modifier = Modifier) {
    val adverts by viewModel.allAdverts.collectAsState(initial = emptyList())

    if (adverts.isNotEmpty()) {
        val avgLat = adverts.sumOf { it.latitude } / adverts.size
        val avgLng = adverts.sumOf { it.longitude } / adverts.size
        val initialPosition = LatLng(avgLat, avgLng)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPosition, 5f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Add markers on the map
            adverts.forEach { advert ->
                Marker(
                    state = MarkerState(position = LatLng(advert.latitude, advert.longitude)),
                    title = advert.name,
                )
            }
        }
    }
}

