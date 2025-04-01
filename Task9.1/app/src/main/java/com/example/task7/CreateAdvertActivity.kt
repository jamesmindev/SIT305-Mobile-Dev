package com.example.task7

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.task7.data.AdvertDatabase
import com.example.task7.data.AdvertRepository
import com.example.task7.model.Advert
import com.example.task7.ui.theme.Task7Theme
import com.example.task7.ui.viewmodel.AdvertViewModel
import com.example.task7.ui.viewmodel.AdvertViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateAdvertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyAn1_wnFzeTRzik-MrZ7S6yzAv1N3FHaIQ")
        }

        val advertViewModal: AdvertViewModel by viewModels {
            AdvertViewModelFactory(AdvertRepository(AdvertDatabase.getDatabase(this).advertDao()))
        }

        setContent {
            Task7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CreateAdvertScreen(
                        viewModel = advertViewModal,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAdvertScreen(viewModel: AdvertViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;
    val activity = context as? ComponentActivity;

    var advertType by remember { mutableStateOf("lost") }
    var advertName by remember { mutableStateOf("") }
    var advertPhone by remember { mutableStateOf("") }
    var advertDescription by remember { mutableStateOf("") }
    var advertDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var advertLocation by remember { mutableStateOf("") }
    var advertLatitude by remember { mutableStateOf(0.0) }
    var advertLongitude by remember { mutableStateOf(0.0) }

    var error by remember { mutableStateOf(false) }

    // Autocomplete launcher for Places API
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                val place = Autocomplete.getPlaceFromIntent(intent)
                advertLocation = place.name ?: ""
                advertLatitude = place.latLng?.latitude ?: 0.0;
                advertLongitude = place.latLng?.longitude ?: 0.0;
            }
        }
    }


    // Location client to fetch the current location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Use current location function
    @SuppressLint("MissingPermission")
    fun useCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Get latitude and longitude
                val latitude = location.latitude
                val longitude = location.longitude

                advertLatitude = latitude;
                advertLongitude = longitude;

                // Use Geocoder to get the address
                val geocoder = Geocoder(context, Locale.getDefault())

                try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses.get(0)
                            advertLocation = "${address.getAddressLine(0)}, ${address.locality}, ${address.countryName}"
                        } else {
                            advertLocation = "(Unknown address)"
                        }
                    }
                } catch (e: Exception) {
                    advertLocation = "(Unknown address)"
                }
            } else {
                advertLocation = "(Unknown Location)"
            }
        }
    }


    Column (
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        // Heading
        Text("Create a New Advert", fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp))


        // Type field
        Column {
            Text("Post Type", fontWeight = FontWeight.SemiBold)
            Row(Modifier.selectableGroup()) {
                Row(
                    Modifier
                        .height(48.dp)
                        .selectable(
                            selected = (advertType=="lost"),
                            onClick = { advertType = "lost" }
                        )
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = advertType=="lost", onClick = { advertType = "lost" })
                    Text("Lost")
                }

                Row(
                    Modifier
                        .height(48.dp)
                        .selectable(
                            selected = (advertType=="found"),
                            onClick = { advertType = "found" }
                        )
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = advertType=="found", onClick = { advertType = "found" })
                    Text("Found")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Name field
        Text("Name", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = advertName,
            onValueChange = { advertName = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Phone field
        Text("Phone", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = advertPhone,
            onValueChange = { advertPhone = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))


        // Description
        Text("Description", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = advertDescription,
            onValueChange = { advertDescription = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        DatePickerField(
            selectedDate = advertDate,
            onDateSelected = { advertDate = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Location
        Text("Location", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.clickable {
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                ).build(context)
                autocompleteLauncher.launch(intent)
            }.fillMaxWidth()
                .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .padding(start= 12.dp, end = 4.dp, top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (advertLocation.isEmpty()) "Add Location" else advertLocation)
        }
        TextButton (onClick = { useCurrentLocation() }) {
            Text("Use Current Location")
        }


        Spacer(modifier = Modifier.height(12.dp))

        if (error) {
            Text(text = "Please fill in all fields.", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Button to add
        Button(onClick = {
            error = false
            if (advertName.isNotEmpty() && advertDate > 0 && advertDescription.isNotEmpty() && advertPhone.isNotEmpty()&& advertLocation.isNotEmpty()) {
                val advert = Advert(postType = advertType, name = advertName, date = advertDate, description = advertDescription, phoneNumber = advertPhone, location = advertLocation, latitude = advertLatitude, longitude = advertLongitude)
                viewModel.addAdvert(advert)

                // Once added, go back to previous screen
                (context as? ComponentActivity)?.finish()
            } else {
                error = true;
            }
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            onDateSelected(selectedCalendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    Column {
        Text("Date", fontWeight = FontWeight.SemiBold)
            
        Row (
            modifier = Modifier.fillMaxWidth()
                .height(56.dp)
                .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .padding(start= 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate)))
            TextButton (onClick = { datePickerDialog.show() }) {
                Text("Change Date")
            }
        }
    }
}
