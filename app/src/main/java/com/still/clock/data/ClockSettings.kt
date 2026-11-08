package com.still.clock.data

import android.content.Context
import android.text.format.DateFormat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.clockDataStore by preferencesDataStore(name = "clock_settings")

enum class Atmosphere {
    ECLIPSE,
    DAWN,
    EMBER,
}

data class ClockSettings(
    val use24Hour: Boolean = true,
    val showSeconds: Boolean = true,
    val keepScreenOn: Boolean = false,
    val atmosphere: Atmosphere = Atmosphere.ECLIPSE,
)

class ClockSettingsRepository(private val context: Context) {
    private val systemUses24Hour = DateFormat.is24HourFormat(context)

    val settings: Flow<ClockSettings> = context.clockDataStore.data.map { preferences ->
        ClockSettings(
            use24Hour = preferences[USE_24_HOUR] ?: systemUses24Hour,
            showSeconds = preferences[SHOW_SECONDS] ?: true,
            keepScreenOn = preferences[KEEP_SCREEN_ON] ?: false,
            atmosphere = preferences[ATMOSPHERE]
                ?.let { name -> Atmosphere.entries.firstOrNull { it.name == name } }
                ?: Atmosphere.ECLIPSE,
        )
    }

    suspend fun update(settings: ClockSettings) {
        context.clockDataStore.edit { preferences ->
            preferences[USE_24_HOUR] = settings.use24Hour
            preferences[SHOW_SECONDS] = settings.showSeconds
            preferences[KEEP_SCREEN_ON] = settings.keepScreenOn
            preferences[ATMOSPHERE] = settings.atmosphere.name
        }
    }

    private companion object {
        val USE_24_HOUR = booleanPreferencesKey("use_24_hour")
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val ATMOSPHERE = stringPreferencesKey("atmosphere")
    }
}
