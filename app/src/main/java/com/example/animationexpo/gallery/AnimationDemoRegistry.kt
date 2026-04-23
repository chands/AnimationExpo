package com.example.animationexpo.gallery

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animationexpo.ixigo.IxigoLogoAnimationScreen
import com.example.animationexpo.receipt.InteractiveReceiptScreen
import com.example.animationexpo.ui.theme.CanvasMist
import com.example.animationexpo.ui.theme.Ink
import com.example.animationexpo.ui.theme.JourneyBlue
import com.example.animationexpo.ui.theme.PaperWhite
import com.example.animationexpo.ui.theme.TravelOrange
import com.example.animationexpo.ui.theme.TravelOrangeBright

sealed interface AnimationDestination {
    data object Gallery : AnimationDestination
    data class Demo(val demoId: String) : AnimationDestination
}

enum class ThumbnailStyle {
    Poster,
    Diagram
}

data class AnimationDemoSpec(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val thumbnailStyle: ThumbnailStyle,
    val interactionHint: String,
    val accentColors: List<Color>,
    val destination: AnimationDestination,
    val poster: @Composable BoxScope.() -> Unit,
    val screen: @Composable (modifier: Modifier) -> Unit
)

fun animationDemoSpecs(): List<AnimationDemoSpec> {
    return listOf(
        AnimationDemoSpec(
            id = "receipt",
            title = "Receipt Cloth",
            subtitle = "Grab a hanging receipt and deform the mesh in real time.",
            category = "Interactive Physics",
            thumbnailStyle = ThumbnailStyle.Diagram,
            interactionHint = "Drag the receipt to pull the cloth simulation around.",
            accentColors = listOf(Color(0xFFEAE4DB), CanvasMist, Color(0xFFCFC3B2)),
            destination = AnimationDestination.Demo("receipt"),
            poster = { ReceiptPosterArtwork() },
            screen = { modifier ->
                InteractiveReceiptScreen(
                    modifier = modifier,
                    showHint = false
                )
            }
        ),
        AnimationDemoSpec(
            id = "ixigo-logo",
            title = "ixigo Route Reveal",
            subtitle = "A travel path sweeps forward, then resolves into the brand mark.",
            category = "Brand Motion",
            thumbnailStyle = ThumbnailStyle.Poster,
            interactionHint = "Tap to replay, or drag left and right to scrub the route reveal.",
            accentColors = listOf(TravelOrangeBright, TravelOrange, Color(0xFFFFC27A)),
            destination = AnimationDestination.Demo("ixigo-logo"),
            poster = { IxigoPosterArtwork() },
            screen = { modifier ->
                IxigoLogoAnimationScreen(modifier = modifier)
            }
        )
    )
}

@Composable
private fun ReceiptPosterArtwork() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val pegY = size.height * 0.16f
        val centerX = size.width * 0.5f
        val paperWidth = size.width * 0.42f
        val paperHeight = size.height * 0.56f
        val left = centerX - paperWidth / 2f
        val top = size.height * 0.22f

        drawLine(
            color = Color(0x665E554B),
            start = Offset(centerX, pegY),
            end = Offset(centerX, top),
            strokeWidth = 5f
        )
        drawCircle(
            color = Color(0xFF6A5E52),
            radius = size.minDimension * 0.028f,
            center = Offset(centerX, pegY)
        )

        val receiptPath = Path().apply {
            moveTo(left, top)
            lineTo(left + paperWidth, top)
            lineTo(left + paperWidth, top + paperHeight * 0.82f)
            cubicTo(
                left + paperWidth * 0.86f, top + paperHeight * 0.94f,
                left + paperWidth * 0.70f, top + paperHeight * 0.90f,
                left + paperWidth * 0.58f, top + paperHeight
            )
            cubicTo(
                left + paperWidth * 0.44f, top + paperHeight * 0.92f,
                left + paperWidth * 0.28f, top + paperHeight * 0.98f,
                left + paperWidth * 0.14f, top + paperHeight * 0.88f
            )
            lineTo(left, top + paperHeight * 0.78f)
            close()
        }

        drawPath(
            path = receiptPath,
            brush = Brush.verticalGradient(
                colors = listOf(PaperWhite, Color(0xFFF0E7D7))
            )
        )
        drawPath(
            path = receiptPath,
            color = Color(0x1A000000),
            style = Stroke(width = 3f)
        )

        repeat(5) { index ->
            val y = top + paperHeight * (0.14f + index * 0.12f)
            drawRoundRect(
                color = Color(0x18000000),
                topLeft = Offset(left + paperWidth * 0.12f, y),
                size = Size(paperWidth * (0.58f + (index % 2) * 0.14f), 7f),
                cornerRadius = CornerRadius(7f, 7f)
            )
        }
    }
}

@Composable
private fun IxigoPosterArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val path = Path().apply {
                moveTo(size.width * 0.06f, size.height * 0.72f)
                cubicTo(
                    size.width * 0.28f, size.height * 0.34f,
                    size.width * 0.44f, size.height * 0.82f,
                    size.width * 0.70f, size.height * 0.46f
                )
            }
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.3f),
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 10f,
                center = Offset(size.width * 0.06f, size.height * 0.72f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.55f),
                radius = 8f,
                center = Offset(size.width * 0.42f, size.height * 0.61f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = 11f,
                center = Offset(size.width * 0.70f, size.height * 0.46f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(TravelOrangeBright, TravelOrange)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ixigo",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.2).sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == 2) 9.dp else 7.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.88f))
                        )
                    }
                }
            }
        }

        Text(
            text = "Route trace",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 4.dp),
            color = Ink.copy(alpha = 0.62f),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "Search to departure",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 4.dp),
            color = JourneyBlue.copy(alpha = 0.68f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
