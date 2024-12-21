package com.singularityindonesia.modelfirstprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.singularityindonesia.modelfirstprogramming.core.theme.ModelFirstProgrammingTheme
import com.singularityindonesia.modelfirstprogramming.model.PageTitle
import com.singularityindonesia.modelfirstprogramming.scene.MainNavigation

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var pageTitle by remember { mutableStateOf(PageTitle("Home")) }
            ModelFirstProgrammingTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = pageTitle.value,
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    MainNavigation(
                        modifier = Modifier.padding(innerPadding),
                        onNavigate = {
                            pageTitle = it
                        }
                    )
                }
            }
        }
    }
}
