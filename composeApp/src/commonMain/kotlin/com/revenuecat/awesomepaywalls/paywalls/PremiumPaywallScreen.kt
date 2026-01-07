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
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

data class GlowOrb(
  var x: Float,
  var y: Float,
  var radius: Float,
  var color: Color,
  var speedX: Float,
  var speedY: Float,
  var alpha: Float,
)

class PremiumBackgroundSystem {
  private val orbs = mutableListOf<GlowOrb>()
  private val random = Random
  private var initialized = false

  private val colors = listOf(
    Color(0xFF6366F1),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899),
    Color(0xFF06B6D4),
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    repeat(6) {
      orbs.add(
        GlowOrb(
          x = random.nextFloat() * width,
          y = random.nextFloat() * height,
          radius = random.nextFloat() * 150f + 100f,
          color = colors[random.nextInt(colors.size)],
          speedX = (random.nextFloat() - 0.5f) * 30f,
          speedY = (random.nextFloat() - 0.5f) * 30f,
          alpha = random.nextFloat() * 0.3f + 0.1f,
        ),
      )
    }
  }

  fun update(width: Float, height: Float, deltaTime: Float) {
    for (orb in orbs) {
      orb.x += orb.speedX * deltaTime
      orb.y += orb.speedY * deltaTime

      if (orb.x < -orb.radius) orb.x = width + orb.radius
      if (orb.x > width + orb.radius) orb.x = -orb.radius
      if (orb.y < -orb.radius) orb.y = height + orb.radius
      if (orb.y > height + orb.radius) orb.y = -orb.radius
    }
  }

  fun draw(drawScope: DrawScope) {
    for (orb in orbs) {
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            orb.color.copy(alpha = orb.alpha),
            orb.color.copy(alpha = orb.alpha * 0.5f),
            Color.Transparent,
          ),
          center = Offset(orb.x, orb.y),
          radius = orb.radius,
        ),
        radius = orb.radius,
        center = Offset(orb.x, orb.y),
        blendMode = BlendMode.Plus,
      )
    }
  }
}

@Composable
fun PremiumBackground(modifier: Modifier = Modifier) {
  val system = remember { PremiumBackgroundSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
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

        if (canvasSize.width > 0 && canvasSize.height > 0) {
          system.initialize(canvasSize.width, canvasSize.height)
          system.update(canvasSize.width, canvasSize.height, deltaTime)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    system.draw(this)
  }
}

@Composable
fun PremiumBadge(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "badge")

  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotation",
  )

  Box(
    modifier = modifier.size(100.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2

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
        radius = radius,
        center = center,
        style = Stroke(width = 4f),
      )

      for (i in 0 until 8) {
        val angle = (i * 45f + rotation) * PI.toFloat() / 180f
        val dotRadius = 4f
        val dotX = center.x + cos(angle) * (radius - 8f)
        val dotY = center.y + sin(angle) * (radius - 8f)

        drawCircle(
          color = Color.White,
          radius = dotRadius,
          center = Offset(dotX, dotY),
        )
      }
    }

    Box(
      modifier = Modifier
        .size(70.dp)
        .clip(CircleShape)
        .background(
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFF6366F1),
              Color(0xFF8B5CF6),
            ),
          ),
        ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "PRO",
        style = TextStyle(
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.ExtraBold,
        ),
      )
    }
  }
}

@Composable
fun FeatureCheckItem(title: String, description: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 10.dp),
    verticalAlignment = Alignment.Top,
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
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
          color = Color(0xFF6366F1),
          style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column {
      Text(
        text = title,
        style = TextStyle(
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
        ),
      )
      Text(
        text = description,
        style = TextStyle(
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 13.sp,
        ),
      )
    }
  }
}

@Composable
fun PricingToggle(isYearly: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "toggle")

  val borderAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.6f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "borderAlpha",
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White.copy(alpha = 0.05f))
      .border(
        width = 1.dp,
        color = Color(0xFF6366F1).copy(alpha = borderAlpha),
        shape = RoundedCornerShape(12.dp),
      )
      .padding(4.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(10.dp))
        .clickable { onToggle(false) }
        .background(if (!isYearly) Color(0xFF6366F1) else Color.Transparent)
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "Monthly",
        style = TextStyle(
          color = if (!isYearly) Color.White else Color.White.copy(alpha = 0.6f),
          fontSize = 14.sp,
          fontWeight = if (!isYearly) FontWeight.Bold else FontWeight.Normal,
        ),
      )
    }

    Box(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(10.dp))
        .clickable { onToggle(true) }
        .background(if (isYearly) Color(0xFF6366F1) else Color.Transparent)
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Yearly",
          style = TextStyle(
            color = if (isYearly) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = if (isYearly) FontWeight.Bold else FontWeight.Normal,
          ),
        )
        if (isYearly) {
          Text(
            text = "Save 40%",
            style = TextStyle(
              color = Color(0xFF22C55E),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }
    }
  }
}

@Composable
fun PremiumPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  var isYearly by remember { mutableStateOf(true) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F0F1A),
            Color(0xFF1A1A2E),
            Color(0xFF0F0F1A),
          ),
        ),
      ),
    contentAlignment = Alignment.Center,
  ) {
    PremiumBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      PremiumBadge()

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Upgrade to Premium",
        style = TextStyle(
          color = Color.White,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      Text(
        text = "Unlock all features and take your experience to the next level",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 14.sp,
          textAlign = TextAlign.Center,
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      )

      Spacer(modifier = Modifier.height(24.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.05f))
          .padding(16.dp),
      ) {
        FeatureCheckItem(
          title = "Unlimited Cloud Storage",
          description = "Store and sync all your data across devices",
        )
        FeatureCheckItem(
          title = "Advanced Analytics",
          description = "Get detailed insights and performance metrics",
        )
        FeatureCheckItem(
          title = "Priority Support",
          description = "24/7 dedicated support with faster response times",
        )
        FeatureCheckItem(
          title = "Custom Integrations",
          description = "Connect with your favorite tools and services",
        )
        FeatureCheckItem(
          title = "No Advertisements",
          description = "Enjoy a clean, distraction-free experience",
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      PricingToggle(
        isYearly = isYearly,
        onToggle = { isYearly = it },
      )

      Spacer(modifier = Modifier.height(20.dp))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(
          verticalAlignment = Alignment.Bottom,
        ) {
          Text(
            text = if (isYearly) "\$59.99" else "\$9.99",
            style = TextStyle(
              color = Color.White,
              fontSize = 40.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
          Text(
            text = if (isYearly) "/year" else "/month",
            style = TextStyle(
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 16.sp,
              fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp),
          )
        }

        if (isYearly) {
          Row {
            Text(
              text = "\$119.88",
              style = TextStyle(
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp,
                textDecoration = TextDecoration.LineThrough,
              ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "(\$4.99/month)",
              style = TextStyle(
                color = Color(0xFF22C55E),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
              ),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          paywallState.purchase(if (isYearly) PackageType.ANNUAL else PackageType.MONTHLY)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF6366F1),
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = "Start Premium",
          style = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "7-day free trial • Cancel anytime",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.5f),
          fontSize = 12.sp,
        ),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color(0xFF6366F1),
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
