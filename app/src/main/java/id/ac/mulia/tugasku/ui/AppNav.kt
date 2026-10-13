package id.ac.mulia.tugasku.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val back: () -> Unit = { nav.popBackStack() }

    NavHost(navController = nav, startDestination = "list") {
        composable("list") {
            ListScreen(
                vm = viewModel(factory = AppViewModelFactory),
                onAdd = { nav.navigate("form") },
                onOpen = { id -> nav.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            DetailScreen(
                vm = viewModel(factory = AppViewModelFactory),
                onBack = back,
                onEdit = { id -> nav.navigate("form?id=$id") }
            )
        }
        // tanpa id = tambah, dengan id = edit
        composable(
            route = "form?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) {
            FormScreen(vm = viewModel(factory = AppViewModelFactory), onBack = back)
        }
    }
}
