package com.example.animationexpo.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.animationexpo.ui.theme.CanvasMist
import com.example.animationexpo.ui.theme.CanvasWarm
import com.example.animationexpo.ui.theme.Ink
import com.example.animationexpo.ui.theme.InkSoft
import com.example.animationexpo.ui.theme.PaperWhite
import com.example.animationexpo.ui.theme.TravelOrange

@Composable
fun AnimationGalleryScreen(
    demos: List<AnimationDemoSpec>,
    onDemoSelected: (AnimationDemoSpec) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CanvasWarm, Color(0xFFF1E6D7), Color(0xFFEADFD2))
                )
            )
    ) {
        GalleryAtmosphere()

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 172.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                GalleryHeader(demoCount = demos.size)
            }

            items(demos, key = { it.id }) { demo ->
                DemoCard(
                    demo = demo,
                    onClick = { onDemoSelected(demo) }
                )
            }
        }
    }
}

@Composable
fun AnimationDemoDetailScreen(
    demo: AnimationDemoSpec,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        demo.screen(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.testTag("demo-back"),
                onClick = onBack,
                shape = CircleShape,
                color = PaperWhite.copy(alpha = 0.92f),
                contentColor = Ink,
                shadowElevation = 8.dp
            ) {
                Text(
                    text = "Back",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.74f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = demo.category.uppercase(),
                    color = demo.accentColors.first().copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = demo.title,
                    color = Ink,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .safeDrawingPadding()
                .padding(18.dp),
            shape = RoundedCornerShape(26.dp),
            color = Color.White.copy(alpha = 0.78f),
            contentColor = InkSoft,
            shadowElevation = 10.dp
        ) {
            Text(
                text = demo.interactionHint,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun GalleryHeader(demoCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderPill(
                text = "Motion studies",
                brush = Brush.horizontalGradient(
                    listOf(TravelOrange.copy(alpha = 0.92f), Color(0xFFF79A42))
                ),
                contentColor = Color.White
            )
            HeaderPill(
                text = "$demoCount demos",
                brush = Brush.horizontalGradient(
                    listOf(Color.White.copy(alpha = 0.88f), CanvasMist.copy(alpha = 0.94f))
                ),
                contentColor = Ink
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Animation Expo",
                color = Ink,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "A gallery of tactile motion experiments. Start with cloth physics, then move into ixigo travel-brand animation.",
                color = InkSoft,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun HeaderPill(
    text: String,
    brush: Brush,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(brush)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun DemoCard(
    demo: AnimationDemoSpec,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .testTag("demo-card-${demo.id}")
            .fillMaxWidth()
            .height(270.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = PaperWhite.copy(alpha = 0.9f),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                demo.accentColors[0].copy(alpha = 0.26f),
                                demo.accentColors[1].copy(alpha = 0.48f),
                                demo.accentColors.last().copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                demo.poster.invoke(this)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardTag(text = demo.category, shape = RoundedCornerShape(16.dp))
                    CardTag(
                        text = demo.thumbnailStyle.name.lowercase(),
                        shape = RoundedCornerShape(16.dp),
                        textColor = demo.accentColors.first()
                    )
                }

                Text(
                    text = demo.title,
                    color = Ink,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = demo.subtitle,
                    color = InkSoft,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CardTag(
    text: String,
    shape: Shape,
    textColor: Color = Ink
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(Color(0xFFF1E8DD))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun BoxScope.GalleryAtmosphere() {
    AtmosphereOrb(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 56.dp, end = 24.dp)
            .size(220.dp),
        colors = listOf(Color(0x33F97316), Color(0x00F97316))
    )
    AtmosphereOrb(
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 8.dp)
            .size(180.dp),
        colors = listOf(Color(0x1A345A8A), Color(0x00345A8A))
    )
    AtmosphereOrb(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 12.dp, bottom = 120.dp)
            .size(260.dp),
        colors = listOf(Color(0x22FFFFFF), Color(0x00FFFFFF))
    )
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
