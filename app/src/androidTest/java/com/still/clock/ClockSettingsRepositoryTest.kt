package com.still.clock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.still.clock.data.Atmosphere
import com.still.clock.data.ClockSettings
import com.still.clock.data.ClockSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClockSettingsRepositoryTest {
    @Test
    fun settingsSurviveANewRepositoryInstance() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expected = ClockSettings(
            use24Hour = false,
            showSeconds = false,
            keepScreenOn = true,
            atmosphere = Atmosphere.EMBER,
        )

        ClockSettingsRepository(context).update(expected)
        val restored = ClockSettingsRepository(context).settings.first()

        assertEquals(expected, restored)
        ClockSettingsRepository(context).update(ClockSettings())
    }
}
