/*
 * Copyright (c) 2026 RevenueCat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.revenuecat.awesomepaywalls

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlin.random.Random

enum class PaywallDemo {
  CHRISTMAS,
  NEW_YEAR,
  NEW_YEARS_EVE_FIREWORKS,
  SUMMER,
  PREMIUM,
  GROWING_TREE,
  UNIVERSE,
  DAY_NIGHT,
  UNDERWATER,
  SYNTHWAVE,
  SAKURA,
  FIREFLIES,
  GEOMETRY,
  ATOMIC,
  BIBLE,
}

@Composable
fun PaywallCard(
  title: String,
  subtitle: String,
  background: Brush,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit = {},
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(140.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(background)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.BottomStart,
  ) {
    content()
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
          ),
        )
        .padding(16.dp),
    ) {
      Text(
        text = title,
        style = TextStyle(
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
        ),
      )
      Text(
        text = subtitle,
        style = TextStyle(
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 12.sp,
        ),
      )
    }
  }
}

@Composable
fun ChristmasCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "christmas")
  val snowOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "snow",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val snowflakes = listOf(
      Offset(size.width * 0.2f, (snowOffset * 1.2f) % size.height),
      Offset(size.width * 0.5f, (snowOffset * 0.8f + 30f) % size.height),
      Offset(size.width * 0.8f, (snowOffset * 1.5f + 60f) % size.height),
      Offset(size.width * 0.35f, (snowOffset * 1.0f + 45f) % size.height),
      Offset(size.width * 0.65f, (snowOffset * 1.3f + 15f) % size.height),
    )
    snowflakes.forEach { pos ->
      drawCircle(Color.White.copy(alpha = 0.8f), 3f, pos)
    }
  }
}

@Composable
fun NewYearCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "newyear")
  val sparkle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "sparkle",
  )

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(
      text = "2026",
      style = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFFFFD700).copy(alpha = 0.5f + sparkle * 0.5f),
            Color(0xFFFFA500).copy(alpha = 0.5f + sparkle * 0.5f),
          ),
        ),
      ),
    )
  }
}

@Composable
fun FireworksCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "fireworks")
  val burst by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "burst",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val centerX = size.width * 0.7f
    val centerY = size.height * 0.4f
    val colors = listOf(Color(0xFFFF6B6B), Color(0xFFFFD93D), Color(0xFF6BCB77))

    for (i in 0 until 8) {
      val angle = i * 45f * PI.toFloat() / 180f
      val radius = burst * 40f
      val alpha = (1f - burst).coerceIn(0f, 1f)
      drawCircle(
        color = colors[i % colors.size].copy(alpha = alpha),
        radius = 4f,
        center = Offset(
          centerX + cos(angle) * radius,
          centerY + sin(angle) * radius,
        ),
      )
    }
  }
}

@Composable
fun SummerCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "summer")
  val rayRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(10000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rays",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val sunCenter = Offset(size.width * 0.8f, size.height * 0.3f)

    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFE55C), Color(0xFFFF9500)),
        center = sunCenter,
        radius = 25f,
      ),
      radius = 25f,
      center = sunCenter,
    )

    for (i in 0 until 8) {
      val angle = (i * 45f + rayRotation) * PI.toFloat() / 180f
      drawLine(
        color = Color(0xFFFFD700).copy(alpha = 0.6f),
        start = Offset(sunCenter.x + cos(angle) * 28f, sunCenter.y + sin(angle) * 28f),
        end = Offset(sunCenter.x + cos(angle) * 40f, sunCenter.y + sin(angle) * 40f),
        strokeWidth = 3f,
      )
    }
  }
}

@Composable
fun PremiumCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "premium")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotation",
  )

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(50.dp)) {
      val center = Offset(size.width / 2, size.height / 2)
      drawCircle(
        brush = Brush.sweepGradient(
          colors = listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6),
            Color(0xFFEC4899),
            Color(0xFF6366F1),
          ),
          center = center,
        ),
        radius = size.minDimension / 2,
        center = center,
        style = Stroke(width = 3f),
      )

      for (i in 0 until 6) {
        val angle = (i * 60f + rotation) * PI.toFloat() / 180f
        drawCircle(
          color = Color.White,
          radius = 2f,
          center = Offset(
            center.x + cos(angle) * (size.minDimension / 2 - 5f),
            center.y + sin(angle) * (size.minDimension / 2 - 5f),
          ),
        )
      }
    }

    Box(
      modifier = Modifier
        .size(35.dp)
        .clip(CircleShape)
        .background(
          brush = Brush.linearGradient(
            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
          ),
        ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "PRO",
        style = TextStyle(
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
        ),
      )
    }
  }
}

@Composable
fun GrowingTreeCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "tree")
  val sway by infiniteTransition.animateFloat(
    initialValue = -2f,
    targetValue = 2f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "sway",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val centerX = size.width * 0.3f
    val groundY = size.height * 0.85f

    drawRect(
      color = Color(0xFF5D4037),
      topLeft = Offset(centerX - 5f, groundY - 50f),
      size = androidx.compose.ui.geometry.Size(10f, 50f),
    )

    val leafPositions = listOf(
      Offset(centerX + sway, groundY - 70f) to 20f,
      Offset(centerX - 15f + sway * 0.5f, groundY - 55f) to 12f,
      Offset(centerX + 15f + sway * 0.5f, groundY - 55f) to 12f,
    )

    leafPositions.forEach { (pos, radius) ->
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF32CD32), Color(0xFF228B22)),
          center = pos,
          radius = radius,
        ),
        radius = radius,
        center = pos,
      )
    }
  }
}

@Composable
fun UniverseCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "universe")
  val orbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(6000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "orbit",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val centerX = size.width * 0.5f
    val centerY = size.height * 0.5f

    repeat(15) {
      val x = Random(it).nextFloat() * size.width
      val y = Random(it + 100).nextFloat() * size.height
      drawCircle(Color.White.copy(alpha = 0.6f), 1.5f, Offset(x, y))
    }

    val orbitRadii = listOf(
      size.width * 0.25f,
      size.width * 0.4f,
      size.width * 0.55f,
    )

    orbitRadii.forEach { radius ->
      drawOval(
        color = Color.White.copy(alpha = 0.1f),
        topLeft = Offset(centerX - radius, centerY - radius * 0.2f),
        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 0.4f),
        style = Stroke(width = 1f),
      )
    }

    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFFE55C), Color(0xFFFF6600)),
        center = Offset(centerX, centerY),
        radius = 15f,
      ),
      radius = 15f,
      center = Offset(centerX, centerY),
    )

    val planets = listOf(
      Triple(orbitRadii[0], Color(0xFFB0B0B0), 5f),
      Triple(orbitRadii[1], Color(0xFF4080FF), 7f),
      Triple(orbitRadii[2], Color(0xFFE0C070), 8f),
    )

    planets.forEachIndexed { index, (orbit, color, pSize) ->
      val angle = (orbitAngle * (1.5f - index * 0.3f)) * PI.toFloat() / 180f
      val px = centerX + cos(angle) * orbit
      val py = centerY + sin(angle) * orbit * 0.2f
      drawCircle(color, pSize, Offset(px, py))
    }
  }
}

@Composable
fun DayNightCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "daynight")
  val cycle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "cycle",
  )

  val isDay = cycle < 0.5f

  Crossfade(
    targetState = isDay,
    animationSpec = tween(800),
    label = "daynight_crossfade",
    modifier = modifier.fillMaxSize(),
  ) { day ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = if (day) {
              listOf(Color(0xFF87CEEB), Color(0xFFB8E0F0))
            } else {
              listOf(Color(0xFF1B263B), Color(0xFF2C3E50))
            },
          ),
        ),
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val celestialX = size.width * 0.75f
        val celestialY = size.height * 0.35f

        if (day) {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFFFE55C), Color(0xFFFFA500)),
              center = Offset(celestialX, celestialY),
              radius = 18f,
            ),
            radius = 18f,
            center = Offset(celestialX, celestialY),
          )
        } else {
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFFFFFFF0), Color(0xFFE8E8D0)),
              center = Offset(celestialX, celestialY),
              radius = 15f,
            ),
            radius = 15f,
            center = Offset(celestialX, celestialY),
          )

          repeat(8) {
            val x = Random(it).nextFloat() * size.width
            val y = Random(it + 50).nextFloat() * size.height * 0.6f
            drawCircle(Color.White.copy(alpha = 0.7f), 1.5f, Offset(x, y))
          }
        }
      }
    }
  }
}

@Composable
fun UnderwaterCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "underwater")
  val bubbleOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "bubbles",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    for (i in 0 until 3) {
      val rayX = size.width * (0.2f + i * 0.3f)
      val path = Path().apply {
        moveTo(rayX, 0f)
        lineTo(rayX + 30f, 0f)
        lineTo(rayX + 50f, size.height)
        lineTo(rayX - 20f, size.height)
        close()
      }
      drawPath(
        path = path,
        color = Color.White.copy(alpha = 0.1f),
      )
    }

    val bubbles = listOf(
      Offset(size.width * 0.3f, size.height - (bubbleOffset * 1.2f) % size.height),
      Offset(size.width * 0.6f, size.height - (bubbleOffset * 0.8f + 40f) % size.height),
      Offset(size.width * 0.8f, size.height - (bubbleOffset * 1.5f + 20f) % size.height),
    )
    bubbles.forEach { pos ->
      drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = 6f,
        center = pos,
        style = Stroke(width = 1.5f),
      )
    }
  }
}

@Composable
fun SynthwaveCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "synthwave")
  val gridScroll by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "grid",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val horizonY = size.height * 0.5f

    drawCircle(
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF6EC7), Color(0xFFFF9E00)),
        startY = horizonY - 30f,
        endY = horizonY + 10f,
      ),
      radius = 25f,
      center = Offset(size.width / 2, horizonY - 10f),
    )

    val gridColor = Color(0xFFFF6EC7).copy(alpha = 0.5f)
    for (i in 0..6) {
      val x = i * size.width / 6
      drawLine(
        color = gridColor,
        start = Offset(size.width / 2 + (x - size.width / 2) * 0.2f, horizonY),
        end = Offset(x, size.height),
        strokeWidth = 1f,
      )
    }

    for (i in 0..4) {
      val progress = (i / 4f + gridScroll) % 1f
      val y = horizonY + (size.height - horizonY) * progress
      drawLine(
        color = gridColor.copy(alpha = progress),
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = 1f,
      )
    }
  }
}

@Composable
fun SakuraCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "sakura")
  val petalFall by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 100f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "petals",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    drawLine(
      color = Color(0xFF5D3A29),
      start = Offset(-10f, size.height * 0.2f),
      end = Offset(size.width * 0.6f, size.height * 0.35f),
      strokeWidth = 4f,
      cap = StrokeCap.Round,
    )

    val blossomPositions = listOf(
      Offset(size.width * 0.2f, size.height * 0.25f),
      Offset(size.width * 0.4f, size.height * 0.3f),
      Offset(size.width * 0.55f, size.height * 0.34f),
    )

    blossomPositions.forEach { pos ->
      for (i in 0 until 5) {
        val angle = i * 72f * PI.toFloat() / 180f
        drawCircle(
          color = Color(0xFFFFB7C5),
          radius = 5f,
          center = Offset(pos.x + cos(angle) * 6f, pos.y + sin(angle) * 6f),
        )
      }
      drawCircle(color = Color(0xFFFFE4B5), radius = 3f, center = pos)
    }

    val petals = listOf(
      Offset(size.width * 0.3f, (petalFall * 1.2f) % size.height),
      Offset(size.width * 0.6f, (petalFall * 0.9f + 30f) % size.height),
      Offset(size.width * 0.8f, (petalFall * 1.4f + 50f) % size.height),
    )

    petals.forEach { pos ->
      val path = Path().apply {
        moveTo(pos.x, pos.y - 5f)
        quadraticTo(pos.x + 4f, pos.y, pos.x, pos.y + 5f)
        quadraticTo(pos.x - 4f, pos.y, pos.x, pos.y - 5f)
        close()
      }
      drawPath(path, Color(0xFFFFB7C5).copy(alpha = 0.8f))
    }
  }
}

@Composable
fun FirefliesCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "fireflies")
  val glow by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glow",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    // Stars
    repeat(10) {
      val x = Random(it).nextFloat() * size.width
      val y = Random(it + 100).nextFloat() * size.height * 0.5f
      drawCircle(Color.White.copy(alpha = 0.4f), 1f, Offset(x, y))
    }

    // Fireflies
    val fireflyPositions = listOf(
      Offset(size.width * 0.3f, size.height * 0.3f),
      Offset(size.width * 0.7f, size.height * 0.4f),
      Offset(size.width * 0.5f, size.height * 0.55f),
      Offset(size.width * 0.2f, size.height * 0.5f),
      Offset(size.width * 0.8f, size.height * 0.25f),
    )

    fireflyPositions.forEachIndexed { index, pos ->
      val fireflyGlow = (sin(glow * PI.toFloat() + index * 0.5f) + 1f) / 2f
      val alpha = 0.4f + fireflyGlow * 0.6f

      // Glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFFBBFF57).copy(alpha = alpha * 0.5f),
            Color.Transparent,
          ),
          center = pos,
          radius = 15f,
        ),
        radius = 15f,
        center = pos,
      )

      // Core
      drawCircle(
        color = Color(0xFFFFE066).copy(alpha = alpha),
        radius = 3f,
        center = pos,
      )
    }

    // Grass silhouette
    val grassColor = Color(0xFF1A3D1A)
    for (i in 0 until 8) {
      val x = i * size.width / 7
      val height = 20f + Random(i + 200).nextFloat() * 25f
      val path = Path().apply {
        moveTo(x, size.height)
        quadraticTo(x + 3f, size.height - height * 0.5f, x + 2f, size.height - height)
        quadraticTo(x + 5f, size.height - height * 0.5f, x + 6f, size.height)
        close()
      }
      drawPath(path, grassColor)
    }
  }
}

@Composable
fun GeometryCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "geometry")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(10000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotation",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    // Grid
    val gridColor = Color(0xFF00D4FF).copy(alpha = 0.15f)
    for (i in 0..6) {
      val x = i * size.width / 6
      drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
    }
    for (i in 0..4) {
      val y = i * size.height / 4
      drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
    }

    // Rotating triangle
    val centerX = size.width * 0.3f
    val centerY = size.height * 0.4f
    val triSize = 25f
    val triAngle = rotation * PI.toFloat() / 180f

    val trianglePath = Path().apply {
      for (i in 0 until 3) {
        val angle = triAngle + i * 2 * PI.toFloat() / 3 - PI.toFloat() / 2
        val px = centerX + cos(angle) * triSize
        val py = centerY + sin(angle) * triSize
        if (i == 0) moveTo(px, py) else lineTo(px, py)
      }
      close()
    }
    drawPath(trianglePath, Color(0xFF00D4FF).copy(alpha = 0.6f), style = Stroke(2f))

    // Hexagon
    val hexCenterX = size.width * 0.7f
    val hexCenterY = size.height * 0.5f
    val hexSize = 20f
    val hexPath = Path().apply {
      for (i in 0 until 6) {
        val angle =
          -rotation * 0.5f * PI.toFloat() / 180f + i * PI.toFloat() / 3 - PI.toFloat() / 2
        val px = hexCenterX + cos(angle) * hexSize
        val py = hexCenterY + sin(angle) * hexSize
        if (i == 0) moveTo(px, py) else lineTo(px, py)
      }
      close()
    }
    drawPath(hexPath, Color(0xFF7B68EE).copy(alpha = 0.6f), style = Stroke(2f))

    // Circle
    drawCircle(
      color = Color(0xFFFF6B9D).copy(alpha = 0.5f),
      radius = 15f,
      center = Offset(size.width * 0.5f, size.height * 0.3f),
      style = Stroke(2f),
    )
  }
}

@Composable
fun AtomicCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "atomic")
  val orbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "orbit",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val centerX = size.width * 0.5f
    val centerY = size.height * 0.45f

    // Orbit paths
    val orbitRadii = listOf(25f, 40f)
    val orbitColors = listOf(Color(0xFF00FFFF), Color(0xFFFF00FF))

    orbitRadii.forEachIndexed { index, radius ->
      drawOval(
        color = orbitColors[index].copy(alpha = 0.2f),
        topLeft = Offset(centerX - radius, centerY - radius * 0.3f),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 0.6f),
        style = Stroke(1f),
      )

      // Electrons
      val electronCount = index + 1
      for (i in 0 until electronCount) {
        val speed = if (index == 0) 1f else -0.7f
        val angle = (orbitAngle * speed + i * 360f / electronCount) * PI.toFloat() / 180f
        val ex = centerX + cos(angle) * radius
        val ey = centerY + sin(angle) * radius * 0.3f

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(orbitColors[index].copy(alpha = 0.6f), Color.Transparent),
            center = Offset(ex, ey),
            radius = 8f,
          ),
          radius = 8f,
          center = Offset(ex, ey),
        )
        drawCircle(Color.White, 2f, Offset(ex, ey))
      }
    }

    // Nucleus glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF6B6B).copy(alpha = 0.4f), Color.Transparent),
        center = Offset(centerX, centerY),
        radius = 25f,
      ),
      radius = 25f,
      center = Offset(centerX, centerY),
    )

    // Nucleus
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color.White, Color(0xFFFF6B6B)),
        center = Offset(centerX - 3f, centerY - 3f),
        radius = 10f,
      ),
      radius = 10f,
      center = Offset(centerX, centerY),
    )
  }
}

@Composable
fun BibleCardPreview(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "bible")
  val glow by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glow",
  )
  val rayOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rays",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val centerX = size.width * 0.5f
    val topY = 0f

    // Light rays from above
    for (i in 0 until 7) {
      val angle = (-0.4f + i * 0.13f)
      val rayAlpha = (0.1f + sin(rayOffset * PI.toFloat() * 2 + i * 0.5f) * 0.05f) * glow
      val path = Path().apply {
        moveTo(centerX - 5f, topY)
        lineTo(centerX + 5f, topY)
        lineTo(centerX + cos(angle) * size.height + 30f, size.height)
        lineTo(centerX + cos(angle) * size.height - 30f, size.height)
        close()
      }
      drawPath(path, Color(0xFFFFD700).copy(alpha = rayAlpha))
    }

    // Cross
    val crossX = size.width * 0.5f
    val crossY = size.height * 0.4f
    val crossColor = Color(0xFFFFD700).copy(alpha = glow)

    // Vertical beam
    drawLine(
      color = crossColor,
      start = Offset(crossX, crossY - 20f),
      end = Offset(crossX, crossY + 25f),
      strokeWidth = 4f,
      cap = StrokeCap.Round,
    )
    // Horizontal beam
    drawLine(
      color = crossColor,
      start = Offset(crossX - 15f, crossY - 5f),
      end = Offset(crossX + 15f, crossY - 5f),
      strokeWidth = 4f,
      cap = StrokeCap.Round,
    )

    // Cross glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFD700).copy(alpha = 0.3f * glow),
          Color.Transparent,
        ),
        center = Offset(crossX, crossY),
        radius = 35f,
      ),
      radius = 35f,
      center = Offset(crossX, crossY),
    )

    // Dove silhouette
    val doveX = size.width * 0.7f
    val doveY = size.height * 0.25f
    val doveColor = Color.White.copy(alpha = 0.7f * glow)

    // Simple dove shape
    drawOval(
      color = doveColor,
      topLeft = Offset(doveX - 8f, doveY - 3f),
      size = androidx.compose.ui.geometry.Size(16f, 6f),
    )
    drawCircle(color = doveColor, radius = 3f, center = Offset(doveX + 6f, doveY - 2f))

    // Stars
    val starPositions = listOf(
      Offset(size.width * 0.2f, size.height * 0.2f),
      Offset(size.width * 0.8f, size.height * 0.15f),
      Offset(size.width * 0.15f, size.height * 0.5f),
    )
    starPositions.forEach { pos ->
      val starGlow = (sin(rayOffset * PI.toFloat() * 2 + pos.x * 0.01f) + 1f) / 2f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White.copy(alpha = 0.8f * starGlow),
            Color(0xFFFFD700).copy(alpha = 0.3f * starGlow),
            Color.Transparent,
          ),
          center = pos,
          radius = 8f,
        ),
        radius = 8f,
        center = pos,
      )
    }
  }
}

@Composable
fun MainScreen(onPaywallSelected: (PaywallDemo) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F0F1A),
          ),
        ),
      )
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
  ) {
    Spacer(modifier = Modifier.height(32.dp))

    Text(
      text = "Awesome Paywalls",
      style = TextStyle(
        color = Color.White,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
      ),
    )

    Text(
      text = "Choose a demo to explore",
      style = TextStyle(
        color = Color.White.copy(alpha = 0.6f),
        fontSize = 14.sp,
      ),
    )

    Spacer(modifier = Modifier.height(24.dp))

    PaywallCard(
      title = "Christmas",
      subtitle = "Festive holiday theme with snowflakes",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFFC41E3A), Color(0xFF1E5631)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.CHRISTMAS) },
    ) {
      ChristmasCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "New Year 2026",
      subtitle = "Celebrate with golden sparkles",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF2D2D44)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.NEW_YEAR) },
    ) {
      NewYearCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "New Year's Eve Fireworks",
      subtitle = "Explosive celebration countdown",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0D0D1A), Color(0xFF1A0A2E)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.NEW_YEARS_EVE_FIREWORKS) },
    ) {
      FireworksCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Summer",
      subtitle = "Sunny beach vibes and waves",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0077B6), Color(0xFF00B4D8)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.SUMMER) },
    ) {
      SummerCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Premium",
      subtitle = "Elegant pro subscription style",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF2E1A4A)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.PREMIUM) },
    ) {
      PremiumCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Growing Tree",
      subtitle = "Watch your benefits grow",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF1B4332), Color(0xFF2D6A4F)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.GROWING_TREE) },
    ) {
      GrowingTreeCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Universe",
      subtitle = "Explore the cosmic galaxy",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0A0A1A), Color(0xFF1A1030)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.UNIVERSE) },
    ) {
      UniverseCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Day & Night",
      subtitle = "Smooth day-night transitions",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF4A90D9), Color(0xFF1B263B)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.DAY_NIGHT) },
    ) {
      DayNightCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Underwater",
      subtitle = "Deep sea exploration vibes",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0077B6), Color(0xFF03045E)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.UNDERWATER) },
    ) {
      UnderwaterCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Synthwave",
      subtitle = "Retro 80s neon aesthetic",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF2D1B4E), Color(0xFF0D0221)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.SYNTHWAVE) },
    ) {
      SynthwaveCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Sakura",
      subtitle = "Peaceful cherry blossom theme",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFFFFE4E8), Color(0xFFFFB7C5)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.SAKURA) },
    ) {
      SakuraCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Fireflies",
      subtitle = "Magical night garden ambiance",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0A1628), Color(0xFF1F4068)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.FIREFLIES) },
    ) {
      FirefliesCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Geometry",
      subtitle = "Mathematical precision and shapes",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0D0D1A), Color(0xFF1A1A2E)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.GEOMETRY) },
    ) {
      GeometryCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Atomic",
      subtitle = "Nuclear powered science theme",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF0A0A1A), Color(0xFF1A0A2E)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.ATOMIC) },
    ) {
      AtomicCardPreview()
    }

    Spacer(modifier = Modifier.height(12.dp))

    PaywallCard(
      title = "Bible",
      subtitle = "Heavenly prayer and faith theme",
      background = Brush.linearGradient(
        colors = listOf(Color(0xFF2A1810), Color(0xFF1A0F08)),
      ),
      onClick = { onPaywallSelected(PaywallDemo.BIBLE) },
    ) {
      BibleCardPreview()
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}
