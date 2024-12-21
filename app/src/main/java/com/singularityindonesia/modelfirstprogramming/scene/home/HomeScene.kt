package com.singularityindonesia.modelfirstprogramming.scene.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.singularityindonesia.modelfirstprogramming.model.User

@Composable
fun HomeScenePane(
    onLoading: (isLoading: Boolean) -> Unit,
    gotoProfile: () -> Unit
) {
    val user = remember { User.get() }
    val userIsSynchronizing by user.isSynchronizing.collectAsStateWithLifecycle()
    val userName by user.name.collectAsStateWithLifecycle()

    LaunchedEffect(userIsSynchronizing) {
        onLoading.invoke(userIsSynchronizing)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Welcome home ${userName.value}")
        Text(
            text = "Note: We are emulating 5 second delay, you don't need to wait until the process returns success, because everything is syncronized.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = gotoProfile
        ) {
            Text("Goto Profile")
        }
    }
}