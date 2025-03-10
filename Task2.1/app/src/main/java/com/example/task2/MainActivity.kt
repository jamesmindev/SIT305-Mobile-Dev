@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.task2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task2.ui.theme.Task2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task2Theme {
                Column(modifier = Modifier.fillMaxSize()) {
                    UnitConverterMain()
                }
            }
        }
    }
}

@Composable
fun UnitConverterMain(modifier: Modifier = Modifier) {
    val conversionTypes = listOf("Length", "Weight", "Temperature")
    var conversionType by remember { mutableStateOf(conversionTypes[0]) }

    var convertFrom by remember { mutableStateOf("") }
    var convertTo by remember { mutableStateOf("") }

    var valueToConvert by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(0.0) }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp));
        Text(
            text = "Unit Converter",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp));

        Text(text = "What do you want to convert?")

        Spacer(modifier = Modifier.height(4.dp));

        Row {
            FilledTonalButton(
                onClick = {
                    conversionType = conversionTypes[0];
                    convertFrom = "";
                    convertTo = "";
                },

                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (conversionType == conversionTypes[0]) Color(0xFF495D92) else Color.hsl(0f, 0f, 0.92f),
                    contentColor = if (conversionType == conversionTypes[0]) Color.White else Color.Unspecified
                ),
            ) {
                Text("Length")
            }

            Spacer(modifier = Modifier.width(4.dp))

            FilledTonalButton(
                onClick = {
                    conversionType = conversionTypes[1];
                    convertFrom = "";
                    convertTo = "";
                          },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (conversionType == conversionTypes[1]) Color(0xFF495D92) else Color.hsl(0f, 0f, 0.92f),
                    contentColor = if (conversionType == conversionTypes[1]) Color.White else Color.Unspecified
                ),
            ) {
                Text("Weight")
            }

            Spacer(modifier = Modifier.width(4.dp))

            FilledTonalButton(
                onClick = {
                    conversionType = conversionTypes[2];
                    convertFrom = "";
                    convertTo = "";
                          },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (conversionType == conversionTypes[2]) Color(0xFF495D92) else Color.hsl(0f, 0f, 0.92f),
                    contentColor = if (conversionType == conversionTypes[2]) Color.White else Color.Unspecified
                ),
            ) {
                Text("Temperature")
            }
        }

        Spacer(modifier = Modifier.height(16.dp));

        // Convert unit dropdowns
        Row (
            modifier = Modifier.fillMaxWidth(),
        ) {
            UnitSelectorDropdown(
                "From",
                conversionType,
                convertFrom,
                onUpdateConvertUnit = { unit -> convertFrom = unit },
                modifier = Modifier.weight(1f)
            );

            Spacer(modifier = Modifier.width(8.dp))

            UnitSelectorDropdown(
                "To",
                conversionType,
                convertTo,
                onUpdateConvertUnit = { unit -> convertTo = unit },
                modifier = Modifier.weight(1f)
            );
        }

        Spacer(modifier = Modifier.height(4.dp));

        OutlinedTextField(
            value = valueToConvert,
            label = { Text("Enter Value to Convert") },
            onValueChange = { value -> valueToConvert =
                if (
                    value.all { it.isDigit() || it == '.'}
                    &&
                    (value.count { it == '.' } <= 1 )
                )
                    value else valueToConvert
                },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
        )

        Spacer(modifier = Modifier.height(12.dp));

        Button(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            onClick = { result = convertValue(conversionType, convertFrom, convertTo, valueToConvert)}) {
            Text("Convert")
        }

        Spacer(modifier = Modifier.height(24.dp));

        // Conversion result here
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xffeeeeee))
                .padding(16.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Result: ${result}");
            // Text("Result: ${if (result != 0.0) result else ""}");
        }
    }
}

fun convertValue( conversionType: String, from: String, to: String, value: String ): Double {
    if (from.isEmpty() || to.isEmpty() || value.isEmpty()) return 0.0;

    var result: Double = 0.0;
    var fromValue: Double = 1.0;
    var toValue: Double = 1.0;

    // Converting Temperature
    if ( conversionType == "Temperature") {
        when (from) {
            "Celsius" -> {
                fromValue = value.toDouble()
            };
            "Fahrenheit" -> {
                fromValue = (value.toDouble() - 32) / 1.8;
            }
            "Kelvin" -> {
                fromValue = value.toDouble() - 273.15
            }
        }

        when (to) {
            "Celsius" -> {
                result = fromValue;
            }
            "Fahrenheit" -> {
                result = (fromValue * 1.8) + 32;
            }
            "Kelvin" -> {
                result = fromValue + 273.15;
            }
        }

        // Round the value to 3 decimal place.
        // result = (result * 1000.0).toInt() / 1000.0;
        return result;
    }

    // Converting other values
    when (from) {
        "centimeter" -> fromValue = 1.0;
        "inch" -> fromValue = 2.54;
        "foot" -> fromValue = 30.48;
        "yard" -> fromValue = 91.44;
        "mile" -> fromValue = 160934.0;

        "kilogram"-> fromValue = 1.0;
        "pound" -> fromValue = 0.453592;
        "ounce" -> fromValue = 0.0283495;
        "ton" -> fromValue = 907.185;
    }

    when (to) {
        "centimeter" -> toValue = 1.0;
        "inch" -> toValue = 2.54;
        "foot" -> toValue = 30.48;
        "yard" -> toValue = 91.44;
        "mile" -> toValue = 160934.0;

        "kilogram"-> toValue = 1.0;
        "pound" -> toValue = 0.453592;
        "ounce" -> toValue = 0.0283495;
        "ton" -> toValue = 907.185;
    }

    result = (fromValue * value.toDouble() / toValue);
    // Round the value to 3 decimal place.
    // result = (result * 1000.0).toInt() / 1000.0;

    return result;
}

// UI used to select conversion unit
@Composable
fun UnitSelectorDropdown(label: String, conversionType: String, convertUnit: String, onUpdateConvertUnit: (String)->Unit, modifier: Modifier = Modifier ) {

    var showDropdown by remember { mutableStateOf(false) }

    // conversionType can be "Length", "Weight", "Temperature"
    val lengthUnits = listOf("centimeter", "inch", "foot", "yard", "mile");
    val weightUnits = listOf("kilogram", "pound", "ounce", "ton");
    val temperatureUnits = listOf("Celsius", "Fahrenheit", "Kelvin");

    var units = listOf<String>();

    when (conversionType) {
        "Length" -> units = lengthUnits;
        "Weight" -> units = weightUnits;
        "Temperature" -> units = temperatureUnits;
    }

    Column(
        modifier = modifier
    ) {
        Text(label);
        Box() {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDropdown = true;
                    }
                    .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text( text = convertUnit.ifEmpty { "Select" })
                Icon(
                    Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                )
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false}
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            onUpdateConvertUnit(unit);
                            showDropdown = false;
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitConverterPreview() {
    Task2Theme {
        UnitConverterMain()
    }
}