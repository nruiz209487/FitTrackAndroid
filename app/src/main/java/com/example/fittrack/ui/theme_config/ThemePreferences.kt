package com.example.fittrack.ui.theme_config
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// le da contecto al datastore
val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Clase encargada de manejar la preferencia del tema (modo oscuro o claro) de la app.
 */
class ThemePreferences(private val context: Context) {

    companion object {
        // guarda el modo oscuro
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Devuelve a perefeercioa del modo escuro
    // si no se encuentra devuelve falso
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    // guarda el modo osuro en el dataStore
    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}