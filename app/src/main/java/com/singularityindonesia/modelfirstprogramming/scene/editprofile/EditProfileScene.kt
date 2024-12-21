package com.singularityindonesia.modelfirstprogramming.scene.editprofile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.singularityindonesia.modelfirstprogramming.core.component.LinearProgress
import com.singularityindonesia.modelfirstprogramming.model.Name
import com.singularityindonesia.modelfirstprogramming.model.User
import kotlinx.coroutines.launch

@Composable
fun EditProfilePane(
    onLoading: (isLoading: Boolean) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val user = remember { User.get() }
    val userName by user.name.collectAsStateWithLifecycle()

    var editedName by remember(userName) { mutableStateOf(userName.value) }
    val userIsSynchronizing by user.isSynchronizing.collectAsStateWithLifecycle()
    var editUserNameIsLoading by remember { mutableStateOf(false) }

    LaunchedEffect(userIsSynchronizing) {
        onLoading.invoke(userIsSynchronizing)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("User Name") },
            value = editedName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                }
            ),
            onValueChange = {
                editedName = it
            }
        )
        Text(
            text = "Note: We are emulating 5 second delay, you don't need to wait until the process returns success, because everything is syncronized.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        if (editUserNameIsLoading)
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.End),
            )
        else
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    scope.launch {
                        editUserNameIsLoading = true
                        user.updateUserName(Name(editedName))
                            .onSuccess {
                                Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show()
                                navigateBack.invoke()
                            }
                        editUserNameIsLoading = false
                    }
                }
            ) {
                Text("Submit")
            }
    }
}