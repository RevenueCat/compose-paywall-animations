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

data class Firefly(
  var x: Float,
  var y: Float,
  val size: Float,
  val glowPhase: Float,
  val glowSpeed: Float,
  var directionX: Float,
  var directionY: Float,
  val moveSpeed: Float,
  val color: Color,
)

data class GrassBlade(val x: Float, val height: Float, val swayPhase: Float, val width: Float)

class FirefliesSystem {
  private val fireflies = mutableListOf<Firefly>()
  private val stars = mutableListOf<Offset>()
  private val grassBlades = mutableListOf<GrassBlade>()
  private val random = Random
  private var initialized = false

  private val fireflyColors = listOf(
    Color(0xFFFFE066),
    Color(0xFFBBFF57),
    Color(0xFFAAFF77),
    Color(0xFFFFDD44),
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    repeat(20) {
      fireflies.add(createFirefly(width, height))
    }

    repeat(50) {
      stars.add(Offset(random.nextFloat() * width, random.nextFloat() * height * 0.6f))
    }

    repeat(60) {
      grassBlades.add(
        GrassBlade(
          x = random.nextFloat() * width,
          height = random.nextFloat() * 80f + 40f,
          swayPhase = random.nextFloat() * PI.toFloat() * 2f,
          width = random.nextFloat() * 3f + 1f,
        ),
      )
    }
  }

  private fun createFirefly(width: Float, height: Float): Firefly {
    val angle = random.nextFloat() * PI.toFloat() * 2f
    return Firefly(
      x = random.nextFloat() * width,
      y = random.nextFloat() * height * 0.7f + height * 0.1f,
      size = random.nextFloat() * 4f + 3f,
      glowPhase = random.nextFloat() * PI.toFloat() * 2f,
      glowSpeed = random.nextFloat() * 2f + 1f,
      directionX = cos(angle),
      directionY = sin(angle),
      moveSpeed = random.nextFloat() * 20f + 10f,
      color = fireflyColors[random.nextInt(fireflyColors.size)],
    )
  }

  fun update(deltaTime: Float, width: Float, height: Float) {
    for (firefly in fireflies) {
      firefly.x += firefly.directionX * firefly.moveSpeed * deltaTime
      firefly.y += firefly.directionY * firefly.moveSpeed * deltaTime

      // Bounce off edges with some padding
      val padding = 50f
      if (firefly.x < padding || firefly.x > width - padding) {
        firefly.directionX *= -1
        firefly.x = firefly.x.coerceIn(padding, width - padding)
      }
      if (firefly.y < padding || firefly.y > height * 0.75f) {
        firefly.directionY *= -1
        firefly.y = firefly.y.coerceIn(padding, height * 0.75f)
      }

      // Random direction changes
      if (random.nextFloat() < 0.01f) {
        val angle = random.nextFloat() * PI.toFloat() * 2f
        firefly.directionX = cos(angle)
        firefly.directionY = sin(angle)
      }
    }
  }

  fun draw(drawScope: DrawScope, totalTime: Float) {
    val width = drawScope.size.width
    val height = drawScope.size.height

    // Draw stars
    for (star in stars) {
      val twinkle = (sin(totalTime * 2f + star.x * 0.1f) + 1f) / 2f
      drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.3f + twinkle * 0.4f),
        radius = 1f + twinkle * 0.5f,
        center = star,
      )
    }

    // Draw moon
    val moonX = width * 0.8f
    val moonY = height * 0.12f
    val moonRadius = 35f

    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFFAE6).copy(alpha = 0.15f),
          Color.Transparent,
        ),
        center = Offset(moonX, moonY),
        radius = moonRadius * 3f,
      ),
      radius = moonRadius * 3f,
      center = Offset(moonX, moonY),
    )

    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFFFAE6),
          Color(0xFFE8E0C0),
        ),
        center = Offset(moonX - moonRadius * 0.2f, moonY - moonRadius * 0.2f),
        radius = moonRadius,
      ),
      radius = moonRadius,
      center = Offset(moonX, moonY),
    )

    // Draw grass
    for (blade in grassBlades) {
      val sway = sin(totalTime * 1.5f + blade.swayPhase) * 8f
      val grassColor = Color(0xFF1A3D1A)

      val path = Path().apply {
        moveTo(blade.x, height)
        quadraticTo(
          blade.x + sway * 0.5f,
          height - blade.height * 0.5f,
          blade.x + sway,
          height - blade.height,
        )
        quadraticTo(
          blade.x + sway * 0.5f + blade.width,
          height - blade.height * 0.5f,
          blade.x + blade.width,
          height,
        )
        close()
      }
      drawScope.drawPath(path, grassColor)
    }

    // Draw fireflies
    for (firefly in fireflies) {
      val glow = (sin(totalTime * firefly.glowSpeed + firefly.glowPhase) + 1f) / 2f
      val alpha = 0.3f + glow * 0.7f

      // Outer glow
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            firefly.color.copy(alpha = alpha * 0.4f),
            firefly.color.copy(alpha = alpha * 0.1f),
            Color.Transparent,
          ),
          center = Offset(firefly.x, firefly.y),
          radius = firefly.size * 6f,
        ),
        radius = firefly.size * 6f,
        center = Offset(firefly.x, firefly.y),
      )

      // Inner glow
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White.copy(alpha = alpha),
            firefly.color.copy(alpha = alpha),
            Color.Transparent,
          ),
          center = Offset(firefly.x, firefly.y),
          radius = firefly.size * 2f,
        ),
        radius = firefly.size * 2f,
        center = Offset(firefly.x, firefly.y),
      )

      // Core
      drawScope.drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = firefly.size * 0.5f,
        center = Offset(firefly.x, firefly.y),
      )
    }
  }
}

@Composable
fun FirefliesBackground(modifier: Modifier = Modifier) {
  val system = remember { FirefliesSystem() }
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
          system.update(deltaTime, canvasSize.width, canvasSize.height)
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
fun FirefliesFeatureItem(title: String, modifier: Modifier = Modifier) {
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
        .background(Color(0xFFBBFF57).copy(alpha = 0.3f)),
      contentAlignment = Alignment.Center,
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(Color(0xFFBBFF57)),
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color(0xFFE8E0C0),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun PlanOption(
  title: String,
  price: String,
  period: String,
  badge: String? = null,
  savings: String? = null,
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
          Color(0xFF2D5016).copy(alpha = 0.6f)
        } else {
          Color(0xFF0A1628).copy(alpha = 0.5f)
        },
      )
      .then(
        if (isSelected) {
          Modifier.background(
            brush = Brush.linearGradient(
              colors = listOf(
                Color(0xFFBBFF57).copy(alpha = 0.1f),
                Color.Transparent,
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
      // Checkbox on the left, vertically centered
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(
            if (isSelected) Color(0xFFBBFF57) else Color(0xFFE8E0C0).copy(alpha = 0.3f),
          ),
        contentAlignment = Alignment.Center,
      ) {
        if (isSelected) {
          Text(
            text = "✓",
            style = TextStyle(
              color = Color(0xFF0A1628),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            style = TextStyle(
              color = Color(0xFFE8E0C0),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
          if (badge != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFBBFF57)),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = badge,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = TextStyle(
                  color = Color(0xFF0A1628),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                ),
              )
            }
          }
        }
        if (savings != null) {
          Text(
            text = savings,
            style = TextStyle(
              color = Color(0xFFBBFF57),
              fontSize = 12.sp,
            ),
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = price,
          style = TextStyle(
            color = Color(0xFFE8E0C0),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = period,
          style = TextStyle(
            color = Color(0xFFE8E0C0).copy(alpha = 0.6f),
            fontSize = 12.sp,
          ),
        )
      }
    }
  }
}

@Composable
fun FirefliesPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  var selectedPlan by remember { mutableStateOf("yearly") }

  val features = listOf(
    "Unlimited Night Mode",
    "Magical Theme Collection",
    "Ad-free Serenity",
    "Ambient Sound Library",
    "Sync Across Devices",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0A1628),
            Color(0xFF162447),
            Color(0xFF1F4068),
            Color(0xFF0A1628),
          ),
        ),
      ),
  ) {
    FirefliesBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "ENCHANTED",
        style = TextStyle(
          color = Color(0xFFBBFF57).copy(alpha = 0.8f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Night Garden",
        style = TextStyle(
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFE8E0C0),
              Color(0xFFBBFF57),
              Color(0xFFE8E0C0),
            ),
          ),
        ),
      )

      Spacer(modifier = Modifier.weight(0.3f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0A1628).copy(alpha = 0.7f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          FirefliesFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.3f))

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        PlanOption(
          title = "Yearly",
          price = "$29.99",
          period = "per year",
          badge = "BEST VALUE",
          savings = "Save 58% vs monthly",
          isSelected = selectedPlan == "yearly",
          onClick = { selectedPlan = "yearly" },
        )

        PlanOption(
          title = "Monthly",
          price = "$5.99",
          period = "per month",
          isSelected = selectedPlan == "monthly",
          onClick = { selectedPlan = "monthly" },
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (selectedPlan ==
          "yearly"
        ) {
          "7-day free trial included"
        } else {
          "3-day free trial included"
        },
        style = TextStyle(
          color = Color(0xFFBBFF57),
          fontSize = 13.sp,
        ),
      )

      Spacer(modifier = Modifier.weight(0.2f))

      Button(
        onClick = {
          val packageType = if (selectedPlan ==
            "yearly"
          ) {
            PackageType.ANNUAL
          } else {
            PackageType.MONTHLY
          }
          paywallState.purchase(packageType)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF2D5016),
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = if (selectedPlan == "yearly") "Start Free Trial" else "Subscribe Now",
          style = TextStyle(
            color = Color(0xFFBBFF57),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color(0xFFE8E0C0).copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
