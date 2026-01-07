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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

data class NightStar(
  val x: Float,
  val y: Float,
  val size: Float,
  val twinklePhase: Float,
  val twinkleSpeed: Float,
)

data class Cloud(val xOffset: Float, val y: Float, val scale: Float, val speed: Float)

@Composable
fun DayNightBackground(cycleProgress: Float, modifier: Modifier = Modifier) {
  val stars = remember {
    List(60) {
      NightStar(
        x = Random.nextFloat(),
        y = Random.nextFloat() * 0.6f,
        size = Random.nextFloat() * 2f + 1f,
        twinklePhase = Random.nextFloat() * PI.toFloat() * 2f,
        twinkleSpeed = Random.nextFloat() * 2f + 1f,
      )
    }
  }

  val clouds = remember {
    List(5) {
      Cloud(
        xOffset = Random.nextFloat(),
        y = Random.nextFloat() * 0.3f + 0.1f,
        scale = Random.nextFloat() * 0.5f + 0.8f,
        speed = Random.nextFloat() * 0.02f + 0.01f,
      )
    }
  }

  var totalTime by remember { mutableFloatStateOf(0f) }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }

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

  val isDay = cycleProgress < 0.5f
  val dayProgress = if (isDay) cycleProgress * 2f else (cycleProgress - 0.5f) * 2f

  val sunriseColors = listOf(Color(0xFFFF8C42), Color(0xFFFFD700), Color(0xFFFFF4E0))
  val dayColors = listOf(Color(0xFF4A90D9), Color(0xFF87CEEB), Color(0xFFB8E0F0))
  val sunsetColors = listOf(Color(0xFFFF6B35), Color(0xFFFFAB5E), Color(0xFFFFD89E))
  val duskColors = listOf(Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFF5D6D7E))
  val nightColors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF2C3E50))
  val dawnColors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF1F4068))

  val skyColors = if (isDay) {
    when {
      dayProgress < 0.2f -> {
        val t = dayProgress / 0.2f
        listOf(
          lerp(sunriseColors[0], dayColors[0], t),
          lerp(sunriseColors[1], dayColors[1], t),
          lerp(sunriseColors[2], dayColors[2], t),
        )
      }
      dayProgress < 0.8f -> dayColors
      else -> {
        val t = (dayProgress - 0.8f) / 0.2f
        listOf(
          lerp(dayColors[0], sunsetColors[0], t),
          lerp(dayColors[1], sunsetColors[1], t),
          lerp(dayColors[2], sunsetColors[2], t),
        )
      }
    }
  } else {
    when {
      dayProgress < 0.2f -> {
        val t = dayProgress / 0.2f
        listOf(
          lerp(sunsetColors[0], duskColors[0], t),
          lerp(sunsetColors[1], duskColors[1], t),
          lerp(sunsetColors[2], duskColors[2], t),
        )
      }
      dayProgress < 0.8f -> nightColors
      else -> {
        val t = (dayProgress - 0.8f) / 0.2f
        listOf(
          lerp(nightColors[0], sunriseColors[0], t),
          lerp(nightColors[1], sunriseColors[1], t),
          lerp(nightColors[2], sunriseColors[2], t),
        )
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height

    drawRect(
      brush = Brush.verticalGradient(colors = skyColors),
      size = size,
    )

    val starAlpha = if (isDay) {
      when {
        dayProgress < 0.15f -> 1f - dayProgress * 6f
        dayProgress > 0.85f -> (dayProgress - 0.85f) * 6f
        else -> 0f
      }
    } else {
      when {
        dayProgress < 0.15f -> dayProgress * 6f
        dayProgress > 0.85f -> 1f - (dayProgress - 0.85f) * 6f
        else -> 1f
      }
    }

    if (starAlpha > 0f) {
      for (star in stars) {
        val twinkle = (sin(totalTime * star.twinkleSpeed + star.twinklePhase) + 1f) / 2f
        val alpha = starAlpha * (0.4f + twinkle * 0.6f)
        drawCircle(
          color = Color.White.copy(alpha = alpha),
          radius = star.size,
          center = Offset(star.x * width, star.y * height),
        )
      }
    }

    val sunMoonY = height * 0.35f
    val arcRadius = width * 0.6f
    val centerX = width / 2f

    if (isDay) {
      val sunAngle = PI.toFloat() * (1f - dayProgress)
      val sunX = centerX + cos(sunAngle) * arcRadius
      val sunY = sunMoonY - sin(sunAngle) * arcRadius * 0.5f + height * 0.1f

      if (sunY < height * 0.7f) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFFFFCC).copy(alpha = 0.4f),
              Color(0xFFFFD700).copy(alpha = 0.2f),
              Color.Transparent,
            ),
            center = Offset(sunX, sunY),
            radius = 80f,
          ),
          radius = 80f,
          center = Offset(sunX, sunY),
        )

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFFFFE0),
              Color(0xFFFFD700),
              Color(0xFFFFA500),
            ),
            center = Offset(sunX - 8f, sunY - 8f),
            radius = 35f,
          ),
          radius = 35f,
          center = Offset(sunX, sunY),
        )

        for (i in 0 until 12) {
          val rayAngle = (i * 30f + totalTime * 20f) * PI.toFloat() / 180f
          val innerRadius = 40f
          val outerRadius = 55f + sin(totalTime * 3f + i) * 5f
          drawLine(
            color = Color(0xFFFFD700).copy(alpha = 0.6f),
            start = Offset(
              sunX + cos(rayAngle) * innerRadius,
              sunY + sin(rayAngle) * innerRadius,
            ),
            end = Offset(
              sunX + cos(rayAngle) * outerRadius,
              sunY + sin(rayAngle) * outerRadius,
            ),
            strokeWidth = 3f,
            cap = StrokeCap.Round,
          )
        }
      }
    } else {
      val moonAngle = PI.toFloat() * (1f - dayProgress)
      val moonX = centerX + cos(moonAngle) * arcRadius
      val moonY = sunMoonY - sin(moonAngle) * arcRadius * 0.5f + height * 0.1f

      if (moonY < height * 0.7f) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFF5F5DC).copy(alpha = 0.3f),
              Color(0xFFE8E8D0).copy(alpha = 0.1f),
              Color.Transparent,
            ),
            center = Offset(moonX, moonY),
            radius = 60f,
          ),
          radius = 60f,
          center = Offset(moonX, moonY),
        )

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFFFFF0),
              Color(0xFFF5F5DC),
              Color(0xFFE8E8D0),
            ),
            center = Offset(moonX - 5f, moonY - 5f),
            radius = 28f,
          ),
          radius = 28f,
          center = Offset(moonX, moonY),
        )

        drawCircle(
          color = Color(0xFFD4D4AA).copy(alpha = 0.3f),
          radius = 6f,
          center = Offset(moonX - 10f, moonY - 5f),
        )
        drawCircle(
          color = Color(0xFFD4D4AA).copy(alpha = 0.2f),
          radius = 4f,
          center = Offset(moonX + 8f, moonY + 8f),
        )
        drawCircle(
          color = Color(0xFFD4D4AA).copy(alpha = 0.25f),
          radius = 3f,
          center = Offset(moonX + 5f, moonY - 12f),
        )
      }
    }

    val cloudAlpha = if (isDay) 0.9f else 0.15f
    val cloudColor = if (isDay) Color.White else Color(0xFF555555)

    for (cloud in clouds) {
      val cloudX = ((cloud.xOffset + totalTime * cloud.speed) % 1.4f - 0.2f) * width
      val cloudY = cloud.y * height

      drawCircle(
        color = cloudColor.copy(alpha = cloudAlpha * 0.8f),
        radius = 25f * cloud.scale,
        center = Offset(cloudX, cloudY),
      )
      drawCircle(
        color = cloudColor.copy(alpha = cloudAlpha),
        radius = 35f * cloud.scale,
        center = Offset(cloudX + 30f * cloud.scale, cloudY - 5f),
      )
      drawCircle(
        color = cloudColor.copy(alpha = cloudAlpha * 0.9f),
        radius = 28f * cloud.scale,
        center = Offset(cloudX + 55f * cloud.scale, cloudY),
      )
      drawCircle(
        color = cloudColor.copy(alpha = cloudAlpha * 0.7f),
        radius = 20f * cloud.scale,
        center = Offset(cloudX + 75f * cloud.scale, cloudY + 5f),
      )
    }

    drawRect(
      brush = Brush.verticalGradient(
        colors = if (isDay) {
          listOf(
            Color.Transparent,
            Color(0xFF228B22).copy(alpha = 0.3f),
            Color(0xFF2E7D32),
          )
        } else {
          listOf(
            Color.Transparent,
            Color(0xFF1B4332).copy(alpha = 0.3f),
            Color(0xFF0D2818),
          )
        },
        startY = height * 0.85f,
        endY = height,
      ),
      topLeft = Offset(0f, height * 0.85f),
      size = androidx.compose.ui.geometry.Size(width, height * 0.15f),
    )
  }
}

@Composable
fun TimeFeatureItem(title: String, checkColor: Color, modifier: Modifier = Modifier) {
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
        .background(checkColor),
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.size(12.dp)) {
        val path = Path().apply {
          moveTo(size.width * 0.2f, size.height * 0.5f)
          lineTo(size.width * 0.4f, size.height * 0.7f)
          lineTo(size.width * 0.8f, size.height * 0.3f)
        }
        drawPath(
          path = path,
          color = Color.White,
          style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
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
fun DayNightPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  val infiniteTransition = rememberInfiniteTransition(label = "daynight")

  val cycleProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(16000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "cycle",
  )

  val isDay = cycleProgress < 0.5f

  val buttonColor by animateColorAsState(
    targetValue = if (isDay) Color(0xFFFFB800) else Color(0xFF6366F1),
    animationSpec = tween(800),
    label = "buttonColor",
  )

  val buttonTextColor by animateColorAsState(
    targetValue = if (isDay) Color.Black else Color.White,
    animationSpec = tween(800),
    label = "buttonTextColor",
  )

  val accentColor by animateColorAsState(
    targetValue = if (isDay) Color(0xFFFFD700) else Color(0xFF8B9DC3),
    animationSpec = tween(800),
    label = "accentColor",
  )

  val checkColor by animateColorAsState(
    targetValue = if (isDay) Color(0xFFFFB800) else Color(0xFF6366F1),
    animationSpec = tween(800),
    label = "checkColor",
  )

  val features = listOf(
    "24/7 Access Anytime",
    "Sync Across All Devices",
    "Offline Mode Support",
    "Smart Scheduling",
    "Priority Notifications",
  )

  Box(modifier = Modifier.fillMaxSize()) {
    DayNightBackground(cycleProgress = cycleProgress)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Crossfade(
        targetState = isDay,
        animationSpec = tween(800),
        label = "greeting",
      ) { day ->
        Text(
          text = if (day) "GOOD MORNING" else "GOOD EVENING",
          style = TextStyle(
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
          ),
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Always On",
        style = TextStyle(
          color = Color.White,
          fontSize = 38.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      Text(
        text = "Premium works day and night",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 14.sp,
        ),
      )

      Spacer(modifier = Modifier.weight(0.6f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.3f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          TimeFeatureItem(
            title = feature,
            checkColor = checkColor,
          )
        }
      }

      Spacer(modifier = Modifier.weight(0.4f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$39.99/year",
          style = TextStyle(
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "Less than $3.50/month",
          style = TextStyle(
            color = accentColor,
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
          containerColor = buttonColor,
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = "Start Free Trial",
          style = TextStyle(
            color = buttonTextColor,
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
