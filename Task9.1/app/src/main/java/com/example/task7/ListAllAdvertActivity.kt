package com.example.task7

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task7.data.AdvertDatabase
import com.example.task7.data.AdvertRepository
import com.example.task7.model.Advert
import com.example.task7.ui.theme.Task7Theme
import com.example.task7.ui.viewmodel.AdvertViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListAllAdvertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AdvertDatabase.getDatabase(this)
        val repository = AdvertRepository(database.advertDao())
        val viewModel = AdvertViewModel(repository)

        setContent {
            Task7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListAllAdvertScreen(
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ListAllAdvertScreen(viewModel: AdvertViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current;
    val adverts by viewModel.allAdverts.collectAsState(initial = emptyList())

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        TextButton(onClick = {
            (context as? ComponentActivity)?.finish()
        }) {
            Text("Back")
        }

        Text(
            text = "Lost & Found Items",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

//        HorizontalDivider()
        // Should list out all the adverts
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            LazyColumn {
                items(adverts) { advert ->
                    AdvertItem (advert) { selectedAdvert ->
                        val intent = Intent(context, AdvertDetailActivity::class.java)
                        intent.putExtra("advertId", selectedAdvert.id)
                        context.startActivity(intent)
                    }
                }
            }
        }

//        Button(onClick = {
//            val intent = Intent(context, CreateTaskActivity::class.java)
//            context.startActivity(intent);
//        }) {
//            Text("Create Task")
//        }
    }
}

@Composable
fun AdvertItem(advert: Advert, onClick: (Advert) -> Unit) {

    Column (
        modifier = Modifier.clickable { onClick(advert) }
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(6.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
            ) {
                Text(text = advert.name, fontWeight = FontWeight.SemiBold)
                Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(advert.date)), fontSize = 14.sp)
                Text(text = advert.description)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}