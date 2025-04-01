package com.example.task7

import android.content.Intent
import android.os.Bundle
import android.text.method.TextKeyListener.Capitalize
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task7.data.AdvertDatabase
import com.example.task7.data.AdvertRepository
import com.example.task7.model.Advert
import com.example.task7.ui.theme.Task7Theme
import com.example.task7.ui.viewmodel.AdvertViewModel
import com.example.task7.ui.viewmodel.AdvertViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdvertDetailActivity : ComponentActivity() {
    private val advertViewModel: AdvertViewModel by viewModels {
        AdvertViewModelFactory(AdvertRepository(AdvertDatabase.getDatabase(this).advertDao()))
    }
    private var advertId: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        advertId = intent.getIntExtra("advertId", -1)
        if (taskId == -1) throw IllegalArgumentException("Invalid ID.")

        setContent {
            Task7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val advert by advertViewModel.getAdvertById(advertId).observeAsState()

                    advert?.let { advertData ->
                        AdvertDetailScreen(
                            advert = advertData,
                            viewModel = advertViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdvertDetailScreen(advert: Advert, viewModel: AdvertViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        Spacer(modifier= Modifier.height(12.dp))

        Text(text = advert.postType.uppercase(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp)
        )
        Spacer(modifier= Modifier.height(12.dp))

        Text(text = advert.name, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier= Modifier.height(8.dp))

        Text(text = advert.description)
        Spacer(modifier= Modifier.height(24.dp))

        Text("Date", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(advert.date)))
        Spacer(modifier= Modifier.height(24.dp))

        Text("Location", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(text = advert.location)
        Spacer(modifier= Modifier.height(16.dp))

        Text("Contact Number", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(text = advert.phoneNumber)
        Spacer(modifier= Modifier.height(24.dp))

        Button(onClick = {
            viewModel.deleteTask(advert)
            (context as? ComponentActivity)?.finish()
        },
            colors = ButtonDefaults.filledTonalButtonColors(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Remove")
        }
    }
}