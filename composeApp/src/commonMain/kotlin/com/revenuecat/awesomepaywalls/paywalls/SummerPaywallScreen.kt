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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class SunRay(val angle: Float, val length: Float, val width: Float, val alpha: Float)

data class FloatingParticle(
  var x: Float,
  var y: Float,
  var size: Float,
  var alpha: Float,
  var speed: Float,
  var wobblePhase: Float,
)

class SummerBackgroundSystem {
  private val particles = mutableListOf<FloatingParticle>()
  private val random = Random
  private val maxParticles = 30

  val sunRays = List(12) { i ->
    SunRay(
      angle = i * 30f,
      length = random.nextFloat() * 100f + 150f,
      width = random.nextFloat() * 20f + 10f,
      alpha = random.nextFloat() * 0.3f + 0.1f,
    )
  }

  fun update(width: Float, height: Float, deltaTime: Float, totalTime: Float) {
    if (particles.size < maxParticles && random.nextFloat() < 0.1f) {
      particles.add(
        FloatingParticle(
          x = random.nextFloat() * width,
          y = height + 10f,
          size = random.nextFloat() * 4f + 2f,
          alpha = random.nextFloat() * 0.4f + 0.2f,
          speed = random.nextFloat() * 30f + 20f,
          wobblePhase = random.nextFloat() * PI.toFloat() * 2f,
        ),
      )
    }

    val iterator = particles.iterator()
    while (iterator.hasNext()) {
      val p = iterator.next()
      p.y -= p.speed * deltaTime
      p.x += sin(totalTime * 2f + p.wobblePhase) * 20f * deltaTime

      if (p.y < -20f) {
        iterator.remove()
      }
    }
  }

  fun drawParticles(drawScope: DrawScope) {
    for (p in particles) {
      drawScope.drawCircle(
        color = Color(0xFFFFE4B5).copy(alpha = p.alpha),
        radius = p.size,
        center = Offset(p.x, p.y),
      )
    }
  }
}

@Composable
fun SummerBackground(modifier: Modifier = Modifier) {
  val system = remember { SummerBackgroundSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

  val infiniteTransition = rememberInfiniteTransition(label = "summer")

  val sunRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(60000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "sunRotation",
  )

  val waveOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "waveOffset",
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
          system.update(canvasSize.width, canvasSize.height, deltaTime, totalTime)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    val sunCenterX = size.width * 0.8f
    val sunCenterY = size.height * 0.15f

    // Sun glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFD700).copy(alpha = 0.4f),
          Color(0xFFFFA500).copy(alpha = 0.2f),
          Color.Transparent,
        ),
        center = Offset(sunCenterX, sunCenterY),
        radius = 200f,
      ),
      radius = 200f,
      center = Offset(sunCenterX, sunCenterY),
    )

    // Sun rays
    for (ray in system.sunRays) {
      val angleRad = (ray.angle + sunRotation) * PI.toFloat() / 180f
      val startX = sunCenterX + cos(angleRad) * 50f
      val startY = sunCenterY + sin(angleRad) * 50f
      val endX = sunCenterX + cos(angleRad) * ray.length
      val endY = sunCenterY + sin(angleRad) * ray.length

      drawLine(
        color = Color(0xFFFFD700).copy(alpha = ray.alpha),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = ray.width,
        cap = StrokeCap.Round,
      )
    }

    // Sun core
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFFFE0),
          Color(0xFFFFD700),
          Color(0xFFFFA500),
        ),
        center = Offset(sunCenterX, sunCenterY),
        radius = 50f,
      ),
      radius = 50f,
      center = Offset(sunCenterX, sunCenterY),
    )

    // Ocean waves at bottom
    val waveHeight = size.height * 0.12f
    val waveY = size.height - waveHeight

    for (layer in 0..2) {
      val layerOffset = layer * 0.5f
      val layerAlpha = 0.3f - layer * 0.08f
      val path = Path().apply {
        moveTo(0f, size.height)
        lineTo(0f, waveY + layer * 15f)

        var x = 0f
        while (x <= size.width) {
          val y = waveY + layer * 15f +
            sin(x * 0.02f + waveOffset + layerOffset) * 15f +
            sin(x * 0.01f + waveOffset * 0.7f + layerOffset) * 10f
          lineTo(x, y)
          x += 5f
        }

        lineTo(size.width, size.height)
        close()
      }

      drawPath(
        path = path,
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0077B6).copy(alpha = layerAlpha + 0.2f),
            Color(0xFF023E8A).copy(alpha = layerAlpha),
          ),
          startY = waveY,
          endY = size.height,
        ),
      )
    }

    system.drawParticles(this)
  }
}

@Composable
fun SummerTitle(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "title")

  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "SUMMER SALE",
      style = TextStyle(
        color = Color(0xFFFFD700),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 4.sp,
      ),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Hot Deals",
      style = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFFFF6B35),
            Color(0xFFFFD700),
            Color(0xFFFF6B35),
          ),
        ),
      ),
      modifier = Modifier.scale(scale),
    )

    Text(
      text = "Limited Time Offer",
      style = TextStyle(
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun SummerCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "card")

  val borderAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "borderAlpha",
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1A1A2E).copy(alpha = 0.9f),
            Color(0xFF16213E).copy(alpha = 0.95f),
          ),
        ),
      )
      .border(
        width = 2.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFFFF6B35).copy(alpha = borderAlpha),
            Color(0xFFFFD700).copy(alpha = borderAlpha),
            Color(0xFF0077B6).copy(alpha = borderAlpha),
          ),
        ),
        shape = RoundedCornerShape(20.dp),
      )
      .padding(20.dp),
  ) {
    content()
  }
}

@Composable
fun SummerFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Canvas(modifier = Modifier.size(8.dp)) {
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
        ),
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
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
fun SummerPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF87CEEB),
            Color(0xFF4A90A4),
            Color(0xFF2C5364),
          ),
        ),
      ),
    contentAlignment = Alignment.Center,
  ) {
    SummerBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      SummerTitle()

      Spacer(modifier = Modifier.height(32.dp))

      SummerCard(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(
            text = "Premium Features",
            style = TextStyle(
              color = Color(0xFFFFD700),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
            ),
          )

          Spacer(modifier = Modifier.height(16.dp))

          SummerFeatureItem(title = "Unlimited Access to All Content")
          SummerFeatureItem(title = "Exclusive Summer Themes")
          SummerFeatureItem(title = "Offline Mode")
          SummerFeatureItem(title = "Priority Customer Support")
          SummerFeatureItem(title = "Ad-Free Experience")
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "SUMMER SPECIAL",
          style = TextStyle(
            color = Color(0xFFFFD700),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
          ),
        )
        Text(
          text = "50% OFF",
          style = TextStyle(
            color = Color(0xFFFF6B35),
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
          ),
        )
        Text(
          text = "Ends Soon",
          style = TextStyle(
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
          ),
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Box(modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = { paywallState.purchase(PackageType.ANNUAL) },
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35),
          ),
          shape = RoundedCornerShape(14.dp),
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Yearly — \$29.99/year",
              style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Save 50% • \$2.49/month",
              style = TextStyle(
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
              ),
            )
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 8.dp, y = (-10).dp)
            .background(
              color = Color(0xFFFFD700),
              shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
          Text(
            text = "BEST VALUE",
            style = TextStyle(
              color = Color.Black,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
            ),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = { paywallState.purchase(PackageType.MONTHLY) },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF0077B6),
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = "Monthly — \$4.99/month",
          style = TextStyle(
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 14.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Cancel anytime. Terms apply.",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.5f),
          fontSize = 12.sp,
        ),
        textAlign = TextAlign.Center,
      )
    }
  }
}
