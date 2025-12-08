package com.example.weathernow.view.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.weathernow.R
import com.example.weathernow.theme.InputFieldColor
import com.example.weathernow.theme.TextColorDark

@Composable
fun BackGroundImage() {
    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = "Fondo de ciclista",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun EmailField(email: String, error: String?, onEmailChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text(text = "Correo", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        isError = error != null,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldColor,
            unfocusedContainerColor = InputFieldColor,
            disabledContainerColor = InputFieldColor,
            cursorColor = TextColorDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = "Logo de WeatherNow",
        modifier = modifier.size(100.dp)
    )
}

@Composable
fun ErrorField(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
