package com.kajin.navtest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kajin.nav.NavApp
import com.kajin.navtest.FourthDestination
import com.kajin.navtest.SecondDestination
import com.kajin.navtest.ThirdDestination


data class User(
    val name: String,
    val age: Int? = null,
)

@Composable
fun FirstPage(result: String? = null) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (result != null) {
            Text(text = result)
        }
        Button(onClick = {
            NavApp.to(SecondDestination.route)
        }) {
            Text(text = "跳到第二页")
        }
        Button(onClick = { NavApp.to(ThirdDestination.route, params = User("李二狗")) }) {
            Text(text = "带参跳转第三页")
        }
        Button(onClick = { NavApp.to(FourthDestination.route) }) {
            Text(text = "跳到第四页")
        }
    }
}

@Preview
@Composable
fun SecondPagePreview() {
    FirstPage()
}