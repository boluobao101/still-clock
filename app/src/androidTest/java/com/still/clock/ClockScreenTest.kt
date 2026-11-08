package com.still.clock

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.still.clock.data.Atmosphere
import com.still.clock.data.ClockSettings
import com.still.clock.ui.ClockScreen
import org.junit.Rule
import org.junit.Test

class ClockScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun controlsChangeSettingsAndPureModeCanAlwaysBeExited() {
        composeRule.setContent {
            val settings = remember {
                mutableStateOf(ClockSettings(atmosphere = Atmosphere.ECLIPSE))
            }
            ClockScreen(
                settings = settings.value,
                onSettingsChange = { settings.value = it },
            )
        }

        composeRule.onNodeWithContentDescription("显示时钟设置").performClick()
        composeRule.onNodeWithText("24 小时").assertIsOn().performClick().assertIsOff()
        composeRule.onNodeWithText("秒轨").assertIsOn().performClick().assertIsOff()
        composeRule.onNodeWithText("常亮").assertIsOff().performClick().assertIsOn()
        composeRule.onNodeWithContentDescription("光影 晨雾")
            .performClick()
            .assertIsSelected()

        composeRule.onNodeWithText("纯净模式").performClick()
        composeRule.onNodeWithText("STILL").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("退出纯净模式").performClick()
        composeRule.onNodeWithText("STILL").assertIsDisplayed()
    }
}
