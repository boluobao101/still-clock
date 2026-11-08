package com.still.clock.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.still.clock.data.Atmosphere
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.still.clock.data.ClockSettings
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

@Composable
fun ClockScreen(
    settings: ClockSettings,
    onSettingsChange: (ClockSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = settings.atmosphere.palette
    val locale = LocalConfiguration.current.locales[0]
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val isResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    var now by remember { mutableStateOf(ZonedDateTime.now()) }
    var controlsVisible by rememberSaveable { mutableStateOf(false) }
    var pureMode by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(locale, isResumed) {
        if (isResumed) {
            while (true) {
                now = ZonedDateTime.now()
                val untilNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
                delay(untilNextMinute + 20L)
            }
        }
    }

    val display = ClockFormatter.format(now, settings.use24Hour, locale)
    val toggleSurface = {
        if (pureMode) {
            pureMode = false
            controlsVisible = false
        } else {
            controlsVisible = !controlsVisible
        }
    }

    BackHandler(enabled = controlsVisible || pureMode) {
        if (controlsVisible) controlsVisible = false else pureMode = false
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        val landscape = maxWidth > maxHeight
        val density = LocalDensity.current
        val horizontalPadding = if (landscape) 40.dp else 28.dp
        val verticalPadding = if (landscape) 22.dp else 28.dp
        val timeFontSize = with(density) {
            if (landscape) {
                minOf(maxHeight * 0.26f, maxWidth * 0.14f).toSp()
            } else {
                minOf(maxWidth * 0.255f, 116.dp).toSp()
            }
        }

        ClockCanvas(
            palette = palette,
            showSeconds = settings.showSeconds,
            animate = isResumed,
            modifier = Modifier.fillMaxSize(),
        )

        val surfaceDescription = when {
            pureMode -> "退出纯净模式"
            controlsVisible -> "隐藏时钟设置"
            else -> "显示时钟设置"
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = surfaceDescription
                    onClick(label = surfaceDescription) {
                        toggleSurface()
                        true
                    }
                }
                .pointerInput(pureMode, controlsVisible) {
                    detectTapGestures { toggleSurface() }
                },
        )

        AnimatedVisibility(
            visible = !pureMode,
            enter = fadeIn(animationSpec = tween(650)),
            exit = fadeOut(animationSpec = tween(450)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = verticalPadding,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        contentDescription = "息 STILL"
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(1.dp)
                            .background(palette.accent.copy(alpha = 0.75f)),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "息",
                        color = palette.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "STILL",
                        color = palette.secondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 4.sp,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(if (landscape) 0.60f else 1f),
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        AnimatedContent(
                            targetState = display.time,
                            transitionSpec = {
                                (
                                    fadeIn(animationSpec = tween(600)) +
                                        slideInVertically(animationSpec = tween(600)) { it / 9 }
                                    ) togetherWith (
                                    fadeOut(animationSpec = tween(360)) +
                                        slideOutVertically(animationSpec = tween(360)) { -it / 12 }
                                    )
                            },
                            label = "minute_transition",
                        ) { time ->
                            Text(
                                text = time,
                                color = palette.primary,
                                maxLines = 1,
                                softWrap = false,
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Light,
                                    fontSize = timeFontSize,
                                    letterSpacing = (-0.055).em,
                                    fontFeatureSettings = "tnum",
                                ),
                                modifier = Modifier.semantics {
                                    contentDescription = display.spokenTime
                                },
                            )
                        }
                        if (display.period.isNotEmpty()) {
                            Text(
                                text = display.period,
                                color = palette.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(1.dp)
                                .background(palette.accent),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = display.date,
                            color = palette.secondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.4.sp,
                            maxLines = 1,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (landscape) 76.dp else 88.dp))
            }
        }

        AnimatedVisibility(
            visible = controlsVisible && !pureMode,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(animationSpec = tween(280)) +
                slideInVertically(animationSpec = tween(360)) { it / 3 },
            exit = fadeOut(animationSpec = tween(220)) +
                slideOutVertically(animationSpec = tween(260)) { it / 3 },
        ) {
            ControlPanel(
                settings = settings,
                palette = palette,
                onSettingsChange = onSettingsChange,
                onPureMode = {
                    pureMode = true
                    controlsVisible = false
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ControlPanel(
    settings: ClockSettings,
    palette: ClockPalette,
    onSettingsChange: (ClockSettings) -> Unit,
    onPureMode: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.background.copy(alpha = 0.96f))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.secondary.copy(alpha = 0.18f)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "光影",
                color = palette.secondary,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Atmosphere.entries.forEach { atmosphere ->
                AtmosphereChoice(
                    atmosphere = atmosphere,
                    selected = settings.atmosphere == atmosphere,
                    palette = palette,
                    onSelect = {
                        onSettingsChange(settings.copy(atmosphere = atmosphere))
                    },
                )
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            maxItemsInEachRow = if (LocalDensity.current.fontScale > 1.2f) 2 else Int.MAX_VALUE,
        ) {
            QuietToggle(
                label = "24 小时",
                checked = settings.use24Hour,
                palette = palette,
                onCheckedChange = { checked ->
                    onSettingsChange(settings.copy(use24Hour = checked))
                },
            )
            QuietToggle(
                label = "秒轨",
                checked = settings.showSeconds,
                palette = palette,
                onCheckedChange = { checked ->
                    onSettingsChange(settings.copy(showSeconds = checked))
                },
            )
            QuietToggle(
                label = "常亮",
                checked = settings.keepScreenOn,
                palette = palette,
                onCheckedChange = { checked ->
                    onSettingsChange(settings.copy(keepScreenOn = checked))
                },
            )
            Text(
                text = "纯净模式",
                color = palette.secondary,
                fontSize = 11.sp,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(role = Role.Button, onClick = onPureMode)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun AtmosphereChoice(
    atmosphere: Atmosphere,
    selected: Boolean,
    palette: ClockPalette,
    onSelect: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.Tab,
            )
            .minimumInteractiveComponentSize()
            .padding(horizontal = 9.dp, vertical = 5.dp)
            .semantics {
                contentDescription = "光影 ${atmosphere.label}"
            },
    ) {
        val swatchColor = atmosphere.palette.accent
        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(swatchColor.copy(alpha = if (selected) 1f else 0.45f))
                .border(
                    width = 1.dp,
                    color = if (selected) swatchColor else Color.Transparent,
                    shape = CircleShape,
                ),
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = atmosphere.label,
            color = if (selected) palette.primary else palette.secondary,
            fontSize = 9.sp,
        )
    }
}

@Composable
private fun QuietToggle(
    label: String,
    checked: Boolean,
    palette: ClockPalette,
    onCheckedChange: (Boolean) -> Unit,
) {
    val lineColor = palette.accent
    Text(
        text = label,
        color = if (checked) palette.primary else palette.secondary,
        fontSize = 11.sp,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .toggleable(
                value = checked,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .drawBehind {
                if (checked) {
                    drawLine(
                        color = lineColor,
                        start = Offset(12.dp.toPx(), size.height - 6.dp.toPx()),
                        end = Offset(size.width - 12.dp.toPx(), size.height - 6.dp.toPx()),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
            }
            .padding(horizontal = 12.dp, vertical = 16.dp),
    )
}
