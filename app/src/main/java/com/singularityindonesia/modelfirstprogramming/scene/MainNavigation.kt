package com.singularityindonesia.modelfirstprogramming.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
) {
    val controller = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = "home"
    ) {
        composable(route = "home") {
            LaunchedEffect(Unit) {
                onNavigate.invoke(PageTitle("Home"))
            }
            HomeScenePane(
                gotoProfile = {
                    controller.navigate("profile")
                },
                onLoading = onLoading
            )
        }

        composable(route = "profile") {
            LaunchedEffect(Unit) {
                onNavigate.invoke(PageTitle("Profile"))
            }
            ProfileScenePane(
                navigateBack = {
                    controller.popBackStack()
                },
                gotoEditProfile = {
                    controller.navigate("profile/edit")
                },
                onLoading = onLoading
            )
        }

        composable(route = "profile/edit") {
            LaunchedEffect(Unit) {
                onNavigate.invoke(PageTitle("Edit Profile"))
            }
            EditProfilePane(
                navigateBack = { controller.popBackStack() },
                onLoading = onLoading
            )
        }
    }
}