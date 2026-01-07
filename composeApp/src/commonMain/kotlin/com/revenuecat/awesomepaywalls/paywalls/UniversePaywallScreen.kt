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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

data class Star(
  val x: Float,
  val y: Float,
  val size: Float,
  val twinkleSpeed: Float,
  val twinklePhase: Float,
)

data class Planet(
  val orbitRadius: Float,
  val size: Float,
  val color: Color,
  val orbitSpeed: Float,
  val initialAngle: Float,
  val hasRing: Boolean = false,
  val ringColor: Color = Color.Transparent,
  val moons: List<Moon> = emptyList(),
)

data class Moon(
  val orbitRadius: Float,
  val size: Float,
  val color: Color,
  val orbitSpeed: Float,
  val initialAngle: Float,
)

data class ShootingStar(
  var x: Float,
  var y: Float,
  var angle: Float,
  var speed: Float,
  var length: Float,
  var alpha: Float,
  var life: Float,
)

class UniverseSystem {
  private val stars = mutableListOf<Star>()
  private val shootingStars = mutableListOf<ShootingStar>()
  private val random = Random
  private var initialized = false
  private var lastShootingStarTime = 0f

  val planets = listOf(
    Planet(
      orbitRadius = 0.18f,
      size = 10f,
      color = Color(0xFFB0B0B0),
      orbitSpeed = 2.5f,
      initialAngle = 0f,
    ),
    Planet(
      orbitRadius = 0.28f,
      size = 14f,
      color = Color(0xFFE07020),
      orbitSpeed = 1.8f,
      initialAngle = 120f,
    ),
    Planet(
      orbitRadius = 0.38f,
      size = 16f,
      color = Color(0xFF4080FF),
      orbitSpeed = 1.2f,
      initialAngle = 240f,
      moons = listOf(
        Moon(24f, 4f, Color(0xFFC0C0C0), 3f, 0f),
      ),
    ),
    Planet(
      orbitRadius = 0.48f,
      size = 20f,
      color = Color(0xFFE0C070),
      orbitSpeed = 0.8f,
      initialAngle = 60f,
      hasRing = true,
      ringColor = Color(0xFFD4A574),
    ),
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    repeat(100) {
      stars.add(
        Star(
          x = random.nextFloat() * width,
          y = random.nextFloat() * height,
          size = random.nextFloat() * 2f + 0.5f,
          twinkleSpeed = random.nextFloat() * 2f + 1f,
          twinklePhase = random.nextFloat() * PI.toFloat() * 2f,
        ),
      )
    }
  }

  fun update(deltaTime: Float, totalTime: Float, width: Float, height: Float) {
    if (totalTime - lastShootingStarTime > 3f && random.nextFloat() < 0.02f) {
      lastShootingStarTime = totalTime
      val startX = random.nextFloat() * width * 0.8f
      val startY = random.nextFloat() * height * 0.3f
      shootingStars.add(
        ShootingStar(
          x = startX,
          y = startY,
          angle = random.nextFloat() * 30f + 20f,
          speed = random.nextFloat() * 400f + 300f,
          length = random.nextFloat() * 60f + 40f,
          alpha = 1f,
          life = 1f,
        ),
      )
    }

    val iterator = shootingStars.iterator()
    while (iterator.hasNext()) {
      val star = iterator.next()
      val angleRad = star.angle * PI.toFloat() / 180f
      star.x += cos(angleRad) * star.speed * deltaTime
      star.y += sin(angleRad) * star.speed * deltaTime
      star.life -= deltaTime * 1.5f
      star.alpha = star.life.coerceIn(0f, 1f)

      if (star.life <= 0f || star.x > width || star.y > height) {
        iterator.remove()
      }
    }
  }

  fun drawStars(drawScope: DrawScope, totalTime: Float) {
    for (star in stars) {
      val twinkle = (sin(totalTime * star.twinkleSpeed + star.twinklePhase) + 1f) / 2f
      val alpha = 0.3f + twinkle * 0.7f
      drawScope.drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = star.size,
        center = Offset(star.x, star.y),
      )
    }
  }

  fun drawShootingStars(drawScope: DrawScope) {
    for (star in shootingStars) {
      val angleRad = star.angle * PI.toFloat() / 180f
      val endX = star.x - cos(angleRad) * star.length
      val endY = star.y - sin(angleRad) * star.length

      drawScope.drawLine(
        brush = Brush.linearGradient(
          colors = listOf(
            Color.White.copy(alpha = star.alpha),
            Color.White.copy(alpha = star.alpha * 0.5f),
            Color.Transparent,
          ),
          start = Offset(star.x, star.y),
          end = Offset(endX, endY),
        ),
        start = Offset(star.x, star.y),
        end = Offset(endX, endY),
        strokeWidth = 2f,
        cap = StrokeCap.Round,
      )
    }
  }
}

@Composable
fun UniverseBackground(modifier: Modifier = Modifier) {
  val system = remember { UniverseSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

  val infiniteTransition = rememberInfiniteTransition(label = "planets")
  val orbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(20000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "orbit",
  )

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
          system.update(deltaTime, totalTime, canvasSize.width, canvasSize.height)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    val centerX = size.width / 2
    val centerY = size.height * 0.28f
    val orbitScale = size.width

    system.drawStars(this, totalTime)
    system.drawShootingStars(this)

    for (planet in system.planets) {
      val scaledRadius = planet.orbitRadius * orbitScale
      drawOval(
        color = Color.White.copy(alpha = 0.15f),
        topLeft = Offset(centerX - scaledRadius, centerY - scaledRadius * 0.2f),
        size = androidx.compose.ui.geometry.Size(scaledRadius * 2f, scaledRadius * 0.4f),
        style = Stroke(width = 1.5f),
      )
    }

    val sunSize = size.width * 0.06f
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFE55C),
          Color(0xFFFFAA00),
          Color(0xFFFF6600),
        ),
        center = Offset(centerX, centerY),
        radius = sunSize,
      ),
      radius = sunSize,
      center = Offset(centerX, centerY),
    )

    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFFF00).copy(alpha = 0.3f),
          Color.Transparent,
        ),
        center = Offset(centerX, centerY),
        radius = sunSize * 1.6f,
      ),
      radius = sunSize * 1.6f,
      center = Offset(centerX, centerY),
    )

    system.planets.forEachIndexed { index, planet ->
      val scaledRadius = planet.orbitRadius * orbitScale
      val currentAngle =
        (planet.initialAngle + orbitAngle * planet.orbitSpeed) * PI.toFloat() / 180f
      val planetX = centerX + cos(currentAngle) * scaledRadius
      val planetY = centerY + sin(currentAngle) * scaledRadius * 0.2f
      val scaledPlanetSize = planet.size * (size.width / 400f)

      val rotationSpeed = 5f - index * 0.8f
      val rotationPhase = orbitAngle * rotationSpeed

      if (planet.hasRing) {
        drawOval(
          color = planet.ringColor.copy(alpha = 0.6f),
          topLeft = Offset(
            planetX - scaledPlanetSize * 1.8f,
            planetY - scaledPlanetSize * 0.3f,
          ),
          size = androidx.compose.ui.geometry.Size(
            scaledPlanetSize * 3.6f,
            scaledPlanetSize * 0.6f,
          ),
          style = Stroke(width = 3f),
        )
      }

      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            planet.color,
            planet.color.copy(alpha = 0.8f),
            planet.color.copy(alpha = 0.6f),
          ),
          center = Offset(
            planetX - scaledPlanetSize * 0.3f,
            planetY - scaledPlanetSize * 0.3f,
          ),
          radius = scaledPlanetSize,
        ),
        radius = scaledPlanetSize,
        center = Offset(planetX, planetY),
      )

      val numBands = 3
      for (band in 0 until numBands) {
        val bandOffset = (band - 1) * scaledPlanetSize * 0.35f
        val bandPhase = rotationPhase + band * 40f
        val bandX = sin(bandPhase * PI.toFloat() / 180f) * scaledPlanetSize * 0.7f

        val bandVisible = cos(bandPhase * PI.toFloat() / 180f) > -0.3f
        if (bandVisible) {
          val bandAlpha =
            (cos(bandPhase * PI.toFloat() / 180f) + 0.3f).coerceIn(0f, 1f) * 0.25f
          val bandWidth =
            abs(cos(bandPhase * PI.toFloat() / 180f)) * scaledPlanetSize * 0.15f + 1f

          drawLine(
            color = planet.color.copy(
              alpha = bandAlpha,
            ).compositeOver(Color.White.copy(alpha = 0.1f)),
            start = Offset(
              planetX + bandX,
              planetY + bandOffset - scaledPlanetSize * 0.5f,
            ),
            end = Offset(
              planetX + bandX,
              planetY + bandOffset + scaledPlanetSize * 0.5f,
            ),
            strokeWidth = bandWidth,
            cap = StrokeCap.Round,
          )
        }
      }

      drawCircle(
        brush = Brush.linearGradient(
          colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.5f),
          ),
          start = Offset(planetX - scaledPlanetSize, planetY),
          end = Offset(planetX + scaledPlanetSize, planetY),
        ),
        radius = scaledPlanetSize,
        center = Offset(planetX, planetY),
      )

      drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = scaledPlanetSize * 0.2f,
        center = Offset(
          planetX - scaledPlanetSize * 0.4f,
          planetY - scaledPlanetSize * 0.4f,
        ),
      )

      for (moon in planet.moons) {
        val moonAngle =
          (moon.initialAngle + orbitAngle * moon.orbitSpeed) * PI.toFloat() / 180f
        val scaledMoonOrbit = moon.orbitRadius * (size.width / 400f)
        val moonX = planetX + cos(moonAngle) * scaledMoonOrbit
        val moonY = planetY + sin(moonAngle) * scaledMoonOrbit * 0.25f
        val scaledMoonSize = moon.size * (size.width / 400f)

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(moon.color, moon.color.copy(alpha = 0.7f)),
            center = Offset(moonX, moonY),
            radius = scaledMoonSize,
          ),
          radius = scaledMoonSize,
          center = Offset(moonX, moonY),
        )
      }
    }
  }
}

@Composable
fun CosmicFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
          ),
        ),
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.size(10.dp)) {
        drawCircle(
          color = Color.White,
          radius = 3f,
          center = Offset(size.width / 2, size.height / 2),
        )
        for (i in 0 until 4) {
          val angle = i * 90f * PI.toFloat() / 180f
          drawLine(
            color = Color.White,
            start = Offset(
              size.width / 2 + cos(angle) * 2f,
              size.height / 2 + sin(angle) * 2f,
            ),
            end = Offset(
              size.width / 2 + cos(angle) * 5f,
              size.height / 2 + sin(angle) * 5f,
            ),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round,
          )
        }
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun UniversePaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  val features = listOf(
    "Explore Unlimited Galaxies",
    "Access to All Planets",
    "Real-time Space Events",
    "Ad-free Cosmic Experience",
    "Exclusive Nebula Content",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0A0A1A),
            Color(0xFF1A1030),
            Color(0xFF0D0D20),
            Color(0xFF050510),
          ),
        ),
      ),
  ) {
    UniverseBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "EXPLORE THE",
        style = TextStyle(
          color = Color(0xFF8B5CF6),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Text(
        text = "Universe",
        style = TextStyle(
          fontSize = 42.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFF8B5CF6),
              Color(0xFF06B6D4),
              Color(0xFFEC4899),
            ),
          ),
        ),
      )

      Text(
        text = "Unlock the secrets of the cosmos",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 14.sp,
          textAlign = TextAlign.Center,
        ),
        modifier = Modifier.padding(top = 8.dp),
      )

      Spacer(modifier = Modifier.height(200.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.05f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          CosmicFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$79.99/year",
          style = TextStyle(
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "3-day free trial",
          style = TextStyle(
            color = Color(0xFF8B5CF6),
            fontSize = 14.sp,
          ),
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = { paywallState.purchase(PackageType.ANNUAL) },
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
                  Color(0xFF8B5CF6),
                  Color(0xFF6366F1),
                  Color(0xFF06B6D4),
                ),
              ),
              shape = RoundedCornerShape(14.dp),
            ),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Start Exploring",
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
          color = Color.White.copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
