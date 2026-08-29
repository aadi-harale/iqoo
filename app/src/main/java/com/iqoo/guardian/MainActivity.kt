package com.iqoo.guardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.iqoo.guardian.ui.GuardianRoot
import com.iqoo.guardian.ui.theme.GBackground
import com.iqoo.guardian.ui.theme.IqooGuardianTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val repository = (application as GuardianApplication).repository
        setContent {
            IqooGuardianTheme {
                Surface(modifier = Modifier.fillMaxSize().background(GBackground)) {
                    GuardianRoot(repository = repository)
                }
            }
        }
    }
}
