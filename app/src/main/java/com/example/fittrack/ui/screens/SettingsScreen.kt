package com.example.fittrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittrack.ui.ui_elements.NavBar

@Composable
fun SettingsScreen(
    navController: NavController, darkTheme: Boolean, onThemeToggle: (Boolean) -> Unit
) {
    Scaffold(
        bottomBar = { NavBar(navController = navController) }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(text = "Ajustes de la aplicación", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Modo Oscuro")

                Switch(
                    checked = darkTheme, onCheckedChange = { onThemeToggle(it) })
            }

        }
    }
}