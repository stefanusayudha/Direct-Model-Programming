package com.singularityindonesia.modelfirstprogramming.scene.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.singularityindonesia.modelfirstprogramming.model.User

@Composable
fun ProfileScenePane(
    onLoading: (isLoading: Boolean) -> Unit,
    gotoEditProfile: () -> Unit,
) {
    val user = remember { User.get() }
    val userIsSynchronizing by user.isSynchronizing.collectAsStateWithLifecycle()
    val userName by user.name.collectAsStateWithLifecycle()

    LaunchedEffect(userIsSynchronizing) {
        onLoading.invoke(userIsSynchronizing)
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Profile of ${userName.value}")
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier,
                onClick = gotoEditProfile
            ) {
                Text("Go To Edit Profile")
            }
        }
    }
}