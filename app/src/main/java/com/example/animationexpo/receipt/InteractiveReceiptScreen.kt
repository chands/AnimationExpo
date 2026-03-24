package com.example.animationexpo.receipt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BACKGROUND_COLOR = Color(0xFFE5E5E5)
private val INDICATOR_BORDER_COLOR = Color(0x40000000)
private val INDICATOR_FILL_COLOR = Color(0x1A000000)
private val HINT_TEXT_COLOR = Color(0xFF888888)

@Composable
fun InteractiveReceiptScreen() {
    val mesh = remember { ReceiptMeshGenerator.generate() }
    val simulation = remember { ClothSimulation(mesh) }
    val textureBitmap = remember { ReceiptTextureGenerator.generate() }
    val renderer = remember { ReceiptRenderer(mesh, textureBitmap) }

    var frameCount by remember { mutableLongStateOf(0L) }
    var grabbedIndex by remember { mutableIntStateOf(-1) }
    var grabDepth by remember { mutableFloatStateOf(0f) }
    var indicatorPosition by remember { mutableStateOf<Offset?>(null) }
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                simulation.step()
                frameCount++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BACKGROUND_COLOR)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: continue

                            when (event.type) {
                                PointerEventType.Press -> {
                                    val pos = change.position
                                    val (idx, depth) = renderer.findClosestParticle(
                                        pos.x, pos.y,
                                        canvasSize.x, canvasSize.y
                                    )
                                    if (idx != -1) {
                                        grabbedIndex = idx
                                        grabDepth = depth
                                        simulation.grabbedIndex = idx
                                        indicatorPosition = pos
                                        change.consume()
                                    }
                                }

                                PointerEventType.Move -> {
                                    if (grabbedIndex != -1) {
                                        val pos = change.position
                                        val (wx, wy, wz) = renderer.getRayWorldPosition(
                                            pos.x, pos.y,
                                            canvasSize.x, canvasSize.y,
                                            grabDepth
                                        )
                                        simulation.moveGrabbedParticle(wx, wy, wz)
                                        indicatorPosition = pos
                                        change.consume()
                                    }
                                }

                                PointerEventType.Release -> {
                                    grabbedIndex = -1
                                    simulation.grabbedIndex = -1
                                    indicatorPosition = null
                                    change.consume()
                                }
                            }
                        }
                    }
                }
        ) {
            canvasSize = Offset(size.width, size.height)

            @Suppress("UNUSED_EXPRESSION")
            frameCount

            renderer.render(this)

            indicatorPosition?.let { pos ->
                drawGrabIndicator(pos)
            }
        }

        Text(
            text = "Grab and drag the receipt",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            style = TextStyle(
                color = HINT_TEXT_COLOR,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        )
    }
}

private fun DrawScope.drawGrabIndicator(position: Offset) {
    drawCircle(
        color = INDICATOR_FILL_COLOR,
        radius = 16.dp.toPx(),
        center = position
    )
    drawCircle(
        color = INDICATOR_BORDER_COLOR,
        radius = 16.dp.toPx(),
        center = position,
        style = Stroke(width = 2.dp.toPx())
    )
}
