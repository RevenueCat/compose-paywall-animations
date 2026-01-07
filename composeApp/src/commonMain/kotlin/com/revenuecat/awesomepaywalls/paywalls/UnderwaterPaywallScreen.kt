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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

data class SeaBubble(
  var x: Float,
  var y: Float,
  var bubbleRadius: Float,
  var riseSpeed: Float,
  var wobblePhase: Float,
  var wobbleFreq: Float,
)

data class SeaFish(
  var x: Float,
  var y: Float,
  var fishSize: Float,
  var swimSpeed: Float,
  var fishColor: Color,
  var direction: Int,
  var alpha: Float = 1f,
)

data class LightRay(val x: Float, val width: Float, val alpha: Float, val swayPhase: Float)

class UnderwaterSystem {
  private val bubbles = mutableListOf<SeaBubble>()
  private val fishes = mutableListOf<SeaFish>()
  private val lightRays = mutableListOf<LightRay>()
  private val particles = mutableListOf<Offset>()
  private val random = Random
  private var initialized = false

  private val fishColors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFFFFE66D),
    Color(0xFF4ECDC4),
    Color(0xFFFF8C42),
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    repeat(20) {
      bubbles.add(createBubble(width, height, random.nextFloat() * height))
    }

    repeat(5) {
      lightRays.add(
        LightRay(
          x = random.nextFloat() * width,
          width = random.nextFloat() * 60f + 40f,
          alpha = random.nextFloat() * 0.15f + 0.05f,
          swayPhase = random.nextFloat() * PI.toFloat() * 2f,
        ),
      )
    }

    repeat(6) {
      fishes.add(createFish(width, height))
    }

    repeat(30) {
      particles.add(Offset(random.nextFloat() * width, random.nextFloat() * height))
    }
  }

  private fun createBubble(width: Float, height: Float, startY: Float = height + 20f): SeaBubble =
    SeaBubble(
      x = random.nextFloat() * width,
      y = startY,
      bubbleRadius = random.nextFloat() * 8f + 3f,
      riseSpeed = random.nextFloat() * 40f + 20f,
      wobblePhase = random.nextFloat() * PI.toFloat() * 2f,
      wobbleFreq = random.nextFloat() * 2f + 1f,
    )

  private fun createFish(width: Float, height: Float): SeaFish {
    val direction = if (random.nextBoolean()) 1 else -1
    return SeaFish(
      x = if (direction == 1) -50f else width + 50f,
      y = random.nextFloat() * height * 0.6f + height * 0.2f,
      fishSize = random.nextFloat() * 15f + 10f,
      swimSpeed = random.nextFloat() * 30f + 20f,
      fishColor = fishColors[random.nextInt(fishColors.size)],
      direction = direction,
    )
  }

  fun update(deltaTime: Float, totalTime: Float, width: Float, height: Float) {
    val bubbleIterator = bubbles.iterator()
    while (bubbleIterator.hasNext()) {
      val bubble = bubbleIterator.next()
      bubble.y -= bubble.riseSpeed * deltaTime
      bubble.x += sin(totalTime * bubble.wobbleFreq + bubble.wobblePhase) * 20f * deltaTime

      if (bubble.y < -bubble.bubbleRadius * 2) {
        bubbleIterator.remove()
      }
    }

    if (bubbles.size < 20 && random.nextFloat() < 0.1f) {
      bubbles.add(createBubble(width, height))
    }

    val fishIterator = fishes.iterator()
    while (fishIterator.hasNext()) {
      val f = fishIterator.next()
      f.x += f.swimSpeed * f.direction * deltaTime
      f.y += sin(totalTime * 2f + f.x * 0.01f) * 10f * deltaTime

      // Fade out when approaching screen edges
      val fadeDistance = 80f
      if (f.direction == 1) {
        // Moving right - fade out near right edge
        if (f.x > width - fadeDistance) {
          f.alpha = ((width + 50f - f.x) / (fadeDistance + 50f)).coerceIn(0f, 1f)
        }
      } else {
        // Moving left - fade out near left edge
        if (f.x < fadeDistance) {
          f.alpha = ((f.x + 50f) / (fadeDistance + 50f)).coerceIn(0f, 1f)
        }
      }

      if ((f.direction == 1 && f.x > width + 50f) || (f.direction == -1 && f.x < -50f)) {
        fishIterator.remove()
      }
    }

    if (fishes.size < 6 && random.nextFloat() < 0.02f) {
      fishes.add(createFish(width, height))
    }
  }

  fun draw(drawScope: DrawScope, totalTime: Float) {
    val width = drawScope.size.width
    val height = drawScope.size.height

    for (ray in lightRays) {
      val sway = sin(totalTime * 0.5f + ray.swayPhase) * 30f
      val path = Path().apply {
        moveTo(ray.x + sway, 0f)
        lineTo(ray.x + ray.width + sway, 0f)
        lineTo(ray.x + ray.width * 2f + sway * 0.5f, height)
        lineTo(ray.x - ray.width * 0.5f + sway * 0.5f, height)
        close()
      }
      drawScope.drawPath(
        path = path,
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.White.copy(alpha = ray.alpha),
            Color.White.copy(alpha = ray.alpha * 0.5f),
            Color.Transparent,
          ),
        ),
      )
    }

    for (particle in particles) {
      val animatedY = (particle.y + totalTime * 5f) % height
      val twinkle = (sin(totalTime * 3f + particle.x) + 1f) / 2f
      drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.3f + twinkle * 0.3f),
        radius = 1.5f,
        center = Offset(particle.x, animatedY),
      )
    }

    for (bubble in bubbles) {
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White.copy(alpha = 0.4f),
            Color.White.copy(alpha = 0.2f),
            Color.Transparent,
          ),
          center = Offset(
            bubble.x - bubble.bubbleRadius * 0.3f,
            bubble.y - bubble.bubbleRadius * 0.3f,
          ),
          radius = bubble.bubbleRadius,
        ),
        radius = bubble.bubbleRadius,
        center = Offset(bubble.x, bubble.y),
      )
      drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = bubble.bubbleRadius,
        center = Offset(bubble.x, bubble.y),
        style = Stroke(width = 1f),
      )
      drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = bubble.bubbleRadius * 0.2f,
        center = Offset(
          bubble.x - bubble.bubbleRadius * 0.3f,
          bubble.y - bubble.bubbleRadius * 0.3f,
        ),
      )
    }

    for (f in fishes) {
      drawScope.withTransform({
        translate(f.x, f.y)
        if (f.direction == -1) scale(-1f, 1f)
      }) {
        val path = Path().apply {
          moveTo(f.fishSize, 0f)
          quadraticTo(f.fishSize * 0.3f, -f.fishSize * 0.4f, -f.fishSize * 0.5f, 0f)
          quadraticTo(f.fishSize * 0.3f, f.fishSize * 0.4f, f.fishSize, 0f)
          close()
        }
        drawPath(path, f.fishColor.copy(alpha = f.alpha))

        val tailPath = Path().apply {
          moveTo(-f.fishSize * 0.4f, 0f)
          lineTo(-f.fishSize * 0.9f, -f.fishSize * 0.3f)
          lineTo(-f.fishSize * 0.9f, f.fishSize * 0.3f)
          close()
        }
        drawPath(tailPath, f.fishColor.copy(alpha = 0.8f * f.alpha))

        drawCircle(
          color = Color.White.copy(alpha = f.alpha),
          radius = f.fishSize * 0.12f,
          center = Offset(f.fishSize * 0.5f, -f.fishSize * 0.1f),
        )
        drawCircle(
          color = Color.Black.copy(alpha = f.alpha),
          radius = f.fishSize * 0.06f,
          center = Offset(f.fishSize * 0.55f, -f.fishSize * 0.1f),
        )
      }
    }
  }
}

@Composable
fun UnderwaterBackground(modifier: Modifier = Modifier) {
  val system = remember { UnderwaterSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

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
    system.draw(this, totalTime)
  }
}

@Composable
fun UnderwaterFeatureItem(title: String, modifier: Modifier = Modifier) {
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
        .background(Color(0xFF0077B6).copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.size(8.dp)) {
        drawCircle(
          color = Color.White,
          radius = size.minDimension / 2,
          style = Stroke(width = 2f),
        )
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
fun UnderwaterPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  val features = listOf(
    "Dive into Premium Content",
    "Unlimited Deep Sea Access",
    "Exclusive Ocean Discoveries",
    "Ad-free Exploration",
    "Sync Across All Devices",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0077B6),
            Color(0xFF023E8A),
            Color(0xFF03045E),
            Color(0xFF010A1F),
          ),
        ),
      ),
  ) {
    UnderwaterBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "DIVE DEEP",
        style = TextStyle(
          color = Color(0xFF90E0EF),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Ocean Premium",
        style = TextStyle(
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFF90E0EF),
              Color(0xFF00B4D8),
              Color(0xFF0077B6),
            ),
          ),
        ),
      )

      Spacer(modifier = Modifier.weight(0.5f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.3f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          UnderwaterFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.5f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$49.99/year",
          style = TextStyle(
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "7-day free trial",
          style = TextStyle(
            color = Color(0xFF90E0EF),
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
          containerColor = Color(0xFF0077B6),
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = "Start Diving",
          style = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
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
