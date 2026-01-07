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
package com.revenuecat.awesomepaywalls.paywalls

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*

private const val PHI = 1.618033988749895f
private const val TAU = 2f * PI.toFloat()

@Composable
fun GeometryBackground(modifier: Modifier = Modifier) {
  var totalTime by remember { mutableFloatStateOf(0f) }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  val textMeasurer = rememberTextMeasurer()

  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { frameTimeNanos ->
        val deltaTime = if (lastFrameTimeNanos == 0L) {
          0.016f
        } else {
          ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f).coerceIn(0f, 0.1f)
        }
        lastFrameTimeNanos = frameTimeNanos
        totalTime += deltaTime
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    // Layer 1: Subtle grid background
    drawMathGrid(width, height, totalTime)

    // Layer 2: Animated sine wave
    drawAnimatedSineWave(width, height, totalTime)

    // Layer 3: Pythagorean theorem triangle
    drawPythagoreanTheorem(width * 0.7f, height * 0.12f, totalTime)

    // Layer 4: Golden spiral (Fibonacci)
    drawGoldenSpiral(width * 0.08f, height * 0.22f, totalTime)

    // Layer 5: Rotating sacred geometry
    drawSacredGeometry(width * 0.5f, height * 0.28f, totalTime)

    // Layer 6: Floating math symbols
    drawFloatingMathSymbols(width, height, totalTime)

    // Layer 7: Euler's identity circle
    drawEulersCircle(width * 0.85f, height * 0.18f, totalTime)

    // Layer 8: Coordinate system with function
    drawCoordinateFunction(width * 0.15f, height * 0.42f, totalTime)

    // Layer 9: Animated formulas
    drawAnimatedFormulas(width, height, totalTime, textMeasurer)

    // Layer 10: Fibonacci sequence
    drawFibonacciBoxes(width * 0.75f, height * 0.38f, totalTime)
  }
}

private fun DrawScope.drawMathGrid(width: Float, height: Float, time: Float) {
  val gridSpacing = 40f
  val gridColor = Color(0xFF00D4FF)

  // Animated grid lines
  val offset = (time * 10f) % gridSpacing

  // Vertical lines
  var x = offset
  while (x < width) {
    val alpha = 0.03f + 0.02f * sin(time + x * 0.01f)
    drawLine(
      color = gridColor.copy(alpha = alpha),
      start = Offset(x, 0f),
      end = Offset(x, height),
      strokeWidth = 0.5f,
    )
    x += gridSpacing
  }

  // Horizontal lines
  var y = offset
  while (y < height) {
    val alpha = 0.03f + 0.02f * sin(time + y * 0.01f)
    drawLine(
      color = gridColor.copy(alpha = alpha),
      start = Offset(0f, y),
      end = Offset(width, y),
      strokeWidth = 0.5f,
    )
    y += gridSpacing
  }
}

private fun DrawScope.drawAnimatedSineWave(width: Float, height: Float, time: Float) {
  val waveY = height * 0.52f
  val amplitude = 25f
  val frequency = 0.02f
  val waveColor = Color(0xFF7B68EE)

  // Draw multiple waves with phase offset
  for (wave in 0..2) {
    val path = Path()
    val phaseOffset = wave * 0.5f
    val waveAlpha = 0.3f - wave * 0.08f

    var isFirst = true
    val segments = (width / 2).toInt()
    val visibleSegments = ((time * 0.3f % 1f) * segments).toInt() + segments / 2

    for (i in 0..visibleSegments.coerceAtMost(segments)) {
      val xPos = i * 2f
      val yPos =
        waveY +
          sin(xPos * frequency + time * 2f + phaseOffset) * amplitude * (1f + wave * 0.3f)

      if (isFirst) {
        path.moveTo(xPos, yPos)
        isFirst = false
      } else {
        path.lineTo(xPos, yPos)
      }
    }

    drawPath(
      path = path,
      color = waveColor.copy(alpha = waveAlpha),
      style = Stroke(width = 2f - wave * 0.5f, cap = StrokeCap.Round),
    )
  }

  // Draw axis labels
  val axisColor = Color(0xFF00D4FF).copy(alpha = 0.4f)
  drawLine(axisColor, Offset(0f, waveY), Offset(width, waveY), 0.5f)
}

private fun DrawScope.drawPythagoreanTheorem(startX: Float, startY: Float, time: Float) {
  val drawProgress = ((time * 0.15f) % 1f)
  val a = 35f
  val b = 45f
  val c = sqrt(a * a + b * b)

  val p1 = Offset(startX, startY + b)
  val p2 = Offset(startX + a, startY + b)
  val p3 = Offset(startX, startY)

  val triangleColor = Color(0xFFFF6B9D)
  val squareColor = Color(0xFF00D4FF)

  // Animate triangle drawing
  val triangleProgress = (drawProgress * 3f).coerceIn(0f, 1f)

  if (triangleProgress > 0f) {
    // Side a
    val aEnd = Offset(p1.x + (p2.x - p1.x) * triangleProgress.coerceAtMost(0.33f) * 3f, p2.y)
    drawLine(triangleColor.copy(alpha = 0.7f), p1, aEnd, 2f)
  }
  if (triangleProgress > 0.33f) {
    // Side b
    val bProgress = ((triangleProgress - 0.33f) * 3f).coerceIn(0f, 1f)
    val bEnd = Offset(p1.x, p1.y - (p1.y - p3.y) * bProgress)
    drawLine(triangleColor.copy(alpha = 0.7f), p1, bEnd, 2f)
  }
  if (triangleProgress > 0.66f) {
    // Hypotenuse c
    val cProgress = ((triangleProgress - 0.66f) * 3f).coerceIn(0f, 1f)
    val cEnd = Offset(p3.x + (p2.x - p3.x) * cProgress, p3.y + (p2.y - p3.y) * cProgress)
    drawLine(triangleColor.copy(alpha = 0.7f), p3, cEnd, 2f)
  }

  // Draw squares on sides (animated)
  val squareProgress = ((drawProgress - 0.4f) * 2f).coerceIn(0f, 1f)
  if (squareProgress > 0f) {
    // Square on a
    drawRect(
      color = squareColor.copy(alpha = 0.15f * squareProgress),
      topLeft = Offset(p1.x, p1.y),
      size = Size(a * squareProgress, a * squareProgress),
      style = Stroke(1f),
    )

    // Square on b
    drawRect(
      color = squareColor.copy(alpha = 0.15f * squareProgress),
      topLeft = Offset(p1.x - b * squareProgress, p3.y),
      size = Size(b * squareProgress, b * squareProgress),
      style = Stroke(1f),
    )
  }

  // Right angle marker
  if (triangleProgress > 0.5f) {
    val markerSize = 8f
    drawLine(
      color = triangleColor.copy(alpha = 0.5f),
      start = Offset(p1.x + markerSize, p1.y),
      end = Offset(p1.x + markerSize, p1.y - markerSize),
      strokeWidth = 1f,
    )
    drawLine(
      color = triangleColor.copy(alpha = 0.5f),
      start = Offset(p1.x + markerSize, p1.y - markerSize),
      end = Offset(p1.x, p1.y - markerSize),
      strokeWidth = 1f,
    )
  }
}

private fun DrawScope.drawGoldenSpiral(startX: Float, startY: Float, time: Float) {
  val drawProgress = ((time * 0.08f) % 1f)
  val spiralColor = Color(0xFF00D4FF)

  // Draw Fibonacci rectangles
  val fibSizes = listOf(1f, 1f, 2f, 3f, 5f, 8f, 13f, 21f)
  val scale = 4f
  var currentX = startX
  var currentY = startY
  var direction = 0

  fibSizes.forEachIndexed { index, fib ->
    val boxProgress = ((drawProgress - index * 0.08f) * 4f).coerceIn(0f, 1f)
    if (boxProgress <= 0f) return@forEachIndexed

    val size = fib * scale
    val boxAlpha = 0.25f * boxProgress * (0.8f + 0.2f * sin(time + index))

    // Draw rectangle
    drawRect(
      color = spiralColor.copy(alpha = boxAlpha),
      topLeft = Offset(currentX, currentY),
      size = Size(size * boxProgress, size * boxProgress),
      style = Stroke(1f),
    )

    // Draw arc (quarter circle for spiral)
    if (boxProgress > 0.5f) {
      val arcProgress = (boxProgress - 0.5f) * 2f
      val startAngle = when (direction) {
        0 -> 180f
        1 -> 90f
        2 -> 0f
        else -> 270f
      }

      drawArc(
        color = spiralColor.copy(alpha = 0.5f * arcProgress),
        startAngle = startAngle,
        sweepAngle = 90f * arcProgress,
        useCenter = false,
        topLeft = when (direction) {
          0 -> Offset(currentX, currentY)
          1 -> Offset(currentX + size - size * 2, currentY)
          2 -> Offset(currentX + size - size * 2, currentY + size - size * 2)
          else -> Offset(currentX, currentY + size - size * 2)
        },
        size = Size(size * 2, size * 2),
        style = Stroke(1.5f),
      )
    }

    // Move to next position
    when (direction) {
      0 -> currentX += size
      1 -> currentY += size
      2 -> {
        currentX -= fibSizes.getOrElse(index + 1) { fib } * scale
        currentY -= (fibSizes.getOrElse(index + 1) { fib } * scale - size)
      }
      3 -> {
        currentY -= fibSizes.getOrElse(index + 1) { fib } * scale
        currentX -= (fibSizes.getOrElse(index + 1) { fib } * scale - size)
      }
    }
    direction = (direction + 1) % 4
  }
}

private fun DrawScope.drawSacredGeometry(cx: Float, cy: Float, time: Float) {
  val radius = 70f
  val rotation = time * 0.3f
  val pulseScale = 1f + 0.03f * sin(time * 2f)

  // Outer circle
  drawCircle(
    color = Color(0xFF00D4FF).copy(alpha = 0.25f),
    radius = radius * pulseScale,
    center = Offset(cx, cy),
    style = Stroke(1.5f),
  )

  // Inner hexagram (Star of David)
  val hexagramColor = Color(0xFF7B68EE)
  for (triangle in 0..1) {
    val startAngle = rotation + triangle * (PI.toFloat() / 6)
    val path = Path()

    for (i in 0..2) {
      val angle = startAngle + i * TAU / 3
      val px = cx + cos(angle) * radius * 0.8f * pulseScale
      val py = cy + sin(angle) * radius * 0.8f * pulseScale

      if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()

    val drawProgress = ((time * 0.2f + triangle * 0.3f) % 1f)
    drawPath(
      path = path,
      color = hexagramColor.copy(alpha = 0.3f * drawProgress),
      style = Stroke(1.5f),
    )
  }

  // Inner circles (Seed of Life pattern)
  for (i in 0 until 6) {
    val angle = rotation * 0.5f + i * TAU / 6
    val circleX = cx + cos(angle) * radius * 0.4f * pulseScale
    val circleY = cy + sin(angle) * radius * 0.4f * pulseScale
    val circleProgress = ((time * 0.15f - i * 0.05f) % 1f)

    drawCircle(
      color = Color(0xFFFF6B9D).copy(alpha = 0.2f * circleProgress),
      radius = radius * 0.4f * pulseScale,
      center = Offset(circleX, circleY),
      style = Stroke(0.8f),
    )
  }

  // Center circle
  drawCircle(
    color = Color(0xFF00D4FF).copy(alpha = 0.4f),
    radius = radius * 0.4f * pulseScale,
    center = Offset(cx, cy),
    style = Stroke(1.5f),
  )

  // Center point glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
      center = Offset(cx, cy),
      radius = 8f,
    ),
    radius = 8f,
    center = Offset(cx, cy),
  )
}

private fun DrawScope.drawFloatingMathSymbols(width: Float, height: Float, time: Float) {
  // Draw each symbol with pencil drawing animation
  drawPencilPi(width * 0.06f, height * 0.22f, 50f, time, 0f, Color(0xFF00D4FF))
  drawPencilSigma(width * 0.88f, height * 0.28f, 55f, time, 2.5f, Color(0xFF7B68EE))
  drawPencilIntegral(width * 0.04f, height * 0.48f, 60f, time, 5f, Color(0xFFFF6B9D))
  drawPencilSqrt(width * 0.88f, height * 0.42f, 50f, time, 7.5f, Color(0xFF00D4FF))
  drawPencilInfinity(width * 0.08f, height * 0.35f, 45f, time, 10f, Color(0xFF00FF88))
  drawPencilDelta(width * 0.9f, height * 0.55f, 48f, time, 12.5f, Color(0xFFFF6B9D))
  drawPencilTheta(width * 0.06f, height * 0.62f, 45f, time, 15f, Color(0xFF7B68EE))
  drawPencilOmega(width * 0.88f, height * 0.65f, 48f, time, 17.5f, Color(0xFF00FF88))
}

// Helper to add hand-drawn wobble effect
private fun wobble(value: Float, time: Float, intensity: Float = 1f): Float =
  value + sin(time * 15f) * intensity + cos(time * 23f) * intensity * 0.7f

// Draw pencil tip glow at current drawing position
private fun DrawScope.drawPencilTip(x: Float, y: Float, color: Color) {
  // Outer glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        color.copy(alpha = 0.8f),
        color.copy(alpha = 0.4f),
        Color.Transparent,
      ),
      center = Offset(x, y),
      radius = 12f,
    ),
    radius = 12f,
    center = Offset(x, y),
  )
  // Inner bright point
  drawCircle(
    color = Color.White,
    radius = 3f,
    center = Offset(x, y),
  )
}

private fun DrawScope.drawPencilPi(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 6f // seconds for one complete draw cycle
  val pauseDuration = 2f // seconds to pause when complete
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.06f
  val drawnAlpha = 0.85f

  // Pi has 3 strokes: top bar, left leg, right leg
  val stroke1End = 0.33f
  val stroke2End = 0.66f

  // Stroke 1: Top horizontal bar
  val s1Progress = (progress / stroke1End).coerceIn(0f, 1f)
  val startX1 = cx - size * 0.4f
  val endX1Full = cx + size * 0.4f
  val y1 = cy - size * 0.35f

  if (s1Progress > 0f) {
    val currentEndX = startX1 + (endX1Full - startX1) * s1Progress
    val path = Path()
    path.moveTo(startX1, wobble(y1, time, 0.5f))

    // Draw with slight wobble for hand-drawn feel
    val segments = (30 * s1Progress).toInt().coerceAtLeast(1)
    for (i in 1..segments) {
      val t = i.toFloat() / 30
      val x = startX1 + (endX1Full - startX1) * t
      val y = wobble(y1, time + t * 10, 0.8f)
      path.lineTo(x, y)
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < stroke1End) {
      drawPencilTip(currentEndX, wobble(y1, time, 0.5f), color)
    }
  }

  // Stroke 2: Left leg
  if (progress > stroke1End) {
    val s2Progress = ((progress - stroke1End) / (stroke2End - stroke1End)).coerceIn(0f, 1f)
    val legX = cx - size * 0.15f
    val legStartY = y1
    val legEndY = cy + size * 0.35f

    val path = Path()
    path.moveTo(wobble(legX, time, 0.5f), legStartY)

    val segments = (25 * s2Progress).toInt().coerceAtLeast(1)
    for (i in 1..segments) {
      val t = i.toFloat() / 25
      val x = wobble(legX, time + t * 8, 0.6f)
      val y = legStartY + (legEndY - legStartY) * t
      path.lineTo(x, y)
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < stroke2End) {
      val currentY = legStartY + (legEndY - legStartY) * s2Progress
      drawPencilTip(wobble(legX, time, 0.5f), currentY, color)
    }
  }

  // Stroke 3: Right leg
  if (progress > stroke2End) {
    val s3Progress = ((progress - stroke2End) / (1f - stroke2End)).coerceIn(0f, 1f)
    val legX = cx + size * 0.15f
    val legStartY = y1
    val legEndY = cy + size * 0.35f

    val path = Path()
    path.moveTo(wobble(legX, time, 0.5f), legStartY)

    val segments = (25 * s3Progress).toInt().coerceAtLeast(1)
    for (i in 1..segments) {
      val t = i.toFloat() / 25
      val x = wobble(legX, time + t * 8, 0.6f)
      val y = legStartY + (legEndY - legStartY) * t
      path.lineTo(x, y)
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < 1f) {
      val currentY = legStartY + (legEndY - legStartY) * s3Progress
      drawPencilTip(wobble(legX, time, 0.5f), currentY, color)
    }
  }
}

private fun DrawScope.drawPencilSigma(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 5f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.055f
  val drawnAlpha = 0.85f

  // Sigma points: top-right -> top-left -> center -> bottom-left -> bottom-right
  val points = listOf(
    Offset(cx + size * 0.35f, cy - size * 0.4f),
    Offset(cx - size * 0.3f, cy - size * 0.4f),
    Offset(cx + size * 0.1f, cy),
    Offset(cx - size * 0.3f, cy + size * 0.4f),
    Offset(cx + size * 0.35f, cy + size * 0.4f),
  )

  val path = Path()
  var lastPoint: Offset? = null
  var isDrawing = false

  val totalSegments = 4f
  val currentSegment = progress * totalSegments

  for (i in 0 until 4) {
    val segProgress = (currentSegment - i).coerceIn(0f, 1f)
    if (segProgress > 0f) {
      val start = points[i]
      val end = points[i + 1]

      if (!isDrawing) {
        path.moveTo(wobble(start.x, time, 0.6f), wobble(start.y, time + 1f, 0.6f))
        isDrawing = true
      }

      val segments = (20 * segProgress).toInt().coerceAtLeast(1)
      for (j in 1..segments) {
        val t = j.toFloat() / 20
        val x = start.x + (end.x - start.x) * t
        val y = start.y + (end.y - start.y) * t
        path.lineTo(wobble(x, time + t * 5, 0.5f), wobble(y, time + t * 5 + 1f, 0.5f))
        lastPoint = Offset(x, y)
      }
    }
  }

  if (isDrawing) {
    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    if (progress < 1f && lastPoint != null) {
      drawPencilTip(
        wobble(lastPoint.x, time, 0.5f),
        wobble(lastPoint.y, time + 1f, 0.5f),
        color,
      )
    }
  }
}

private fun DrawScope.drawPencilIntegral(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 4f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.05f
  val drawnAlpha = 0.85f

  val path = Path()
  val totalPoints = 60
  val visiblePoints = (totalPoints * progress).toInt()

  if (visiblePoints > 0) {
    var lastX = 0f
    var lastY = 0f

    for (i in 0..visiblePoints) {
      val t = i.toFloat() / totalPoints

      // Elegant S-curve for integral
      val point = when {
        t < 0.15f -> {
          val localT = t / 0.15f
          Offset(
            cx + size * 0.18f * cos(PI.toFloat() * localT),
            cy - size * 0.45f + size * 0.12f * localT,
          )
        }
        t < 0.85f -> {
          val localT = (t - 0.15f) / 0.7f
          Offset(
            cx + size * 0.04f * sin(localT * PI.toFloat()),
            cy - size * 0.33f + size * 0.66f * localT,
          )
        }
        else -> {
          val localT = (t - 0.85f) / 0.15f
          Offset(
            cx - size * 0.18f * cos(PI.toFloat() * (1f - localT)),
            cy + size * 0.33f + size * 0.12f * localT,
          )
        }
      }

      val wobbledX = wobble(point.x, time + t * 10, 0.4f)
      val wobbledY = wobble(point.y, time + t * 10 + 2f, 0.4f)

      if (i == 0) {
        path.moveTo(wobbledX, wobbledY)
      } else {
        path.lineTo(wobbledX, wobbledY)
      }

      lastX = wobbledX
      lastY = wobbledY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < 1f) {
      drawPencilTip(lastX, lastY, color)
    }
  }
}

private fun DrawScope.drawPencilSqrt(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 4.5f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.05f
  val drawnAlpha = 0.85f

  val points = listOf(
    Offset(cx - size * 0.4f, cy + size * 0.05f),
    Offset(cx - size * 0.28f, cy + size * 0.12f),
    Offset(cx - size * 0.08f, cy + size * 0.4f),
    Offset(cx + size * 0.18f, cy - size * 0.35f),
    Offset(cx + size * 0.45f, cy - size * 0.35f),
  )

  val path = Path()
  var lastPoint: Offset? = null
  var isDrawing = false

  val totalSegments = 4f
  val currentSegment = progress * totalSegments

  for (i in 0 until 4) {
    val segProgress = (currentSegment - i).coerceIn(0f, 1f)
    if (segProgress > 0f) {
      val start = points[i]
      val end = points[i + 1]

      if (!isDrawing) {
        path.moveTo(wobble(start.x, time, 0.4f), wobble(start.y, time + 1f, 0.4f))
        isDrawing = true
      }

      val segments = (15 * segProgress).toInt().coerceAtLeast(1)
      for (j in 1..segments) {
        val t = j.toFloat() / 15
        val x = start.x + (end.x - start.x) * t
        val y = start.y + (end.y - start.y) * t
        path.lineTo(wobble(x, time + t * 6, 0.4f), wobble(y, time + t * 6 + 1f, 0.4f))
        lastPoint = Offset(x, y)
      }
    }
  }

  if (isDrawing) {
    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    if (progress < 1f && lastPoint != null) {
      drawPencilTip(
        wobble(lastPoint.x, time, 0.4f),
        wobble(lastPoint.y, time + 1f, 0.4f),
        color,
      )
    }
  }
}

private fun DrawScope.drawPencilInfinity(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 5f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.055f
  val drawnAlpha = 0.85f

  val path = Path()
  val totalPoints = 80
  val visiblePoints = (totalPoints * progress).toInt()

  if (visiblePoints > 0) {
    var lastX = 0f
    var lastY = 0f

    for (i in 0..visiblePoints) {
      val t = i.toFloat() / totalPoints * TAU

      // Lemniscate (figure-8) parametric equation
      val denom = 1f + sin(t).pow(2)
      val x = cx + cos(t) * size * 0.5f / denom
      val y = cy + sin(t) * cos(t) * size * 0.35f / denom

      val wobbledX = wobble(x, time + i * 0.1f, 0.3f)
      val wobbledY = wobble(y, time + i * 0.1f + 1f, 0.3f)

      if (i == 0) {
        path.moveTo(wobbledX, wobbledY)
      } else {
        path.lineTo(wobbledX, wobbledY)
      }

      lastX = wobbledX
      lastY = wobbledY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < 1f) {
      drawPencilTip(lastX, lastY, color)
    }
  }
}

private fun DrawScope.drawPencilDelta(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 4f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.05f
  val drawnAlpha = 0.85f

  val points = listOf(
    Offset(cx, cy - size * 0.4f), // Top
    Offset(cx - size * 0.38f, cy + size * 0.35f), // Bottom left
    Offset(cx + size * 0.38f, cy + size * 0.35f), // Bottom right
    Offset(cx, cy - size * 0.4f), // Back to top
  )

  val path = Path()
  var lastPoint: Offset? = null
  var isDrawing = false

  val totalSegments = 3f
  val currentSegment = progress * totalSegments

  for (i in 0 until 3) {
    val segProgress = (currentSegment - i).coerceIn(0f, 1f)
    if (segProgress > 0f) {
      val start = points[i]
      val end = points[i + 1]

      if (!isDrawing) {
        path.moveTo(wobble(start.x, time, 0.5f), wobble(start.y, time + 1f, 0.5f))
        isDrawing = true
      }

      val segments = (20 * segProgress).toInt().coerceAtLeast(1)
      for (j in 1..segments) {
        val t = j.toFloat() / 20
        val x = start.x + (end.x - start.x) * t
        val y = start.y + (end.y - start.y) * t
        path.lineTo(wobble(x, time + t * 5, 0.4f), wobble(y, time + t * 5 + 1f, 0.4f))
        lastPoint = Offset(x, y)
      }
    }
  }

  if (isDrawing) {
    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    if (progress < 1f && lastPoint != null) {
      drawPencilTip(
        wobble(lastPoint.x, time, 0.4f),
        wobble(lastPoint.y, time + 1f, 0.4f),
        color,
      )
    }
  }
}

private fun DrawScope.drawPencilTheta(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 4.5f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.05f
  val drawnAlpha = 0.85f

  // First 75%: Draw oval, Last 25%: Draw middle line
  val ovalEnd = 0.75f

  // Draw oval
  val ovalProgress = (progress / ovalEnd).coerceIn(0f, 1f)
  if (ovalProgress > 0f) {
    val path = Path()
    val totalPoints = 50
    val visiblePoints = (totalPoints * ovalProgress).toInt()

    var lastX = 0f
    var lastY = 0f

    for (i in 0..visiblePoints) {
      val t = i.toFloat() / totalPoints * TAU
      val x = cx + cos(t) * size * 0.32f
      val y = cy + sin(t) * size * 0.42f

      val wobbledX = wobble(x, time + i * 0.1f, 0.3f)
      val wobbledY = wobble(y, time + i * 0.1f + 1f, 0.3f)

      if (i == 0) {
        path.moveTo(wobbledX, wobbledY)
      } else {
        path.lineTo(wobbledX, wobbledY)
      }

      lastX = wobbledX
      lastY = wobbledY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < ovalEnd) {
      drawPencilTip(lastX, lastY, color)
    }
  }

  // Draw horizontal line through middle
  if (progress > ovalEnd) {
    val lineProgress = ((progress - ovalEnd) / (1f - ovalEnd)).coerceIn(0f, 1f)
    val startX = cx - size * 0.32f
    val endX = cx + size * 0.32f

    val path = Path()
    path.moveTo(wobble(startX, time, 0.3f), wobble(cy, time + 1f, 0.3f))

    val segments = (15 * lineProgress).toInt().coerceAtLeast(1)
    var lastX = startX
    for (i in 1..segments) {
      val t = i.toFloat() / 15
      val x = startX + (endX - startX) * t
      path.lineTo(wobble(x, time + t * 4, 0.3f), wobble(cy, time + t * 4 + 1f, 0.3f))
      lastX = x
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < 1f) {
      drawPencilTip(wobble(lastX, time, 0.3f), wobble(cy, time + 1f, 0.3f), color)
    }
  }
}

private fun DrawScope.drawPencilOmega(
  cx: Float,
  cy: Float,
  size: Float,
  time: Float,
  phase: Float,
  color: Color,
) {
  val cycleDuration = 5f
  val pauseDuration = 2f
  val totalCycle = cycleDuration + pauseDuration
  val cycleTime = (time + phase) % totalCycle
  val progress = (cycleTime / cycleDuration).coerceIn(0f, 1f)

  val strokeWidth = size * 0.05f
  val drawnAlpha = 0.85f

  // Omega: horseshoe arc (0-60%), left foot (60-75%), lift, right arc (75-90%), right foot (90-100%)
  val arcEnd = 0.55f
  val leftFootEnd = 0.70f
  val rightArcStart = 0.75f
  val rightArcEnd = 0.90f

  var lastX = 0f
  var lastY = 0f

  // Main horseshoe arc
  val arcProgress = (progress / arcEnd).coerceIn(0f, 1f)
  if (arcProgress > 0f) {
    val path = Path()
    val totalPoints = 40
    val visiblePoints = (totalPoints * arcProgress).toInt()

    for (i in 0..visiblePoints) {
      val t = PI.toFloat() * 0.12f + (i.toFloat() / totalPoints) * PI.toFloat() * 0.76f
      val x = cx + cos(t) * size * 0.38f
      val y = cy - sin(t) * size * 0.42f + size * 0.12f

      val wobbledX = wobble(x, time + i * 0.08f, 0.3f)
      val wobbledY = wobble(y, time + i * 0.08f + 1f, 0.3f)

      if (i == 0) {
        path.moveTo(wobbledX, wobbledY)
      } else {
        path.lineTo(wobbledX, wobbledY)
      }

      lastX = wobbledX
      lastY = wobbledY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < arcEnd) {
      drawPencilTip(lastX, lastY, color)
    }
  }

  // Left foot
  if (progress > arcEnd && progress <= leftFootEnd) {
    val footProgress = ((progress - arcEnd) / (leftFootEnd - arcEnd)).coerceIn(0f, 1f)
    val footStartX = cx - size * 0.35f
    val footStartY = cy + size * 0.28f
    val footEndX = footStartX - size * 0.15f

    val path = Path()
    path.moveTo(wobble(footStartX, time, 0.3f), wobble(footStartY, time + 1f, 0.3f))

    val segments = (10 * footProgress).toInt().coerceAtLeast(1)
    for (i in 1..segments) {
      val t = i.toFloat() / 10
      val x = footStartX + (footEndX - footStartX) * t
      path.lineTo(wobble(x, time + t * 3, 0.3f), wobble(footStartY, time + t * 3 + 1f, 0.3f))
      lastX = x
      lastY = footStartY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
    drawPencilTip(wobble(lastX, time, 0.3f), wobble(lastY, time + 1f, 0.3f), color)
  } else if (progress > leftFootEnd) {
    // Draw completed left foot
    val footStartX = cx - size * 0.35f
    val footStartY = cy + size * 0.28f
    val footEndX = footStartX - size * 0.15f

    val path = Path()
    path.moveTo(wobble(footStartX, time, 0.3f), wobble(footStartY, time + 1f, 0.3f))
    path.lineTo(wobble(footEndX, time, 0.3f), wobble(footStartY, time + 1f, 0.3f))
    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )
  }

  // Right side: small arc connection and foot
  if (progress > rightArcStart) {
    val rightProgress = ((progress - rightArcStart) / (1f - rightArcStart)).coerceIn(0f, 1f)
    val footStartX = cx + size * 0.35f
    val footStartY = cy + size * 0.28f
    val footEndX = footStartX + size * 0.15f

    val path = Path()
    path.moveTo(wobble(footStartX, time, 0.3f), wobble(footStartY, time + 1f, 0.3f))

    val segments = (10 * rightProgress).toInt().coerceAtLeast(1)
    for (i in 1..segments) {
      val t = i.toFloat() / 10
      val x = footStartX + (footEndX - footStartX) * t
      path.lineTo(wobble(x, time + t * 3, 0.3f), wobble(footStartY, time + t * 3 + 1f, 0.3f))
      lastX = x
      lastY = footStartY
    }

    drawPath(
      path,
      color.copy(alpha = drawnAlpha),
      style = Stroke(strokeWidth, cap = StrokeCap.Round),
    )

    if (progress < 1f) {
      drawPencilTip(wobble(lastX, time, 0.3f), wobble(lastY, time + 1f, 0.3f), color)
    }
  }
}

private fun DrawScope.drawEulersCircle(cx: Float, cy: Float, time: Float) {
  val radius = 30f
  val rotation = time * 0.5f
  val color = Color(0xFF00D4FF)

  // Unit circle
  drawCircle(
    color = color.copy(alpha = 0.3f),
    radius = radius,
    center = Offset(cx, cy),
    style = Stroke(1.5f),
  )

  // Axes
  drawLine(
    color = color.copy(alpha = 0.2f),
    start = Offset(cx - radius * 1.3f, cy),
    end = Offset(cx + radius * 1.3f, cy),
    strokeWidth = 0.8f,
  )
  drawLine(
    color = color.copy(alpha = 0.2f),
    start = Offset(cx, cy - radius * 1.3f),
    end = Offset(cx, cy + radius * 1.3f),
    strokeWidth = 0.8f,
  )

  // Rotating point on circle (e^(iθ))
  val pointX = cx + cos(rotation) * radius
  val pointY = cy - sin(rotation) * radius

  // Line from center to point
  drawLine(
    color = Color(0xFFFF6B9D).copy(alpha = 0.6f),
    start = Offset(cx, cy),
    end = Offset(pointX, pointY),
    strokeWidth = 1.5f,
  )

  // Point
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, Color(0xFFFF6B9D)),
      center = Offset(pointX, pointY),
      radius = 5f,
    ),
    radius = 5f,
    center = Offset(pointX, pointY),
  )

  // Projection lines (real and imaginary parts)
  val projAlpha = 0.3f
  // Real part (cos)
  drawLine(
    color = Color(0xFF00FF88).copy(alpha = projAlpha),
    start = Offset(pointX, pointY),
    end = Offset(pointX, cy),
    strokeWidth = 1f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
  )
  // Imaginary part (sin)
  drawLine(
    color = Color(0xFF7B68EE).copy(alpha = projAlpha),
    start = Offset(pointX, pointY),
    end = Offset(cx, pointY),
    strokeWidth = 1f,
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
  )
}

private fun DrawScope.drawCoordinateFunction(startX: Float, startY: Float, time: Float) {
  val axisLength = 80f
  val color = Color(0xFF7B68EE)

  // X axis
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX, startY),
    end = Offset(startX + axisLength, startY),
    strokeWidth = 1f,
  )
  // Arrow
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX + axisLength, startY),
    end = Offset(startX + axisLength - 5f, startY - 3f),
    strokeWidth = 1f,
  )
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX + axisLength, startY),
    end = Offset(startX + axisLength - 5f, startY + 3f),
    strokeWidth = 1f,
  )

  // Y axis
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX, startY),
    end = Offset(startX, startY - axisLength),
    strokeWidth = 1f,
  )
  // Arrow
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX, startY - axisLength),
    end = Offset(startX - 3f, startY - axisLength + 5f),
    strokeWidth = 1f,
  )
  drawLine(
    color = color.copy(alpha = 0.4f),
    start = Offset(startX, startY - axisLength),
    end = Offset(startX + 3f, startY - axisLength + 5f),
    strokeWidth = 1f,
  )

  // Draw parabola y = x²
  val funcPath = Path()
  val funcColor = Color(0xFFFF6B9D)
  var isFirst = true

  val drawProgress = ((time * 0.2f) % 1f)
  val segments = 40
  val visibleSegments = (segments * drawProgress).toInt() + segments / 2

  for (i in -segments / 2..visibleSegments.coerceAtMost(segments / 2)) {
    val normalizedX = i / 20f
    val normalizedY = normalizedX * normalizedX
    val px = startX + (normalizedX + 1f) * axisLength / 2.5f
    val py = startY - normalizedY * axisLength / 1.5f

    if (py > startY - axisLength && py < startY + 10) {
      if (isFirst) {
        funcPath.moveTo(px, py)
        isFirst = false
      } else {
        funcPath.lineTo(px, py)
      }
    }
  }

  drawPath(
    path = funcPath,
    color = funcColor.copy(alpha = 0.5f),
    style = Stroke(1.5f, cap = StrokeCap.Round),
  )
}

private fun DrawScope.drawAnimatedFormulas(
  width: Float,
  height: Float,
  time: Float,
  textMeasurer: androidx.compose.ui.text.TextMeasurer,
) {
  data class Formula(
    val text: String,
    val x: Float,
    val y: Float,
    val size: Int,
    val color: Color,
    val phase: Float,
  )

  // Larger formulas positioned on edges, away from center content
  val formulas = listOf(
    Formula("E = mc²", 0.02f, 0.08f, 22, Color(0xFF00D4FF), 0f),
    Formula("a² + b² = c²", 0.6f, 0.03f, 18, Color(0xFFFF6B9D), 0.2f),
    Formula("eⁱᵖ + 1 = 0", 0.02f, 0.18f, 18, Color(0xFF7B68EE), 0.4f),
    Formula("φ = 1.618", 0.7f, 0.22f, 20, Color(0xFF00FF88), 0.6f),
    Formula("f(x) = x²", 0.75f, 0.48f, 18, Color(0xFF7B68EE), 0.3f),
    Formula("∫ f(x)dx", 0.02f, 0.72f, 20, Color(0xFFFF6B9D), 0.5f),
    Formula("sin²θ + cos²θ = 1", 0.5f, 0.75f, 16, Color(0xFF00D4FF), 0.7f),
    Formula("∇ × F", 0.8f, 0.68f, 22, Color(0xFF00FF88), 0.8f),
  )

  formulas.forEach { formula ->
    val animProgress = ((time * 0.12f + formula.phase) % 1f)
    val alpha = 0.6f + 0.3f * sin(time * 0.5f + formula.phase * TAU)
    val floatY = formula.y * height + sin(time * 0.3f + formula.phase * 5f) * 10f
    val floatX = formula.x * width + cos(time * 0.2f + formula.phase * 3f) * 5f

    val textStyle = androidx.compose.ui.text.TextStyle(
      color = formula.color.copy(alpha = alpha * animProgress),
      fontSize = formula.size.sp,
      fontWeight = FontWeight.Medium,
    )

    val textLayout = textMeasurer.measure(formula.text, textStyle)

    drawText(
      textLayoutResult = textLayout,
      topLeft = Offset(floatX, floatY),
    )
  }
}

private fun DrawScope.drawFibonacciBoxes(startX: Float, startY: Float, time: Float) {
  val fibNumbers = listOf(1, 1, 2, 3, 5, 8)
  val boxSize = 10f
  val color = Color(0xFF7B68EE)

  fibNumbers.forEachIndexed { index, num ->
    val progress = ((time * 0.2f - index * 0.1f) % 1f).coerceIn(0f, 1f)
    val x = startX + index * (boxSize + 4f)
    val y = startY

    // Draw boxes representing the number
    for (i in 0 until num) {
      val boxY = y - i * (boxSize + 2f)
      val alpha = 0.2f + 0.15f * progress * sin(time + index + i)

      drawRect(
        color = color.copy(alpha = alpha),
        topLeft = Offset(x, boxY - boxSize),
        size = Size(boxSize * progress, boxSize * progress),
      )
    }

    // Connection line
    if (index > 0) {
      drawLine(
        color = color.copy(alpha = 0.15f * progress),
        start = Offset(x - 2f, startY - fibNumbers[index - 1] * (boxSize + 2f) / 2),
        end = Offset(x, startY - num * (boxSize + 2f) / 2),
        strokeWidth = 0.8f,
      )
    }
  }
}

@Composable
fun GeometryPlanOption(
  title: String,
  price: String,
  period: String,
  badge: String? = null,
  description: String? = null,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (isSelected) {
          Color(0xFF1A2A4A).copy(alpha = 0.8f)
        } else {
          Color(0xFF0D0D1A).copy(alpha = 0.6f)
        },
      )
      .then(
        if (isSelected) {
          Modifier.background(
            brush = Brush.linearGradient(
              colors = listOf(
                Color(0xFF00D4FF).copy(alpha = 0.1f),
                Color(0xFF7B68EE).copy(alpha = 0.05f),
              ),
            ),
          )
        } else {
          Modifier
        },
      )
      .clickable(onClick = onClick)
      .padding(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Geometric checkbox
      Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        if (isSelected) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val hexPath = Path().apply {
              for (i in 0 until 6) {
                val angle = i * TAU / 6 - PI.toFloat() / 2
                val hx = size.width / 2 + cos(angle) * size.width / 2
                val hy = size.height / 2 + sin(angle) * size.height / 2
                if (i == 0) moveTo(hx, hy) else lineTo(hx, hy)
              }
              close()
            }
            drawPath(hexPath, Color(0xFF00D4FF))
            drawPath(
              Path().apply {
                moveTo(size.width * 0.3f, size.height * 0.5f)
                lineTo(size.width * 0.45f, size.height * 0.65f)
                lineTo(size.width * 0.7f, size.height * 0.35f)
              },
              color = Color(0xFF0D0D1A),
              style = Stroke(width = 2f, cap = StrokeCap.Round),
            )
          }
        } else {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val hexPath = Path().apply {
              for (i in 0 until 6) {
                val angle = i * TAU / 6 - PI.toFloat() / 2
                val hx = size.width / 2 + cos(angle) * size.width / 2
                val hy = size.height / 2 + sin(angle) * size.height / 2
                if (i == 0) moveTo(hx, hy) else lineTo(hx, hy)
              }
              close()
            }
            drawPath(
              hexPath,
              Color(0xFFE0E8FF).copy(alpha = 0.3f),
              style = Stroke(1.5f),
            )
          }
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            style = TextStyle(
              color = Color(0xFFE0E8FF),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
          if (badge != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                  brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00D4FF), Color(0xFF7B68EE)),
                  ),
                ),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = badge,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = TextStyle(
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                ),
              )
            }
          }
        }
        if (description != null) {
          Text(
            text = description,
            style = TextStyle(
              color = Color(0xFF00D4FF),
              fontSize = 12.sp,
            ),
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = price,
          style = TextStyle(
            color = Color(0xFFE0E8FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = period,
          style = TextStyle(
            color = Color(0xFFE0E8FF).copy(alpha = 0.6f),
            fontSize = 12.sp,
          ),
        )
      }
    }
  }
}

@Composable
fun GeometryFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Canvas(modifier = Modifier.size(20.dp)) {
      val path = Path().apply {
        moveTo(size.width / 2, 2f)
        lineTo(size.width - 2f, size.height - 2f)
        lineTo(2f, size.height - 2f)
        close()
      }
      drawPath(path, Color(0xFF00D4FF), style = Stroke(width = 1.5f))
      drawCircle(Color(0xFF00D4FF), 2f, Offset(size.width / 2, size.height * 0.55f))
    }

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color(0xFFE0E8FF),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun GeometryPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  var selectedPlan by remember { mutableStateOf("yearly") }

  val features = listOf(
    "Sacred Geometry Patterns",
    "Golden Ratio Templates",
    "Infinite Fractal Generator",
    "Mathematical Visualizations",
    "Export to Vector Formats",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF050510),
            Color(0xFF0D0D1A),
            Color(0xFF1A1A2E),
            Color(0xFF0D0D1A),
          ),
        ),
      ),
  ) {
    GeometryBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "∞ METAPHYSICAL ∞",
        style = TextStyle(
          color = Color(0xFF00D4FF).copy(alpha = 0.9f),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Geometry Pro",
        style = TextStyle(
          fontSize = 34.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFF00D4FF),
              Color(0xFF7B68EE),
              Color(0xFFFF6B9D),
              Color(0xFF00D4FF),
            ),
          ),
        ),
      )

      Text(
        text = "Unlock the Universe's Patterns",
        style = TextStyle(
          color = Color(0xFFE0E8FF).copy(alpha = 0.6f),
          fontSize = 13.sp,
        ),
      )

      Spacer(modifier = Modifier.weight(0.25f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0D0D1A).copy(alpha = 0.7f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          GeometryFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.2f))

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        GeometryPlanOption(
          title = "Lifetime",
          price = "$99.99",
          period = "one-time",
          badge = "BEST VALUE",
          description = "Eternal access to all dimensions",
          isSelected = selectedPlan == "lifetime",
          onClick = { selectedPlan = "lifetime" },
        )

        GeometryPlanOption(
          title = "Yearly",
          price = "$39.99",
          period = "per year",
          description = "Save 67% vs monthly",
          isSelected = selectedPlan == "yearly",
          onClick = { selectedPlan = "yearly" },
        )

        GeometryPlanOption(
          title = "Monthly",
          price = "$9.99",
          period = "per month",
          isSelected = selectedPlan == "monthly",
          onClick = { selectedPlan = "monthly" },
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = when (selectedPlan) {
          "lifetime" -> "One payment, infinite possibilities"
          "yearly" -> "7-day free trial included"
          else -> "Cancel anytime"
        },
        style = TextStyle(
          color = Color(0xFF00D4FF),
          fontSize = 12.sp,
        ),
      )

      Spacer(modifier = Modifier.weight(0.15f))

      Button(
        onClick = {
          val packageType = when (selectedPlan) {
            "lifetime" -> PackageType.LIFETIME
            "yearly" -> PackageType.ANNUAL
            else -> PackageType.MONTHLY
          }
          paywallState.purchase(packageType)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              brush = Brush.linearGradient(
                colors = listOf(
                  Color(0xFF00D4FF),
                  Color(0xFF7B68EE),
                  Color(0xFFFF6B9D),
                ),
              ),
              shape = RoundedCornerShape(14.dp),
            ),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = if (selectedPlan ==
              "lifetime"
            ) {
              "Unlock Forever"
            } else {
              "Start Free Trial"
            },
            style = TextStyle(
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color(0xFFE0E8FF).copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
