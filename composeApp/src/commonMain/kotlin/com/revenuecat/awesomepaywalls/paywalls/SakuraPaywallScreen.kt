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

data class SakuraPetal(
  var x: Float,
  var y: Float,
  val rotation: Float,
  val size: Float,
  val alpha: Float,
  val fallSpeed: Float,
  val driftSpeed: Float,
)

class SakuraSystem {
  private val petals = mutableListOf<SakuraPetal>()
  private val random = Random
  private var initialized = false

  private val petalColors = listOf(
    Color(0xFFFFB7C5),
    Color(0xFFFFC0CB),
    Color(0xFFFFDADE),
    Color(0xFFFFE4E8),
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    repeat(40) {
      petals.add(createPetal(width, random.nextFloat() * height))
    }
  }

  private fun createPetal(width: Float, startY: Float = -50f): SakuraPetal = SakuraPetal(
    x = random.nextFloat() * width,
    y = startY,
    rotation = random.nextFloat() * 40f - 20f,
    size = random.nextFloat() * 10f + 6f,
    alpha = random.nextFloat() * 0.3f + 0.5f,
    fallSpeed = random.nextFloat() * 80f + 80f,
    driftSpeed = (random.nextFloat() - 0.5f) * 20f,
  )

  fun update(deltaTime: Float, width: Float, height: Float) {
    val iterator = petals.iterator()
    while (iterator.hasNext()) {
      val petal = iterator.next()
      petal.y += petal.fallSpeed * deltaTime
      petal.x += petal.driftSpeed * deltaTime

      if (petal.y > height + 50f) {
        iterator.remove()
      }
    }

    if (petals.size < 50 && random.nextFloat() < 0.08f) {
      petals.add(createPetal(width))
    }
  }

  fun draw(drawScope: DrawScope) {
    for (petal in petals) {
      drawScope.withTransform({
        translate(petal.x, petal.y)
        rotate(petal.rotation)
      }) {
        val color = petalColors[(petal.hashCode().absoluteValue) % petalColors.size]

        val path = Path().apply {
          moveTo(0f, -petal.size)
          quadraticTo(petal.size * 0.8f, -petal.size * 0.3f, 0f, petal.size * 0.5f)
          quadraticTo(-petal.size * 0.8f, -petal.size * 0.3f, 0f, -petal.size)
          close()
        }
        drawPath(
          path = path,
          color = color.copy(alpha = petal.alpha),
        )

        drawLine(
          color = Color(0xFFE891A0).copy(alpha = petal.alpha * 0.5f),
          start = Offset(0f, -petal.size * 0.8f),
          end = Offset(0f, petal.size * 0.3f),
          strokeWidth = 1f,
        )
      }
    }
  }
}

@Composable
fun SakuraBackground(modifier: Modifier = Modifier) {
  val system = remember { SakuraSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

  val infiniteTransition = rememberInfiniteTransition(label = "sakura")
  val branchSway by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "branchSway",
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
          system.update(deltaTime, canvasSize.width, canvasSize.height)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    val width = size.width
    val height = size.height

    drawBranch(
      startX = -20f,
      startY = height * 0.15f,
      angle = -15f + branchSway,
      length = width * 0.5f,
      thickness = 8f,
      depth = 0,
    )

    drawBranch(
      startX = width + 20f,
      startY = height * 0.08f,
      angle = 195f - branchSway,
      length = width * 0.4f,
      thickness = 6f,
      depth = 0,
    )

    system.draw(this)
  }
}

private fun DrawScope.drawBranch(
  startX: Float,
  startY: Float,
  angle: Float,
  length: Float,
  thickness: Float,
  depth: Int,
) {
  if (depth > 3 || length < 20f) return

  val angleRad = angle * PI.toFloat() / 180f
  val endX = startX + cos(angleRad) * length
  val endY = startY + sin(angleRad) * length

  drawLine(
    color = Color(0xFF5D3A29),
    start = Offset(startX, startY),
    end = Offset(endX, endY),
    strokeWidth = thickness,
    cap = StrokeCap.Round,
  )

  if (depth < 2) {
    drawBranch(
      startX = endX,
      startY = endY,
      angle = angle - 25f,
      length = length * 0.7f,
      thickness = thickness * 0.7f,
      depth = depth + 1,
    )
    drawBranch(
      startX = endX,
      startY = endY,
      angle = angle + 20f,
      length = length * 0.6f,
      thickness = thickness * 0.6f,
      depth = depth + 1,
    )
  }

  val blossomPositions = listOf(0.4f, 0.6f, 0.8f, 1f)
  for (pos in blossomPositions) {
    val bx = startX + (endX - startX) * pos
    val by = startY + (endY - startY) * pos

    for (i in 0 until 5) {
      val petalAngle = i * 72f * PI.toFloat() / 180f
      val petalDist = 8f + depth * 2f

      drawCircle(
        color = Color(0xFFFFB7C5).copy(alpha = 0.7f),
        radius = 6f - depth,
        center = Offset(
          bx + cos(petalAngle) * petalDist,
          by + sin(petalAngle) * petalDist,
        ),
      )
    }

    drawCircle(
      color = Color(0xFFFFE4B5),
      radius = 4f - depth,
      center = Offset(bx, by),
    )
  }
}

@Composable
fun SakuraFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Canvas(modifier = Modifier.size(24.dp)) {
      val petalPath = Path().apply {
        moveTo(size.width / 2, 2f)
        quadraticTo(size.width - 2f, size.height / 3, size.width / 2, size.height - 4f)
        quadraticTo(2f, size.height / 3, size.width / 2, 2f)
        close()
      }
      drawPath(petalPath, Color(0xFFFFB7C5))
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color(0xFF5D3A29),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun SakuraPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  val features = listOf(
    "Unlimited Zen Access",
    "Exclusive Spring Themes",
    "Peaceful Ad-free Experience",
    "Mindfulness Features",
    "Sync Across Devices",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFFFFF5F5),
            Color(0xFFFFE4E8),
            Color(0xFFFFDADE),
            Color(0xFFFFF0F3),
          ),
        ),
      ),
  ) {
    SakuraBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(60.dp))

      Text(
        text = "SPRING BLOOM",
        style = TextStyle(
          color = Color(0xFFE891A0),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Sakura Premium",
        style = TextStyle(
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFE891A0),
              Color(0xFFFFB7C5),
              Color(0xFFE891A0),
            ),
          ),
        ),
      )

      Spacer(modifier = Modifier.weight(0.4f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.7f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          SakuraFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.6f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$39.99/year",
          style = TextStyle(
            color = Color(0xFF5D3A29),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "7-day free trial",
          style = TextStyle(
            color = Color(0xFFE891A0),
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
          containerColor = Color(0xFFFFB7C5),
        ),
        shape = RoundedCornerShape(14.dp),
      ) {
        Text(
          text = "Begin Your Journey",
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
          color = Color(0xFF5D3A29).copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
