package com.singularityindonesia.modelfirstprogramming.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.singularityindonesia.modelfirstprogramming.model.PageTitle
import com.singularityindonesia.modelfirstprogramming.scene.editprofile.EditProfilePane
import com.singularityindonesia.modelfirstprogramming.scene.home.HomeScenePane
import com.singularityindonesia.modelfirstprogramming.scene.profile.ProfileScenePane

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    onNavigate: (PageTitle) -> Unit,
    onLoading: (Boolean) -> Unit,
    setBackAction: ((() -> Unit)?) -> Unit
) {
    val controller = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = "home"
    ) {
        composable(route = "home") {
            DisposableEffect(Unit) {
                onNavigate.invoke(PageTitle("Home"))
                setBackAction.invoke(null)
                onDispose { }
            }
            HomeScenePane(
                gotoProfile = {
                    controller.navigate("profile")
                },
                onLoading = onLoading
            )
        }

        composable(route = "profile") {
            DisposableEffect(Unit) {
                onNavigate.invoke(PageTitle("Profile"))
                setBackAction.invoke {
                    controller.popBackStack()
                }
                onDispose { }
            }
            ProfileScenePane(
                gotoEditProfile = {
                    controller.navigate("profile/edit")
                },
                onLoading = onLoading
            )
        }

        composable(route = "profile/edit") {
            DisposableEffect(Unit) {
                onNavigate.invoke(PageTitle("Edit Profile"))
                setBackAction.invoke {
                    controller.popBackStack()
                }
                onDispose { }
            }
            EditProfilePane(
                navigateBack = { controller.popBackStack() },
                onLoading = onLoading
            )
        }
    }
}