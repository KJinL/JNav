package com.kajin.navtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kajin.nav.NavigationEffect
import com.kajin.nav.composableWithDestination
import com.kajin.nav.params
import com.kajin.nav.result
import com.kajin.navtest.ui.FirstPage
import com.kajin.navtest.ui.FourthPage
import com.kajin.navtest.ui.SecondPage
import com.kajin.navtest.ui.ThirdPage
import com.kajin.navtest.ui.User
import com.kajin.navtest.ui.theme.JNavTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JNavTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    NavigationEffect(startDestination = FirstDestination.route) {
        composableWithDestination(FirstDestination) {
            val result = it.result(String::class.java)
            FirstPage(result)
        }
        composableWithDestination(SecondDestination) {
            SecondPage()
        }
        composableWithDestination(ThirdDestination) {
            val params = it.params(User::class.java)
            ThirdPage(params)
        }
        composableWithDestination(FourthDestination) {
            FourthPage()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JNavTheme {
        Greeting()
    }
}