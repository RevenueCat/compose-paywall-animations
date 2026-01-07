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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

private const val TAU = 2f * PI.toFloat()

// Heavenly light ray
data class HeavenlyRay(
  val angle: Float,
  val width: Float,
  val length: Float,
  val speed: Float,
  val phase: Float,
  val alpha: Float,
)

// Floating dove particle
data class DoveParticle(
  var x: Float,
  var y: Float,
  val size: Float,
  var wingPhase: Float,
  val speed: Float,
  val amplitude: Float,
  val phaseOffset: Float,
)

// Glowing star/sparkle
data class HeavenlyStar(
  val x: Float,
  val y: Float,
  val size: Float,
  val twinkleSpeed: Float,
  val phase: Float,
)

// Floating golden particle
data class GoldenParticle(
  var x: Float,
  var y: Float,
  var vy: Float,
  val size: Float,
  val alpha: Float,
  var swayPhase: Float,
  val swaySpeed: Float,
)

// Cloud layer
data class HeavenlyCloud(
  val x: Float,
  val y: Float,
  val width: Float,
  val height: Float,
  val alpha: Float,
  val driftSpeed: Float,
)

class BibleAnimationSystem {
  private val lightRays = mutableListOf<HeavenlyRay>()
  private val doves = mutableListOf<DoveParticle>()
  private val stars = mutableListOf<HeavenlyStar>()
  private val particles = mutableListOf<GoldenParticle>()
  private val clouds = mutableListOf<HeavenlyCloud>()
  private val random = Random
  private var initialized = false

  // Divine color palette
  private val goldenLight = Color(0xFFFFD700)
  private val warmGold = Color(0xFFFFC857)
  private val divineWhite = Color(0xFFFFFAF0)
  private val heavenlyBlue = Color(0xFF87CEEB)
  private val softPurple = Color(0xFFE6E6FA)

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    // Create light rays emanating from top center
    for (i in 0 until 12) {
      val baseAngle = -PI.toFloat() / 2 + (i - 5.5f) * 0.12f
      lightRays.add(
        HeavenlyRay(
          angle = baseAngle,
          width = random.nextFloat() * 30f + 20f,
          length = height * 0.9f,
          speed = random.nextFloat() * 0.3f + 0.2f,
          phase = random.nextFloat() * TAU,
          alpha = random.nextFloat() * 0.15f + 0.08f,
        ),
      )
    }

    // Create doves
    for (i in 0 until 4) {
      doves.add(
        DoveParticle(
          x = random.nextFloat() * width,
          y = height * 0.1f + random.nextFloat() * height * 0.25f,
          size = random.nextFloat() * 15f + 20f,
          wingPhase = random.nextFloat() * TAU,
          speed = random.nextFloat() * 20f + 15f,
          amplitude = random.nextFloat() * 20f + 10f,
          phaseOffset = random.nextFloat() * TAU,
        ),
      )
    }

    // Create stars
    for (i in 0 until 25) {
      stars.add(
        HeavenlyStar(
          x = random.nextFloat() * width,
          y = random.nextFloat() * height * 0.5f,
          size = random.nextFloat() * 3f + 1f,
          twinkleSpeed = random.nextFloat() * 2f + 1f,
          phase = random.nextFloat() * TAU,
        ),
      )
    }

    // Create clouds
    for (i in 0 until 6) {
      clouds.add(
        HeavenlyCloud(
          x = random.nextFloat() * width * 1.5f - width * 0.25f,
          y = random.nextFloat() * height * 0.15f,
          width = random.nextFloat() * 150f + 100f,
          height = random.nextFloat() * 40f + 30f,
          alpha = random.nextFloat() * 0.15f + 0.05f,
          driftSpeed = random.nextFloat() * 10f + 5f,
        ),
      )
    }
  }

  fun update(deltaTime: Float, width: Float, height: Float) {
    // Spawn golden particles rising upward
    if (random.nextFloat() < 0.15f && particles.size < 40) {
      particles.add(
        GoldenParticle(
          x = random.nextFloat() * width,
          y = height + 20f,
          vy = -(random.nextFloat() * 40f + 30f),
          size = random.nextFloat() * 4f + 2f,
          alpha = random.nextFloat() * 0.6f + 0.3f,
          swayPhase = random.nextFloat() * TAU,
          swaySpeed = random.nextFloat() * 2f + 1f,
        ),
      )
    }

    // Update particles
    val iterator = particles.iterator()
    while (iterator.hasNext()) {
      val particle = iterator.next()
      particle.y += particle.vy * deltaTime
      particle.x += sin(particle.swayPhase) * 0.5f
      particle.swayPhase += particle.swaySpeed * deltaTime

      if (particle.y < -20f) {
        iterator.remove()
      }
    }
  }

  fun draw(drawScope: DrawScope, totalTime: Float, width: Float, height: Float) {
    // Draw heavenly gradient background overlay
    drawHeavenlyGlow(drawScope, width, height, totalTime)

    // Draw clouds
    for (cloud in clouds) {
      drawCloud(drawScope, cloud, totalTime, width)
    }

    // Draw light rays
    for (ray in lightRays) {
      drawLightRay(drawScope, ray, totalTime, width, height)
    }

    // Draw stars
    for (star in stars) {
      drawStar(drawScope, star, totalTime)
    }

    // Draw golden particles
    for (particle in particles) {
      drawGoldenParticle(drawScope, particle, totalTime)
    }

    // Draw shiny cross at center top (replacing halo)
    drawShinyCross(drawScope, width * 0.5f, height * 0.12f, totalTime)

    // Draw doves
    for (dove in doves) {
      drawDove(drawScope, dove, totalTime, width)
    }
  }

  private fun drawHeavenlyGlow(drawScope: DrawScope, width: Float, height: Float, time: Float) {
    val pulse = sin(time * 0.5f) * 0.1f + 0.9f

    // Divine light from above
    drawScope.drawRect(
      brush = Brush.radialGradient(
        colors = listOf(
          goldenLight.copy(alpha = 0.25f * pulse),
          warmGold.copy(alpha = 0.15f * pulse),
          Color.Transparent,
        ),
        center = Offset(width / 2, 0f),
        radius = height * 0.8f,
      ),
      size = Size(width, height),
    )
  }

  private fun drawCloud(
    drawScope: DrawScope,
    cloud: HeavenlyCloud,
    time: Float,
    screenWidth: Float,
  ) {
    val x = (cloud.x + time * cloud.driftSpeed) % (screenWidth + cloud.width * 2) - cloud.width
    val pulse = sin(time * 0.3f + cloud.x * 0.01f) * 0.3f + 0.7f

    // Draw soft cloud shape using multiple circles
    val cloudColor = divineWhite.copy(alpha = cloud.alpha * pulse)

    for (i in 0 until 5) {
      val offsetX = (i - 2) * cloud.width * 0.2f
      val offsetY = sin(i * 1.2f) * cloud.height * 0.2f
      val circleSize = cloud.height * (0.8f + sin(i * 0.8f) * 0.3f)

      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            cloudColor,
            cloudColor.copy(alpha = cloudColor.alpha * 0.5f),
            Color.Transparent,
          ),
          center = Offset(x + offsetX, cloud.y + offsetY),
          radius = circleSize,
        ),
        radius = circleSize,
        center = Offset(x + offsetX, cloud.y + offsetY),
      )
    }
  }

  private fun drawLightRay(
    drawScope: DrawScope,
    ray: HeavenlyRay,
    time: Float,
    width: Float,
    height: Float,
  ) {
    val pulse = sin(time * ray.speed + ray.phase) * 0.4f + 0.6f
    val originX = width / 2
    val originY = -20f

    val endX = originX + cos(ray.angle) * ray.length
    val endY = originY + sin(ray.angle) * ray.length

    // Calculate perpendicular for ray width
    val perpX = -sin(ray.angle) * ray.width * pulse
    val perpY = cos(ray.angle) * ray.width * pulse

    val path = Path().apply {
      moveTo(originX - perpX * 0.1f, originY - perpY * 0.1f)
      lineTo(originX + perpX * 0.1f, originY + perpY * 0.1f)
      lineTo(endX + perpX, endY + perpY)
      lineTo(endX - perpX, endY - perpY)
      close()
    }

    drawScope.drawPath(
      path = path,
      brush = Brush.linearGradient(
        colors = listOf(
          goldenLight.copy(alpha = ray.alpha * pulse),
          warmGold.copy(alpha = ray.alpha * pulse * 0.5f),
          Color.Transparent,
        ),
        start = Offset(originX, originY),
        end = Offset(endX, endY),
      ),
    )
  }

  private fun drawStar(drawScope: DrawScope, star: HeavenlyStar, time: Float) {
    val twinkle = sin(time * star.twinkleSpeed + star.phase) * 0.5f + 0.5f
    val size = star.size * (0.5f + twinkle * 0.5f)

    // Draw 4-pointed star
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = twinkle * 0.9f),
          goldenLight.copy(alpha = twinkle * 0.5f),
          Color.Transparent,
        ),
        center = Offset(star.x, star.y),
        radius = size * 3f,
      ),
      radius = size * 3f,
      center = Offset(star.x, star.y),
    )

    // Star cross shape
    val rayLength = size * 2f
    drawScope.drawLine(
      color = Color.White.copy(alpha = twinkle * 0.8f),
      start = Offset(star.x - rayLength, star.y),
      end = Offset(star.x + rayLength, star.y),
      strokeWidth = 1f,
    )
    drawScope.drawLine(
      color = Color.White.copy(alpha = twinkle * 0.8f),
      start = Offset(star.x, star.y - rayLength),
      end = Offset(star.x, star.y + rayLength),
      strokeWidth = 1f,
    )
  }

  private fun drawGoldenParticle(drawScope: DrawScope, particle: GoldenParticle, time: Float) {
    val twinkle = sin(time * 3f + particle.swayPhase) * 0.3f + 0.7f

    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = particle.alpha * twinkle),
          goldenLight.copy(alpha = particle.alpha * twinkle * 0.6f),
          Color.Transparent,
        ),
        center = Offset(particle.x, particle.y),
        radius = particle.size * 3f,
      ),
      radius = particle.size * 3f,
      center = Offset(particle.x, particle.y),
    )
  }

  private fun drawShinyCross(drawScope: DrawScope, cx: Float, cy: Float, time: Float) {
    val pulse = sin(time * 1.5f) * 0.1f + 1f
    val glow = sin(time * 2f) * 0.3f + 0.7f
    val shimmer = sin(time * 4f) * 0.5f + 0.5f

    val crossWidth = 12f * pulse
    val crossHeight = 90f * pulse
    val crossArmWidth = 65f * pulse
    val crossArmHeight = 12f * pulse
    val crossArmY = cy - 18f * pulse

    // Outermost divine radiance - large soft glow
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.15f * glow),
          goldenLight.copy(alpha = 0.1f * glow),
          Color.Transparent,
        ),
        center = Offset(cx, cy),
        radius = 180f * pulse,
      ),
      radius = 180f * pulse,
      center = Offset(cx, cy),
    )

    // Middle radiance layer
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          goldenLight.copy(alpha = 0.35f * glow),
          warmGold.copy(alpha = 0.2f * glow),
          Color.Transparent,
        ),
        center = Offset(cx, cy),
        radius = 120f * pulse,
      ),
      radius = 120f * pulse,
      center = Offset(cx, cy),
    )

    // Inner bright glow
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.6f * glow),
          goldenLight.copy(alpha = 0.4f * glow),
          Color.Transparent,
        ),
        center = Offset(cx, cy),
        radius = 70f * pulse,
      ),
      radius = 70f * pulse,
      center = Offset(cx, cy),
    )

    // Shining rays emanating from cross
    val rayCount = 12
    for (i in 0 until rayCount) {
      val angle = (i.toFloat() / rayCount) * TAU + time * 0.3f
      val rayLength = (60f + sin(time * 3f + i * 0.5f) * 20f) * pulse
      val rayAlpha = (0.4f + sin(time * 2f + i * 0.8f) * 0.2f) * glow

      val startX = cx + cos(angle) * 25f
      val startY = cy + sin(angle) * 25f
      val endX = cx + cos(angle) * rayLength
      val endY = cy + sin(angle) * rayLength

      drawScope.drawLine(
        brush = Brush.linearGradient(
          colors = listOf(
            Color.White.copy(alpha = rayAlpha),
            goldenLight.copy(alpha = rayAlpha * 0.5f),
            Color.Transparent,
          ),
          start = Offset(startX, startY),
          end = Offset(endX, endY),
        ),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 3f * pulse,
        cap = StrokeCap.Round,
      )
    }

    // Cross vertical beam
    val verticalPath = Path().apply {
      addRoundRect(
        RoundRect(
          left = cx - crossWidth / 2,
          top = cy - crossHeight / 2,
          right = cx + crossWidth / 2,
          bottom = cy + crossHeight / 2,
          cornerRadius = CornerRadius(crossWidth / 2),
        ),
      )
    }

    // Cross horizontal beam
    val horizontalPath = Path().apply {
      addRoundRect(
        RoundRect(
          left = cx - crossArmWidth / 2,
          top = crossArmY - crossArmHeight / 2,
          right = cx + crossArmWidth / 2,
          bottom = crossArmY + crossArmHeight / 2,
          cornerRadius = CornerRadius(crossArmHeight / 2),
        ),
      )
    }

    // Shimmering cross gradient
    val crossGradient = Brush.linearGradient(
      colors = listOf(
        Color.White,
        Color(0xFFFFF8DC), // Cornsilk
        goldenLight,
        Color(0xFFFFF8DC),
        Color.White,
      ),
      start = Offset(cx - crossArmWidth / 2, cy - crossHeight / 2),
      end = Offset(cx + crossArmWidth / 2, cy + crossHeight / 2),
    )

    // Draw shadow for depth
    drawScope.drawPath(
      Path().apply {
        addRoundRect(
          RoundRect(
            left = cx - crossWidth / 2 + 3f,
            top = cy - crossHeight / 2 + 3f,
            right = cx + crossWidth / 2 + 3f,
            bottom = cy + crossHeight / 2 + 3f,
            cornerRadius = CornerRadius(crossWidth / 2),
          ),
        )
      },
      color = Color.Black.copy(alpha = 0.2f),
    )
    drawScope.drawPath(
      Path().apply {
        addRoundRect(
          RoundRect(
            left = cx - crossArmWidth / 2 + 3f,
            top = crossArmY - crossArmHeight / 2 + 3f,
            right = cx + crossArmWidth / 2 + 3f,
            bottom = crossArmY + crossArmHeight / 2 + 3f,
            cornerRadius = CornerRadius(crossArmHeight / 2),
          ),
        )
      },
      color = Color.Black.copy(alpha = 0.2f),
    )

    // Draw main cross
    drawScope.drawPath(verticalPath, crossGradient)
    drawScope.drawPath(horizontalPath, crossGradient)

    // Bright edge highlight (shimmer effect)
    val highlightAlpha = 0.7f + shimmer * 0.3f
    drawScope.drawPath(
      verticalPath,
      color = Color.White.copy(alpha = highlightAlpha),
      style = Stroke(width = 2f),
    )
    drawScope.drawPath(
      horizontalPath,
      color = Color.White.copy(alpha = highlightAlpha),
      style = Stroke(width = 2f),
    )

    // Inner shine highlight
    drawScope.drawLine(
      color = Color.White.copy(alpha = 0.8f * shimmer),
      start = Offset(cx, cy - crossHeight / 2 + 8f),
      end = Offset(cx, cy + crossHeight / 2 - 8f),
      strokeWidth = 2f,
      cap = StrokeCap.Round,
    )
    drawScope.drawLine(
      color = Color.White.copy(alpha = 0.8f * shimmer),
      start = Offset(cx - crossArmWidth / 2 + 8f, crossArmY),
      end = Offset(cx + crossArmWidth / 2 - 8f, crossArmY),
      strokeWidth = 2f,
      cap = StrokeCap.Round,
    )

    // Sparkle points at cross tips
    val sparklePositions = listOf(
      Offset(cx, cy - crossHeight / 2), // Top
      Offset(cx, cy + crossHeight / 2), // Bottom
      Offset(cx - crossArmWidth / 2, crossArmY), // Left
      Offset(cx + crossArmWidth / 2, crossArmY), // Right
      Offset(cx, crossArmY), // Center intersection
    )

    for ((index, pos) in sparklePositions.withIndex()) {
      val sparklePhase = time * 3f + index * 1.2f
      val sparkleAlpha = (sin(sparklePhase) * 0.5f + 0.5f) * glow
      val sparkleSize = 8f + sin(sparklePhase) * 4f

      // Sparkle glow
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White.copy(alpha = sparkleAlpha),
            goldenLight.copy(alpha = sparkleAlpha * 0.5f),
            Color.Transparent,
          ),
          center = pos,
          radius = sparkleSize * 2f,
        ),
        radius = sparkleSize * 2f,
        center = pos,
      )

      // Sparkle cross rays
      val rayLen = sparkleSize * 1.5f
      drawScope.drawLine(
        color = Color.White.copy(alpha = sparkleAlpha * 0.9f),
        start = Offset(pos.x - rayLen, pos.y),
        end = Offset(pos.x + rayLen, pos.y),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round,
      )
      drawScope.drawLine(
        color = Color.White.copy(alpha = sparkleAlpha * 0.9f),
        start = Offset(pos.x, pos.y - rayLen),
        end = Offset(pos.x, pos.y + rayLen),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round,
      )
    }
  }

  private fun drawDove(drawScope: DrawScope, dove: DoveParticle, time: Float, screenWidth: Float) {
    val x = (dove.x + time * dove.speed) % (screenWidth + dove.size * 4) - dove.size * 2
    val y = dove.y + sin(time * 1.5f + dove.phaseOffset) * dove.amplitude
    val wingFlap = sin(time * 8f + dove.wingPhase) * 0.4f

    val doveColor = divineWhite.copy(alpha = 0.9f)

    // Body
    drawScope.drawOval(
      color = doveColor,
      topLeft = Offset(x - dove.size * 0.4f, y - dove.size * 0.15f),
      size = Size(dove.size * 0.8f, dove.size * 0.3f),
    )

    // Head
    drawScope.drawCircle(
      color = doveColor,
      radius = dove.size * 0.15f,
      center = Offset(x + dove.size * 0.35f, y - dove.size * 0.05f),
    )

    // Left wing
    val leftWingPath = Path().apply {
      moveTo(x - dove.size * 0.1f, y)
      quadraticTo(
        x - dove.size * 0.5f,
        y - dove.size * (0.5f + wingFlap),
        x - dove.size * 0.3f,
        y - dove.size * 0.1f,
      )
    }
    drawScope.drawPath(
      leftWingPath,
      color = doveColor,
      style = Stroke(width = dove.size * 0.08f, cap = StrokeCap.Round),
    )

    // Right wing
    val rightWingPath = Path().apply {
      moveTo(x + dove.size * 0.1f, y)
      quadraticTo(
        x + dove.size * 0.2f,
        y - dove.size * (0.4f + wingFlap * 0.8f),
        x + dove.size * 0.05f,
        y - dove.size * 0.08f,
      )
    }
    drawScope.drawPath(
      rightWingPath,
      color = doveColor,
      style = Stroke(width = dove.size * 0.06f, cap = StrokeCap.Round),
    )

    // Tail
    drawScope.drawLine(
      color = doveColor,
      start = Offset(x - dove.size * 0.4f, y),
      end = Offset(x - dove.size * 0.6f, y + dove.size * 0.1f),
      strokeWidth = dove.size * 0.06f,
      cap = StrokeCap.Round,
    )

    // Glow around dove
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.3f),
          Color.Transparent,
        ),
        center = Offset(x, y),
        radius = dove.size,
      ),
      radius = dove.size,
      center = Offset(x, y),
    )
  }
}

@Composable
fun BibleBackground(modifier: Modifier = Modifier) {
  val system = remember { BibleAnimationSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(Size.Zero) }

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

        if (canvasSize.width > 0 && canvasSize.height > 0) {
          system.initialize(canvasSize.width, canvasSize.height)
          system.update(deltaTime, canvasSize.width, canvasSize.height)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    system.draw(this, totalTime, size.width, size.height)
  }
}

@Composable
fun BiblePlanOption(
  title: String,
  price: String,
  period: String,
  badge: String? = null,
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
          Color(0xFF2A1810).copy(alpha = 0.9f)
        } else {
          Color(0xFF1A0F08).copy(alpha = 0.7f)
        },
      )
      .clickable(onClick = onClick)
      .padding(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Cross-shaped checkbox
        Box(
          modifier = Modifier.size(24.dp),
          contentAlignment = Alignment.Center,
        ) {
          if (isSelected) {
            Canvas(modifier = Modifier.size(20.dp)) {
              // Golden circle
              drawCircle(
                brush = Brush.radialGradient(
                  colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFFC857),
                  ),
                ),
                radius = size.width / 2,
              )
              // Small cross
              val crossColor = Color(0xFF1A0F08)
              drawLine(
                color = crossColor,
                start = Offset(size.width / 2, size.height * 0.25f),
                end = Offset(size.width / 2, size.height * 0.75f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
              )
              drawLine(
                color = crossColor,
                start = Offset(size.width * 0.3f, size.height * 0.4f),
                end = Offset(size.width * 0.7f, size.height * 0.4f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round,
              )
            }
          } else {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
            )
          }
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = title,
              style = TextStyle(
                color = Color(0xFFFFFAF0),
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
                      colors = listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFFC857),
                      ),
                    ),
                  )
                  .padding(horizontal = 6.dp, vertical = 2.dp),
              ) {
                Text(
                  text = badge,
                  style = TextStyle(
                    color = Color(0xFF1A0F08),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                  ),
                )
              }
            }
          }
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = price,
          style = TextStyle(
            color = Color(0xFFFFD700),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = period,
          style = TextStyle(
            color = Color(0xFFFFFAF0).copy(alpha = 0.6f),
            fontSize = 12.sp,
          ),
        )
      }
    }
  }
}

@Composable
fun BibleFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Small cross icon
    Canvas(modifier = Modifier.size(18.dp)) {
      val gold = Color(0xFFFFD700)
      drawLine(
        color = gold,
        start = Offset(size.width / 2, size.height * 0.15f),
        end = Offset(size.width / 2, size.height * 0.85f),
        strokeWidth = 2f,
        cap = StrokeCap.Round,
      )
      drawLine(
        color = gold,
        start = Offset(size.width * 0.2f, size.height * 0.35f),
        end = Offset(size.width * 0.8f, size.height * 0.35f),
        strokeWidth = 2f,
        cap = StrokeCap.Round,
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color(0xFFFFFAF0),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun BiblePaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  var selectedPlan by remember { mutableStateOf("yearly") }

  val features = listOf(
    "Daily Scripture Readings",
    "Audio Bible Narration",
    "Prayer Journal & Tracker",
    "Devotional Plans",
    "Offline Access",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1A0F08),
            Color(0xFF2A1810),
            Color(0xFF3D2517),
            Color(0xFF1A0F08),
          ),
        ),
      ),
  ) {
    BibleBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(80.dp))

      Text(
        text = "WALK IN FAITH",
        style = TextStyle(
          color = Color(0xFFFFD700).copy(alpha = 0.9f),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Bible Pro",
        style = TextStyle(
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFFFD700),
              Color(0xFFFFC857),
              Color(0xFFFFE4B5),
            ),
          ),
        ),
      )

      Text(
        text = "\"For where two or three gather...\"",
        style = TextStyle(
          color = Color(0xFFFFFAF0).copy(alpha = 0.7f),
          fontSize = 13.sp,
          fontWeight = FontWeight.Normal,
        ),
      )

      Spacer(modifier = Modifier.weight(0.25f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF1A0F08).copy(alpha = 0.85f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          BibleFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.2f))

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        BiblePlanOption(
          title = "Lifetime",
          price = "$79.99",
          period = "one-time",
          badge = "BLESSED",
          isSelected = selectedPlan == "lifetime",
          onClick = { selectedPlan = "lifetime" },
        )

        BiblePlanOption(
          title = "Yearly",
          price = "$29.99",
          period = "per year",
          isSelected = selectedPlan == "yearly",
          onClick = { selectedPlan = "yearly" },
        )

        BiblePlanOption(
          title = "Monthly",
          price = "$4.99",
          period = "per month",
          isSelected = selectedPlan == "monthly",
          onClick = { selectedPlan = "monthly" },
        )
      }

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
          .height(56.dp),
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
                  Color(0xFFFFD700),
                  Color(0xFFFFC857),
                  Color(0xFFFFE4B5),
                ),
              ),
              shape = RoundedCornerShape(14.dp),
            ),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Begin Your Journey",
            style = TextStyle(
              color = Color(0xFF1A0F08),
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
          color = Color(0xFFFFFAF0).copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
