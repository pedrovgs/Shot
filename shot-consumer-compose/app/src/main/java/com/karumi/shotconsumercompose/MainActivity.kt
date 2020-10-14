package com.karumi.shotconsumercompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.ui.tooling.preview.Preview
import com.karumi.shotconsumercompose.ui.ShotConsumerComposeTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Box
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShotConsumerComposeTheme {
                // A surface container using the 'background' color from the theme
                ScrollableColumn {
                    Greeting("Android")
                    RoundedCornersBox()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShotConsumerComposeTheme {
        Greeting("Android")
    }
}

@Preview
@Composable
fun RoundedCornersBox() {
    Box(Modifier.background(color = Color(0xFF4E62FD), shape = RoundedCornerShape(size = 20.dp)).width(100.dp).height(100.dp))
}