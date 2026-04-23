package com.example.animationexpo.ixigo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val RouteOrange = Color(0xFFF97316)
private val RouteOrangeSoft = Color(0xFFFFAE52)
private val RouteInk = Color(0xFF1F2023)
private val RouteCream = Color(0xFFFFFBF4)
private val RouteBlue = Color(0xFF2A5887)

@Composable
fun IxigoLogoAnimationScreen(
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    var widthPx by remember { mutableFloatStateOf(1f) }
    val scope = rememberCoroutineScope()
    val pulseTransition = rememberInfiniteTransition(label = "ixigo-idle")
    val idlePulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ixigo-idle-pulse"
    )

    LaunchedEffect(Unit) {
        replay(progress)
    }

    val pillReveal = revealWindow(progress.value, start = 0.34f, end = 0.74f)
    val textReveal = revealWindow(progress.value, start = 0.54f, end = 0.88f)
    val pillScale = if (progress.value >= 0.98f) 1f + idlePulse * 0.018f else 1f

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF4E7),
                        Color(0xFFFFE4C4),
                        Color(0xFFF6E7D5)
                    )
                )
            )
            .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(widthPx) {
                detectDragGestures(
                    onDragStart = {
                        scope.launch { progress.stop() }
                    },
                    onDragEnd = {
                        if (progress.value < 1f) {
                            scope.launch {
                                progress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = ((1f - progress.value) * 1100f)
                                            .roundToInt()
                                            .coerceIn(280, 1100),
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        if (progress.value < 1f) {
                            scope.launch {
                                progress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = ((1f - progress.value) * 900f)
                                            .roundToInt()
                                            .coerceIn(260, 900),
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val target = (progress.value + dragAmount.x / widthPx).coerceIn(0f, 1f)
                    scope.launch {
                        progress.snapTo(target)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            replay(progress)
                        }
                    }
                )
            }
    ) {
        IxigoAtmosphere()

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 72.dp)
        ) {
            val start = Offset(size.width * 0.08f, size.height * 0.70f)
            val control1 = Offset(size.width * 0.30f, size.height * 0.33f)
            val control2 = Offset(size.width * 0.46f, size.height * 0.84f)
            val end = Offset(size.width * 0.73f, size.height * 0.46f)

            val basePath = buildBezierPath(
                start = start,
                control1 = control1,
                control2 = control2,
                end = end,
                progress = 1f
            )
            val activePath = buildBezierPath(
                start = start,
                control1 = control1,
                control2 = control2,
                end = end,
                progress = progress.value
            )
            val currentPoint = cubicPoint(
                t = progress.value,
                p0 = start,
                p1 = control1,
                p2 = control2,
                p3 = end
            )

            drawPath(
                path = basePath,
                color = Color.White.copy(alpha = 0.42f),
                style = Stroke(width = 14f, cap = StrokeCap.Round)
            )
            drawPath(
                path = activePath,
                brush = Brush.linearGradient(
                    colors = listOf(RouteOrangeSoft, RouteOrange, RouteBlue),
                    start = start,
                    end = end
                ),
                style = Stroke(width = 18f, cap = StrokeCap.Round)
            )

            routeStops().forEach { stop ->
                val stopPoint = cubicPoint(stop, start, control1, control2, end)
                val reached = progress.value >= stop
                drawCircle(
                    color = if (reached) RouteOrange else Color.White.copy(alpha = 0.78f),
                    radius = if (reached) 12f else 9f,
                    center = stopPoint
                )
                drawCircle(
                    color = Color.White.copy(alpha = if (reached) 0.98f else 0.68f),
                    radius = if (reached) 5f else 4f,
                    center = stopPoint
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.92f), Color.Transparent),
                    center = currentPoint,
                    radius = 56f
                ),
                radius = 38f,
                center = currentPoint,
                blendMode = BlendMode.Plus
            )
            drawCircle(
                color = Color.White,
                radius = 13f,
                center = currentPoint
            )
            drawCircle(
                color = RouteOrange,
                radius = 7f,
                center = currentPoint
            )
        }

        RouteStage(
            modifier = Modifier.align(Alignment.Center),
            pillReveal = pillReveal,
            textReveal = textReveal,
            pillScale = pillScale
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 118.dp, end = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StepPill(text = "search")
            StepPill(text = "book")
            StepPill(text = "go")
        }
    }
}

@Composable
private fun BoxScope.IxigoAtmosphere() {
    AtmosphereOrb(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = 34.dp, start = 24.dp)
            .size(180.dp),
        colors = listOf(Color(0x33FF9A3E), Color.Transparent)
    )
    AtmosphereOrb(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 12.dp, bottom = 126.dp)
            .size(220.dp),
        colors = listOf(Color(0x225A85B8), Color.Transparent)
    )
}

@Composable
private fun RouteStage(
    modifier: Modifier,
    pillReveal: Float,
    textReveal: Float,
    pillScale: Float
) {
    Box(
        modifier = modifier
            .size(
                width = lerp(92.dp, 238.dp, pillReveal),
                height = lerp(58.dp, 102.dp, pillReveal)
            )
            .graphicsLayer {
                alpha = pillReveal.coerceAtLeast(0.08f)
                scaleX = (0.82f + pillReveal * 0.18f) * pillScale
                scaleY = (0.9f + pillReveal * 0.1f) * pillScale
            }
            .clip(RoundedCornerShape(34.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(RouteOrangeSoft, RouteOrange)
                )
            )
            .padding(horizontal = lerp(18.dp, 30.dp, pillReveal), vertical = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "ixigo",
            modifier = Modifier
                .drawWithContent {
                    clipRect(right = size.width * textReveal) {
                        this@drawWithContent.drawContent()
                    }
                }
                .graphicsLayer { alpha = textReveal.coerceAtLeast(0.05f) },
            color = Color.White,
            fontSize = 44.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.4).sp
        )
    }
}

@Composable
private fun StepPill(text: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(RouteCream.copy(alpha = 0.74f))
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = RouteInk.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun AtmosphereOrb(
    modifier: Modifier,
    colors: List<Color>
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.radialGradient(colors))
    )
}

private suspend fun replay(progress: Animatable<Float, *>) {
    progress.snapTo(0f)
    progress.animateTo(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 2100,
            easing = FastOutSlowInEasing
        )
    )
}

private fun routeStops(): List<Float> = listOf(0f, 0.42f, 1f)

private fun revealWindow(value: Float, start: Float, end: Float): Float {
    return ((value - start) / (end - start)).coerceIn(0f, 1f)
}

private fun buildBezierPath(
    start: Offset,
    control1: Offset,
    control2: Offset,
    end: Offset,
    progress: Float,
    segments: Int = 84
): Path {
    val clamped = progress.coerceIn(0f, 1f)
    val path = Path().apply { moveTo(start.x, start.y) }

    if (clamped == 0f) {
        return path
    }

    val steps = (segments * clamped).roundToInt().coerceAtLeast(1)
    for (step in 1..steps) {
        val t = clamped * (step / steps.toFloat())
        val point = cubicPoint(t, start, control1, control2, end)
        path.lineTo(point.x, point.y)
    }

    return path
}

private fun cubicPoint(
    t: Float,
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset
): Offset {
    val oneMinusT = 1f - t
    val x = oneMinusT * oneMinusT * oneMinusT * p0.x +
        3f * oneMinusT * oneMinusT * t * p1.x +
        3f * oneMinusT * t * t * p2.x +
        t * t * t * p3.x
    val y = oneMinusT * oneMinusT * oneMinusT * p0.y +
        3f * oneMinusT * oneMinusT * t * p1.y +
        3f * oneMinusT * t * t * p2.y +
        t * t * t * p3.y
    return Offset(x, y)
}
