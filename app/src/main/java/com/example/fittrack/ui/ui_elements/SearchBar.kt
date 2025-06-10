package com.example.fittrack.ui.ui_elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

@Composable
fun SearchBarComposable(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Buscar..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}
