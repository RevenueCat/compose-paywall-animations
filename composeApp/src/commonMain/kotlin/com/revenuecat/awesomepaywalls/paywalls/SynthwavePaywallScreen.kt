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

@Composable
fun SynthwaveBackground(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "synthwave")

  val gridScroll by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "gridScroll",
  )

  val sunPulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "sunPulse",
  )

  val glowIntensity by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glow",
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val width = size.width
    val height = size.height
    val horizonY = height * 0.55f

    drawRect(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFF0D0221),
          Color(0xFF1A0533),
          Color(0xFF2D1B4E),
        ),
        startY = 0f,
        endY = horizonY,
      ),
      size = androidx.compose.ui.geometry.Size(width, horizonY),
    )

    val sunRadius = width * 0.18f * sunPulse
    val sunCenterY = horizonY - sunRadius * 0.3f

    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFF6EC7).copy(alpha = 0.4f * glowIntensity),
          Color.Transparent,
        ),
        center = Offset(width / 2, sunCenterY),
        radius = sunRadius * 2f,
      ),
      radius = sunRadius * 2f,
      center = Offset(width / 2, sunCenterY),
    )

    val sunStripes = 8
    for (i in 0 until sunStripes) {
      val stripeY = sunCenterY - sunRadius + (i * sunRadius * 2 / sunStripes)
      val stripeHeight = sunRadius * 2 / sunStripes / 2

      if (stripeY > sunCenterY) {
        val halfWidth = sqrt(
          sunRadius * sunRadius - (stripeY - sunCenterY) * (stripeY - sunCenterY),
        )
        if (halfWidth > 0) {
          drawRect(
            color = Color(0xFF0D0221),
            topLeft = Offset(width / 2 - halfWidth, stripeY),
            size = androidx.compose.ui.geometry.Size(
              halfWidth * 2,
              stripeHeight.coerceAtMost(
                sunCenterY + sunRadius - stripeY,
              ),
            ),
          )
        }
      }
    }

    drawCircle(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFFFF6EC7),
          Color(0xFFFF9E00),
          Color(0xFFFFE600),
        ),
        startY = sunCenterY - sunRadius,
        endY = sunCenterY + sunRadius,
      ),
      radius = sunRadius,
      center = Offset(width / 2, sunCenterY),
      blendMode = BlendMode.SrcAtop,
    )

    drawCircle(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFFFF6EC7),
          Color(0xFFFF9E00),
          Color(0xFFFFE600),
        ),
        startY = sunCenterY - sunRadius,
        endY = sunCenterY + sunRadius,
      ),
      radius = sunRadius,
      center = Offset(width / 2, sunCenterY),
    )

    for (i in 1..sunStripes) {
      val stripeY = sunCenterY + (i * sunRadius / sunStripes) * 0.8f
      val alpha = (i.toFloat() / sunStripes) * 0.8f

      val dist = stripeY - sunCenterY
      if (dist < sunRadius) {
        val halfWidth = sqrt(sunRadius * sunRadius - dist * dist)
        drawLine(
          color = Color(0xFF0D0221).copy(alpha = alpha),
          start = Offset(width / 2 - halfWidth, stripeY),
          end = Offset(width / 2 + halfWidth, stripeY),
          strokeWidth = 4f - i * 0.3f,
        )
      }
    }

    drawRect(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFF2D1B4E),
          Color(0xFF1A0533),
          Color(0xFF0D0221),
        ),
        startY = horizonY,
        endY = height,
      ),
      topLeft = Offset(0f, horizonY),
      size = androidx.compose.ui.geometry.Size(width, height - horizonY),
    )

    val gridColor = Color(0xFFFF6EC7).copy(alpha = 0.6f * glowIntensity)
    val gridLines = 15
    val gridSpacing = width / gridLines

    for (i in 0..gridLines) {
      val x = i * gridSpacing
      val perspectiveX1 = width / 2 + (x - width / 2) * 0.1f
      val perspectiveX2 = x

      drawLine(
        color = gridColor,
        start = Offset(perspectiveX1, horizonY),
        end = Offset(perspectiveX2, height),
        strokeWidth = 1.5f,
      )
    }

    val horizontalLines = 12
    for (i in 0..horizontalLines) {
      val progress = (i.toFloat() / horizontalLines + gridScroll) % 1f
      val y = horizonY + (height - horizonY) * progress.pow(1.5f)
      val alpha = progress * 0.8f

      drawLine(
        color = gridColor.copy(alpha = alpha * glowIntensity),
        start = Offset(0f, y),
        end = Offset(width, y),
        strokeWidth = 1.5f,
      )
    }

    drawLine(
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color.Transparent,
          Color(0xFF00FFFF).copy(alpha = glowIntensity),
          Color(0xFFFF6EC7).copy(alpha = glowIntensity),
          Color.Transparent,
        ),
      ),
      start = Offset(0f, horizonY),
      end = Offset(width, horizonY),
      strokeWidth = 3f,
    )
  }
}

@Composable
fun NeonFeatureItem(title: String, modifier: Modifier = Modifier) {
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
        .background(Color(0xFFFF6EC7).copy(alpha = 0.3f)),
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.size(10.dp)) {
        drawLine(
          color = Color(0xFFFF6EC7),
          start = Offset(0f, size.height / 2),
          end = Offset(size.width, size.height / 2),
          strokeWidth = 2f,
          cap = StrokeCap.Round,
        )
        drawLine(
          color = Color(0xFFFF6EC7),
          start = Offset(size.width / 2, 0f),
          end = Offset(size.width / 2, size.height),
          strokeWidth = 2f,
          cap = StrokeCap.Round,
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
fun SynthwavePaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  val infiniteTransition = rememberInfiniteTransition(label = "neon")
  val textGlow by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "textGlow",
  )

  val features = listOf(
    "Unlimited Retro Access",
    "Neon Theme Collection",
    "Exclusive Synthwave Tracks",
    "Ad-free Experience",
    "Cross-platform Sync",
  )

  Box(modifier = Modifier.fillMaxSize()) {
    SynthwaveBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = "WELCOME TO",
        style = TextStyle(
          color = Color(0xFF00FFFF).copy(alpha = textGlow),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 6.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "RETRO PRO",
        style = TextStyle(
          fontSize = 42.sp,
          fontWeight = FontWeight.ExtraBold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFFF6EC7).copy(alpha = textGlow),
              Color(0xFF00FFFF).copy(alpha = textGlow),
            ),
          ),
        ),
      )

      Spacer(modifier = Modifier.weight(0.6f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.Black.copy(alpha = 0.5f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          NeonFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.4f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$69.99/year",
          style = TextStyle(
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "3-day free trial",
          style = TextStyle(
            color = Color(0xFFFF6EC7),
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
              brush = Brush.horizontalGradient(
                colors = listOf(
                  Color(0xFFFF6EC7),
                  Color(0xFF00FFFF),
                ),
              ),
              shape = RoundedCornerShape(14.dp),
            ),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "ACTIVATE PRO",
            style = TextStyle(
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp,
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
