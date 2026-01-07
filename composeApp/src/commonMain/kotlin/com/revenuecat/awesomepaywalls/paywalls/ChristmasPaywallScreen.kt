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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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

data class Snowflake(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var size: Float,
  var alpha: Float,
  var rotation: Float,
  var rotationSpeed: Float,
  var type: SnowflakeType,
)

enum class SnowflakeType {
  SMALL,
  MEDIUM,
  LARGE,
  SPARKLE,
}

class SnowfallSystem {
  private val snowflakes = mutableListOf<Snowflake>()
  private val random = Random
  private val maxSnowflakes = 100

  fun update(width: Float, height: Float, deltaTime: Float) {
    // Spawn new snowflakes
    if (snowflakes.size < maxSnowflakes && random.nextFloat() < 0.3f) {
      spawnSnowflake(width)
    }

    // Update existing snowflakes
    val iterator = snowflakes.iterator()
    while (iterator.hasNext()) {
      val snowflake = iterator.next()

      // Apply wind sway
      snowflake.vx = sin(snowflake.y * 0.01f + snowflake.x * 0.005f) * 30f
      snowflake.x += snowflake.vx * deltaTime
      snowflake.y += snowflake.vy * deltaTime
      snowflake.rotation += snowflake.rotationSpeed * deltaTime

      // Remove if out of bounds
      if (snowflake.y > height + 20f || snowflake.x < -20f || snowflake.x > width + 20f) {
        iterator.remove()
      }
    }
  }

  private fun spawnSnowflake(width: Float) {
    val type = when {
      random.nextFloat() < 0.1f -> SnowflakeType.SPARKLE
      random.nextFloat() < 0.3f -> SnowflakeType.LARGE
      random.nextFloat() < 0.6f -> SnowflakeType.MEDIUM
      else -> SnowflakeType.SMALL
    }

    val size = when (type) {
      SnowflakeType.SMALL -> random.nextFloat() * 3f + 2f
      SnowflakeType.MEDIUM -> random.nextFloat() * 5f + 4f
      SnowflakeType.LARGE -> random.nextFloat() * 8f + 6f
      SnowflakeType.SPARKLE -> random.nextFloat() * 6f + 4f
    }

    snowflakes.add(
      Snowflake(
        x = random.nextFloat() * width,
        y = -20f,
        vx = 0f,
        vy = random.nextFloat() * 80f + 40f,
        size = size,
        alpha = random.nextFloat() * 0.5f + 0.5f,
        rotation = random.nextFloat() * 360f,
        rotationSpeed = random.nextFloat() * 100f - 50f,
        type = type,
      ),
    )
  }

  fun draw(drawScope: DrawScope) {
    for (snowflake in snowflakes) {
      drawScope.drawContext.canvas.save()
      drawScope.drawContext.canvas.translate(snowflake.x, snowflake.y)

      when (snowflake.type) {
        SnowflakeType.SPARKLE -> {
          // Draw sparkle with glow
          drawScope.drawCircle(
            color = Color.White.copy(alpha = snowflake.alpha * 0.3f),
            radius = snowflake.size * 2f,
            center = Offset.Zero,
            blendMode = BlendMode.Plus,
          )
          drawScope.drawCircle(
            color = Color.White.copy(alpha = snowflake.alpha),
            radius = snowflake.size,
            center = Offset.Zero,
          )
        }
        else -> {
          // Draw snowflake crystal
          drawSnowflakeCrystal(drawScope, snowflake)
        }
      }

      drawScope.drawContext.canvas.restore()
    }
  }

  private fun drawSnowflakeCrystal(drawScope: DrawScope, snowflake: Snowflake) {
    val arms = 6
    val armLength = snowflake.size

    for (i in 0 until arms) {
      val angle = (i * 60f + snowflake.rotation) * PI.toFloat() / 180f
      val endX = cos(angle) * armLength
      val endY = sin(angle) * armLength

      drawScope.drawLine(
        color = Color.White.copy(alpha = snowflake.alpha),
        start = Offset.Zero,
        end = Offset(endX, endY),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round,
      )

      // Draw small branches
      if (snowflake.type != SnowflakeType.SMALL) {
        val branchLength = armLength * 0.4f
        val branchStart = armLength * 0.5f
        for (side in listOf(-1, 1)) {
          val branchAngle = angle + side * 45f * PI.toFloat() / 180f
          val startX = cos(angle) * branchStart
          val startY = sin(angle) * branchStart
          val branchEndX = startX + cos(branchAngle) * branchLength
          val branchEndY = startY + sin(branchAngle) * branchLength

          drawScope.drawLine(
            color = Color.White.copy(alpha = snowflake.alpha * 0.8f),
            start = Offset(startX, startY),
            end = Offset(branchEndX, branchEndY),
            strokeWidth = 1f,
            cap = StrokeCap.Round,
          )
        }
      }
    }
  }
}

data class FloatingOrnament(
  var x: Float,
  var y: Float,
  var baseY: Float,
  var size: Float,
  var color: Color,
  var glowColor: Color,
  var phase: Float,
  var speed: Float,
)

class OrnamentSystem {
  private val ornaments = mutableListOf<FloatingOrnament>()
  private val random = Random

  private val ornamentColors = listOf(
    Color(0xFFE53935) to Color(0xFFFF6F60), // Red
    Color(0xFFC62828) to Color(0xFFFF5252), // Dark Red
    Color(0xFFFFD700) to Color(0xFFFFE57F), // Gold
    Color(0xFF2E7D32) to Color(0xFF60AD5E), // Green
    Color(0xFF1565C0) to Color(0xFF5E92F3), // Blue
    Color(0xFF6A1B9A) to Color(0xFF9C4DCC), // Purple
  )

  fun initialize(width: Float, height: Float, count: Int = 15) {
    ornaments.clear()
    for (i in 0 until count) {
      val colorPair = ornamentColors[random.nextInt(ornamentColors.size)]
      val y = random.nextFloat() * height
      ornaments.add(
        FloatingOrnament(
          x = random.nextFloat() * width,
          y = y,
          baseY = y,
          size = random.nextFloat() * 15f + 10f,
          color = colorPair.first,
          glowColor = colorPair.second,
          phase = random.nextFloat() * 2f * PI.toFloat(),
          speed = random.nextFloat() * 2f + 1f,
        ),
      )
    }
  }

  fun update(time: Float) {
    for (ornament in ornaments) {
      ornament.y = ornament.baseY + sin(time * ornament.speed + ornament.phase) * 20f
    }
  }

  fun draw(drawScope: DrawScope) {
    for (ornament in ornaments) {
      // Outer glow
      drawScope.drawCircle(
        color = ornament.glowColor.copy(alpha = 0.2f),
        radius = ornament.size * 2f,
        center = Offset(ornament.x, ornament.y),
        blendMode = BlendMode.Plus,
      )
      // Mid glow
      drawScope.drawCircle(
        color = ornament.glowColor.copy(alpha = 0.4f),
        radius = ornament.size * 1.3f,
        center = Offset(ornament.x, ornament.y),
        blendMode = BlendMode.Plus,
      )
      // Core ornament
      drawScope.drawCircle(
        color = ornament.color,
        radius = ornament.size,
        center = Offset(ornament.x, ornament.y),
      )
      // Highlight
      drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = ornament.size * 0.3f,
        center = Offset(
          ornament.x - ornament.size * 0.3f,
          ornament.y - ornament.size * 0.3f,
        ),
      )
    }
  }
}

@Composable
fun ChristmasLightsString(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "lights")

  val lightColors = listOf(
    Color(0xFFFF1744), // Red
    Color(0xFF00E676), // Green
    Color(0xFFFFD600), // Yellow
    Color(0xFF2979FF), // Blue
    Color(0xFFFF9100), // Orange
  )

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    lightColors.forEachIndexed { index, color ->
      val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(
            durationMillis = 800,
            easing = LinearEasing,
            delayMillis = index * 150,
          ),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "light_$index",
      )

      val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
          animation = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing,
            delayMillis = index * 150,
          ),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "scale_$index",
      )

      Box(
        modifier = Modifier
          .size(16.dp)
          .scale(scale)
          .background(
            color = color.copy(alpha = alpha),
            shape = CircleShape,
          )
          .blur(4.dp),
      )
      Box(
        modifier = Modifier
          .size(12.dp)
          .scale(scale)
          .offset(x = (-14).dp)
          .background(
            color = color.copy(alpha = alpha),
            shape = CircleShape,
          ),
      )
    }
  }
}

@Composable
fun AnimatedGiftBox(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "gift")

  val bounce by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = -10f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "bounce",
  )

  val rotate by infiniteTransition.animateFloat(
    initialValue = -5f,
    targetValue = 5f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "rotate",
  )

  Box(
    modifier = modifier
      .offset(y = bounce.dp)
      .rotate(rotate),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "🎁",
      fontSize = 48.sp,
    )
  }
}

@Composable
fun SnowfallBackground(modifier: Modifier = Modifier) {
  val snowfallSystem = remember { SnowfallSystem() }
  val ornamentSystem = remember { OrnamentSystem() }
  var initialized by remember { mutableStateOf(false) }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { frameTimeNanos ->
        val deltaTime = if (lastFrameTimeNanos == 0L) {
          0.016f // Default to ~60fps for first frame
        } else {
          ((frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f).coerceIn(0f, 0.1f)
        }
        lastFrameTimeNanos = frameTimeNanos
        totalTime += deltaTime

        if (canvasSize.width > 0 && canvasSize.height > 0) {
          if (!initialized) {
            ornamentSystem.initialize(canvasSize.width, canvasSize.height)
            initialized = true
          }
          snowfallSystem.update(canvasSize.width, canvasSize.height, deltaTime)
          ornamentSystem.update(totalTime)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    ornamentSystem.draw(this)
    snowfallSystem.draw(this)
  }
}

@Composable
fun PremiumFeatureItem(emoji: String, title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text = emoji, fontSize = 20.sp)
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = title,
      style = TextStyle(
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun AnimatedStarDecoration(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "star")

  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "rotation",
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 1.1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Box(
    modifier = modifier
      .rotate(rotation)
      .scale(scale),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = "⭐", fontSize = 32.sp)
  }
}

@Composable
fun GlowingBorderCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "glow")

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glowAlpha",
  )

  Box(
    modifier = modifier,
  ) {
    // Glow effect
    Box(
      modifier = Modifier
        .matchParentSize()
        .blur(20.dp)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFFE53935).copy(alpha = glowAlpha),
              Color(0xFF2E7D32).copy(alpha = glowAlpha),
            ),
          ),
          shape = RoundedCornerShape(24.dp),
        ),
    )

    // Card content
    Box(
      modifier = Modifier
        .matchParentSize()
        .clip(RoundedCornerShape(24.dp))
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFF1A1A2E),
              Color(0xFF16213E),
            ),
          ),
        )
        .border(
          width = 2.dp,
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFFE53935).copy(alpha = 0.6f),
              Color(0xFF2E7D32).copy(alpha = 0.6f),
            ),
          ),
          shape = RoundedCornerShape(24.dp),
        )
        .padding(24.dp),
    ) {
      content()
    }
  }
}

@Composable
fun CandyCaneDecoration(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "candy")

  val swing by infiniteTransition.animateFloat(
    initialValue = -15f,
    targetValue = 15f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "swing",
  )

  Text(
    text = "🍬",
    fontSize = 28.sp,
    modifier = modifier.rotate(swing),
  )
}

@Composable
fun ChristmasPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  val infiniteTransition = rememberInfiniteTransition(label = "paywall")

  val titleScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "titleScale",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F0F23),
            Color(0xFF1A1A2E),
            Color(0xFF0F0F23),
          ),
        ),
      ),
    contentAlignment = Alignment.Center,
  ) {
    // Animated snowfall and ornaments background
    SnowfallBackground()

    // Christmas lights at top
    ChristmasLightsString(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 60.dp),
    )

    // Star decorations
    AnimatedStarDecoration(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 30.dp, top = 120.dp),
    )
    AnimatedStarDecoration(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(end = 30.dp, top = 140.dp),
    )

    // Main content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      // Holiday greeting
      Text(
        text = "🎄 Holiday Special 🎄",
        style = TextStyle(
          color = Color(0xFFFFD700),
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
        ),
        modifier = Modifier.scale(titleScale),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Main title
      Text(
        text = "Unlock Premium",
        style = TextStyle(
          color = Color.White,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "This Christmas",
        style = TextStyle(
          color = Color(0xFFE53935),
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Gift box animation
      AnimatedGiftBox()

      Spacer(modifier = Modifier.height(24.dp))

      // Features card
      GlowingBorderCard(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(
            text = "Premium Features",
            style = TextStyle(
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
            ),
          )

          Spacer(modifier = Modifier.height(16.dp))

          PremiumFeatureItem(
            emoji = "🎅",
            title = "Unlimited Holiday Themes",
          )
          PremiumFeatureItem(
            emoji = "🦌",
            title = "Exclusive Winter Content",
          )
          PremiumFeatureItem(
            emoji = "🎁",
            title = "Special Gift Rewards",
          )
          PremiumFeatureItem(
            emoji = "❄️",
            title = "Ad-Free Experience",
          )
          PremiumFeatureItem(
            emoji = "🌟",
            title = "Priority Support",
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Discount badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        CandyCaneDecoration()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "50% OFF",
          style = TextStyle(
            color = Color(0xFF4CAF50),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
          ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        CandyCaneDecoration(modifier = Modifier.rotate(180f))
      }

      Text(
        text = "Limited Time Holiday Offer!",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 12.sp,
        ),
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Yearly Subscribe button (Best Value)
      Box(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Button(
          onClick = { paywallState.purchase(PackageType.ANNUAL) },
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2E7D32),
          ),
          shape = RoundedCornerShape(16.dp),
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
              text = "🎁 Yearly - $29.99/year",
              style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Save 50% • Only $2.49/month",
              style = TextStyle(
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
              ),
            )
          }
        }

        // Best Value badge
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 8.dp, y = (-8).dp)
            .background(
              color = Color(0xFFFFD700),
              shape = RoundedCornerShape(8.dp),
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

      // Monthly Subscribe button
      Button(
        onClick = { paywallState.purchase(PackageType.MONTHLY) },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFE53935),
        ),
        shape = RoundedCornerShape(16.dp),
      ) {
        Text(
          text = "🎄 Monthly - $4.99/month",
          style = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Restore purchases
      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 14.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Terms
      Text(
        text = "Cancel anytime. Terms apply.",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.4f),
          fontSize = 12.sp,
        ),
        textAlign = TextAlign.Center,
      )
    }

    // Bottom candy canes decoration
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text("🍭", fontSize = 24.sp)
      Text("🎄", fontSize = 24.sp)
      Text("⛄", fontSize = 24.sp)
      Text("🎄", fontSize = 24.sp)
      Text("🍭", fontSize = 24.sp)
    }
  }
}
