package com.still.clock.ui

import androidx.compose.ui.graphics.Color
import com.still.clock.data.Atmosphere

data class ClockPalette(
    val background: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val coreLight: Color,
    val coreDark: Color,
)

val Atmosphere.palette: ClockPalette
    get() = when (this) {
        Atmosphere.ECLIPSE -> ClockPalette(
            background = Color(0xFF070809),
            primary = Color(0xFFF1EEE7),
            secondary = Color(0xFF918D84),
            accent = Color(0xFFD7B77A),
            coreLight = Color(0xFF24231F),
            coreDark = Color(0xFF0A0B0C),
        )

        Atmosphere.DAWN -> ClockPalette(
            background = Color(0xFF080B0E),
            primary = Color(0xFFEEF3F6),
            secondary = Color(0xFF81909A),
            accent = Color(0xFFC7D8E4),
            coreLight = Color(0xFF1B252C),
            coreDark = Color(0xFF090C0F),
        )

        Atmosphere.EMBER -> ClockPalette(
            background = Color(0xFF0B0808),
            primary = Color(0xFFF2EAE7),
            secondary = Color(0xFF987F78),
            accent = Color(0xFFD78F7E),
            coreLight = Color(0xFF281C1A),
            coreDark = Color(0xFF0D0909),
        )
    }

val Atmosphere.label: String
    get() = when (this) {
        Atmosphere.ECLIPSE -> "月蚀"
        Atmosphere.DAWN -> "晨雾"
        Atmosphere.EMBER -> "余烬"
    }
