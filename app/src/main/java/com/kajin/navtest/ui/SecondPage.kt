package com.kajin.navtest.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.Navigation
import com.kajin.nav.NavApp
import com.kajin.navtest.SecondDestination

@Composable
fun SecondPage() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "这是第二页",
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { NavApp.back(result = "123") }
        )
        Button(onClick = {
            NavApp.back()
        }) {
            Text(text = "返回")
        }
    }
}

@Preview
@Composable
fun TwoPagePreview() {

}