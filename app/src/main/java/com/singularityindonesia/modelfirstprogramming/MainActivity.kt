package com.singularityindonesia.modelfirstprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.singularityindonesia.modelfirstprogramming.core.component.LinearProgress
import com.singularityindonesia.modelfirstprogramming.core.theme.ModelFirstProgrammingTheme
import com.singularityindonesia.modelfirstprogramming.model.PageTitle
import com.singularityindonesia.modelfirstprogramming.scene.MainNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            var pageTitle by remember { mutableStateOf(PageTitle("Home")) }
            var backAction by remember { mutableStateOf<(() -> Unit)?>(null) }
            var showLoadingIndicator by remember { mutableStateOf(false) }
            ModelFirstProgrammingTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (backAction != null)
                                IconButton(
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .padding(start = 4.dp),
                                    onClick = {
                                        scope.launch {
                                            delay(150.milliseconds)
                                            backAction?.invoke()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "back action",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                            TopAppBar(
                                title = {
                                    Text(
                                        text = pageTitle.value,
                                    )
                                }
                            )
                            if (showLoadingIndicator)
                                LinearProgress(
                                    modifier = Modifier.statusBarsPadding()
                                )
                        }
                    }
                ) { innerPadding ->
                    MainNavigation(
                        modifier = Modifier.padding(innerPadding),
                        onNavigate = {
                            pageTitle = it
                        },
                        onLoading = {
                            showLoadingIndicator = it
                        },
                        setBackAction = {
                            backAction = it
                        }
                    )
                }
            }
        }
    }
}
