package com.example.fittrack.ui.ui_elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fittrack.R
/**
 * Clase que contiene la ruta el nombre y el icono del navar
 */
sealed class NavItem(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector
) {
    data object Home : NavItem("home", R.string.home, Icons.Filled.Home)
    data object ExerciseList : NavItem("exercise_list", R.string.exercise_list, Icons.Filled.Menu)
    data object Notes : NavItem("notes", R.string.notes, Icons.Filled.Book)
    data object Profile : NavItem("profile", R.string.profile, Icons.Filled.Person)
}
/**
 * Barra de navegacion esta en todas las paginas
 */
@Composable
fun NavBar(navController: NavController) {
    val items = listOf(NavItem.Home, NavItem.ExerciseList, NavItem.Notes, NavItem.Profile) // anyado los NavItem a la lisat
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
    ) {
        //genera tantos como haya en la lista
        items.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.padding(horizontal = 0.dp),
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(id = item.labelResId),
                        modifier = Modifier.padding(0.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(id = item.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}