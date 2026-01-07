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
import androidx.compose.ui.geometry.Size
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

data class FireworkParticle(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var life: Float,
  var maxLife: Float,
  var color: Color,
  var size: Float,
  var trail: MutableList<Offset> = mutableListOf(),
)

data class Firework(
  var x: Float,
  var y: Float,
  var vy: Float,
  var targetY: Float,
  var color: Color,
  var exploded: Boolean = false,
  var particles: MutableList<FireworkParticle> = mutableListOf(),
)

class FireworkSystem {
  private val fireworks = mutableListOf<Firework>()
  private val random = Random
  private var timeSinceLastLaunch = 0f
  private val launchInterval = 0.8f

  private val fireworkColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFFFF6B6B), // Coral Red
    Color(0xFF4ECDC4), // Teal
    Color(0xFFFF69B4), // Hot Pink
    Color(0xFF87CEEB), // Sky Blue
    Color(0xFFFFFFFF), // White
    Color(0xFFFFA500), // Orange
    Color(0xFF9370DB), // Purple
    Color(0xFF00FF7F), // Spring Green
  )

  fun update(width: Float, height: Float, deltaTime: Float) {
    timeSinceLastLaunch += deltaTime

    // Launch new fireworks
    if (timeSinceLastLaunch >= launchInterval && fireworks.size < 8) {
      launchFirework(width, height)
      timeSinceLastLaunch = 0f
    }

    // Update existing fireworks
    val iterator = fireworks.iterator()
    while (iterator.hasNext()) {
      val firework = iterator.next()

      if (!firework.exploded) {
        // Rising phase
        firework.y += firework.vy * deltaTime

        if (firework.y <= firework.targetY) {
          explodeFirework(firework)
        }
      } else {
        // Update particles
        val particleIterator = firework.particles.iterator()
        while (particleIterator.hasNext()) {
          val particle = particleIterator.next()

          // Add current position to trail
          particle.trail.add(Offset(particle.x, particle.y))
          if (particle.trail.size > 5) {
            particle.trail.removeAt(0)
          }

          // Physics
          particle.vy += 150f * deltaTime // Gravity
          particle.x += particle.vx * deltaTime
          particle.y += particle.vy * deltaTime
          particle.life -= deltaTime

          // Fade and shrink
          if (particle.life <= 0) {
            particleIterator.remove()
          }
        }

        // Remove firework when all particles are gone
        if (firework.particles.isEmpty()) {
          iterator.remove()
        }
      }
    }
  }

  private fun launchFirework(width: Float, height: Float) {
    val color = fireworkColors[random.nextInt(fireworkColors.size)]
    fireworks.add(
      Firework(
        x = random.nextFloat() * width * 0.6f + width * 0.2f,
        y = height + 20f,
        vy = -(random.nextFloat() * 200f + 400f),
        targetY = random.nextFloat() * height * 0.4f + height * 0.1f,
        color = color,
      ),
    )
  }

  private fun explodeFirework(firework: Firework) {
    firework.exploded = true
    val particleCount = random.nextInt(40) + 60

    for (i in 0 until particleCount) {
      val angle = random.nextFloat() * 2f * PI.toFloat()
      val speed = random.nextFloat() * 200f + 100f
      val life = random.nextFloat() * 1.5f + 1f

      firework.particles.add(
        FireworkParticle(
          x = firework.x,
          y = firework.y,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          life = life,
          maxLife = life,
          color = firework.color,
          size = random.nextFloat() * 4f + 2f,
        ),
      )
    }
  }

  fun draw(drawScope: DrawScope) {
    for (firework in fireworks) {
      if (!firework.exploded) {
        // Draw rising firework
        drawScope.drawCircle(
          color = firework.color,
          radius = 4f,
          center = Offset(firework.x, firework.y),
        )
        // Trail glow
        drawScope.drawCircle(
          color = firework.color.copy(alpha = 0.3f),
          radius = 12f,
          center = Offset(firework.x, firework.y),
          blendMode = BlendMode.Plus,
        )
      } else {
        // Draw particles
        for (particle in firework.particles) {
          val alpha = (particle.life / particle.maxLife).coerceIn(0f, 1f)

          // Draw trail
          for ((index, pos) in particle.trail.withIndex()) {
            val trailAlpha = alpha * (index.toFloat() / particle.trail.size) * 0.5f
            drawScope.drawCircle(
              color = particle.color.copy(alpha = trailAlpha),
              radius = particle.size * 0.5f,
              center = pos,
            )
          }

          // Draw particle with glow
          drawScope.drawCircle(
            color = particle.color.copy(alpha = alpha * 0.4f),
            radius = particle.size * 2f,
            center = Offset(particle.x, particle.y),
            blendMode = BlendMode.Plus,
          )
          drawScope.drawCircle(
            color = particle.color.copy(alpha = alpha),
            radius = particle.size,
            center = Offset(particle.x, particle.y),
          )
        }
      }
    }
  }
}

data class Confetti(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var rotation: Float,
  var rotationSpeed: Float,
  var width: Float,
  var height: Float,
  var color: Color,
  var wobblePhase: Float,
  var wobbleSpeed: Float,
)

class ConfettiSystem {
  private val confettiList = mutableListOf<Confetti>()
  private val random = Random
  private val maxConfetti = 80

  private val confettiColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFFC0C0C0), // Silver
    Color(0xFFFF69B4), // Pink
    Color(0xFF00CED1), // Cyan
    Color(0xFFFF4500), // Orange Red
    Color(0xFF9400D3), // Violet
    Color(0xFF00FF00), // Lime
    Color(0xFFFFFFFF), // White
  )

  fun update(width: Float, height: Float, deltaTime: Float, totalTime: Float) {
    // Spawn new confetti
    if (confettiList.size < maxConfetti && random.nextFloat() < 0.4f) {
      spawnConfetti(width)
    }

    // Update confetti
    val iterator = confettiList.iterator()
    while (iterator.hasNext()) {
      val confetti = iterator.next()

      // Wobble effect
      confetti.vx = sin(totalTime * confetti.wobbleSpeed + confetti.wobblePhase) * 50f
      confetti.x += confetti.vx * deltaTime
      confetti.y += confetti.vy * deltaTime
      confetti.rotation += confetti.rotationSpeed * deltaTime

      if (confetti.y > height + 20f) {
        iterator.remove()
      }
    }
  }

  private fun spawnConfetti(width: Float) {
    confettiList.add(
      Confetti(
        x = random.nextFloat() * width,
        y = -20f,
        vx = 0f,
        vy = random.nextFloat() * 100f + 80f,
        rotation = random.nextFloat() * 360f,
        rotationSpeed = random.nextFloat() * 400f - 200f,
        width = random.nextFloat() * 8f + 4f,
        height = random.nextFloat() * 12f + 6f,
        color = confettiColors[random.nextInt(confettiColors.size)],
        wobblePhase = random.nextFloat() * 2f * PI.toFloat(),
        wobbleSpeed = random.nextFloat() * 3f + 2f,
      ),
    )
  }

  fun draw(drawScope: DrawScope) {
    for (confetti in confettiList) {
      drawScope.withTransform({
        translate(confetti.x, confetti.y)
        rotate(confetti.rotation, Offset.Zero)
      }) {
        drawRect(
          color = confetti.color,
          topLeft = Offset(-confetti.width / 2, -confetti.height / 2),
          size = Size(confetti.width, confetti.height),
        )
      }
    }
  }
}

data class Bubble(
  var x: Float,
  var y: Float,
  var vy: Float,
  var size: Float,
  var alpha: Float,
  var wobblePhase: Float,
  var wobbleAmount: Float,
)

class BubbleSystem {
  private val bubbles = mutableListOf<Bubble>()
  private val random = Random
  private val maxBubbles = 40

  fun update(width: Float, height: Float, deltaTime: Float, totalTime: Float) {
    // Spawn bubbles from bottom
    if (bubbles.size < maxBubbles && random.nextFloat() < 0.2f) {
      bubbles.add(
        Bubble(
          x = random.nextFloat() * width,
          y = height + 10f,
          vy = -(random.nextFloat() * 60f + 40f),
          size = random.nextFloat() * 6f + 3f,
          alpha = random.nextFloat() * 0.4f + 0.3f,
          wobblePhase = random.nextFloat() * 2f * PI.toFloat(),
          wobbleAmount = random.nextFloat() * 20f + 10f,
        ),
      )
    }

    val iterator = bubbles.iterator()
    while (iterator.hasNext()) {
      val bubble = iterator.next()
      bubble.y += bubble.vy * deltaTime
      bubble.x += sin(totalTime * 2f + bubble.wobblePhase) * bubble.wobbleAmount * deltaTime

      if (bubble.y < -20f) {
        iterator.remove()
      }
    }
  }

  fun draw(drawScope: DrawScope) {
    for (bubble in bubbles) {
      // Outer glow
      drawScope.drawCircle(
        color = Color(0xFFFFD700).copy(alpha = bubble.alpha * 0.3f),
        radius = bubble.size * 2f,
        center = Offset(bubble.x, bubble.y),
        blendMode = BlendMode.Plus,
      )
      // Bubble
      drawScope.drawCircle(
        color = Color.White.copy(alpha = bubble.alpha),
        radius = bubble.size,
        center = Offset(bubble.x, bubble.y),
        style = Stroke(width = 1.5f),
      )
      // Highlight
      drawScope.drawCircle(
        color = Color.White.copy(alpha = bubble.alpha * 0.8f),
        radius = bubble.size * 0.3f,
        center = Offset(bubble.x - bubble.size * 0.3f, bubble.y - bubble.size * 0.3f),
      )
    }
  }
}

data class Sparkle(
  var x: Float,
  var y: Float,
  var size: Float,
  var alpha: Float,
  var phase: Float,
  var speed: Float,
)

class SparkleSystem {
  private val sparkles = mutableListOf<Sparkle>()
  private val random = Random

  fun initialize(width: Float, height: Float, count: Int = 50) {
    sparkles.clear()
    for (i in 0 until count) {
      sparkles.add(
        Sparkle(
          x = random.nextFloat() * width,
          y = random.nextFloat() * height,
          size = random.nextFloat() * 3f + 1f,
          alpha = random.nextFloat() * 0.6f + 0.2f,
          phase = random.nextFloat() * 2f * PI.toFloat(),
          speed = random.nextFloat() * 3f + 2f,
        ),
      )
    }
  }

  fun draw(drawScope: DrawScope, totalTime: Float) {
    for (sparkle in sparkles) {
      val animatedAlpha =
        sparkle.alpha * (0.5f + 0.5f * sin(totalTime * sparkle.speed + sparkle.phase))
      val animatedSize =
        sparkle.size * (0.8f + 0.4f * sin(totalTime * sparkle.speed + sparkle.phase))

      // Draw 4-point star
      val arms = 4
      for (i in 0 until arms) {
        val angle = (i * 90f) * PI.toFloat() / 180f
        val endX = sparkle.x + cos(angle) * animatedSize * 2f
        val endY = sparkle.y + sin(angle) * animatedSize * 2f

        drawScope.drawLine(
          color = Color.White.copy(alpha = animatedAlpha),
          start = Offset(sparkle.x, sparkle.y),
          end = Offset(endX, endY),
          strokeWidth = 1.5f,
          cap = StrokeCap.Round,
        )
      }

      // Center glow
      drawScope.drawCircle(
        color = Color.White.copy(alpha = animatedAlpha * 0.5f),
        radius = animatedSize,
        center = Offset(sparkle.x, sparkle.y),
        blendMode = BlendMode.Plus,
      )
    }
  }
}

@Composable
fun NewYearBackground(modifier: Modifier = Modifier) {
  val fireworkSystem = remember { FireworkSystem() }
  val confettiSystem = remember { ConfettiSystem() }
  val bubbleSystem = remember { BubbleSystem() }
  val sparkleSystem = remember { SparkleSystem() }

  var initialized by remember { mutableStateOf(false) }
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
          if (!initialized) {
            sparkleSystem.initialize(canvasSize.width, canvasSize.height)
            initialized = true
          }
          fireworkSystem.update(canvasSize.width, canvasSize.height, deltaTime)
          confettiSystem.update(canvasSize.width, canvasSize.height, deltaTime, totalTime)
          bubbleSystem.update(canvasSize.width, canvasSize.height, deltaTime, totalTime)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    sparkleSystem.draw(this, totalTime)
    bubbleSystem.draw(this)
    fireworkSystem.draw(this)
    confettiSystem.draw(this)
  }
}

@Composable
fun AnimatedYearDisplay(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "year")

  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Text(
    text = "2026",
    style = TextStyle(
      fontSize = 72.sp,
      fontWeight = FontWeight.ExtraBold,
      brush = Brush.linearGradient(
        colors = listOf(
          Color(0xFFFFD700),
          Color(0xFFFFA500),
          Color(0xFFFFD700),
        ),
      ),
    ),
    modifier = modifier.scale(scale),
  )
}

@Composable
fun AnimatedChampagneGlass(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "champagne")

  val tilt by infiniteTransition.animateFloat(
    initialValue = -5f,
    targetValue = 5f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "tilt",
  )

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "🥂",
      fontSize = 48.sp,
      modifier = Modifier.rotate(tilt),
    )
  }
}

@Composable
fun GlowingNewYearCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glowAlpha",
  )

  Box(modifier = modifier) {
    // Golden glow effect
    Box(
      modifier = Modifier
        .matchParentSize()
        .blur(24.dp)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFFFD700).copy(alpha = glowAlpha),
              Color(0xFFFFA500).copy(alpha = glowAlpha * 0.5f),
              Color.Transparent,
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
              Color(0xFF1A1A2E).copy(alpha = 0.95f),
              Color(0xFF0D0D1A).copy(alpha = 0.95f),
            ),
          ),
        )
        .border(
          width = 2.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFFFD700).copy(alpha = 0.8f),
              Color(0xFFC0C0C0).copy(alpha = 0.6f),
              Color(0xFFFFD700).copy(alpha = 0.8f),
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
fun NewYearFeatureItem(emoji: String, title: String, modifier: Modifier = Modifier) {
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
fun AnimatedStarsRow(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "stars")

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    repeat(5) { index ->
      val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
          animation = tween(600, easing = FastOutSlowInEasing, delayMillis = index * 100),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "star_$index",
      )

      val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
          animation = tween(800, easing = FastOutSlowInEasing, delayMillis = index * 150),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "rotation_$index",
      )

      Text(
        text = "✨",
        fontSize = 20.sp,
        modifier = Modifier
          .scale(scale)
          .rotate(rotation),
      )
    }
  }
}

data class BurstParticle(val angle: Float, val speed: Float, val color: Color, val size: Float)

@Composable
fun FireworkBurstDecoration(modifier: Modifier = Modifier, color: Color = Color(0xFFFFD700)) {
  val particles = remember {
    val random = Random
    val colors = listOf(
      Color(0xFFFFD700),
      Color(0xFFFF6B6B),
      Color(0xFF4ECDC4),
      Color(0xFFFF69B4),
      Color(0xFFFFFFFF),
    )
    List(24) { i ->
      BurstParticle(
        angle = (i * 15f) * PI.toFloat() / 180f,
        speed = random.nextFloat() * 20f + 25f,
        color = colors[random.nextInt(colors.size)],
        size = random.nextFloat() * 2f + 2f,
      )
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "burst")

  val progress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart,
    ),
    label = "burstProgress",
  )

  Canvas(modifier = modifier.size(80.dp)) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    for (particle in particles) {
      val distance = particle.speed * progress
      val alpha = (1f - progress).coerceIn(0f, 1f)
      val particleSize = particle.size * (1f - progress * 0.5f)

      val x = centerX + cos(particle.angle) * distance
      val y = centerY + sin(particle.angle) * distance

      drawCircle(
        color = particle.color.copy(alpha = alpha * 0.4f),
        radius = particleSize * 2f,
        center = Offset(x, y),
        blendMode = BlendMode.Plus,
      )
      drawCircle(
        color = particle.color.copy(alpha = alpha),
        radius = particleSize,
        center = Offset(x, y),
      )
    }

    val coreAlpha = (1f - progress * 2f).coerceIn(0f, 1f)
    if (coreAlpha > 0f) {
      drawCircle(
        color = Color.White.copy(alpha = coreAlpha * 0.5f),
        radius = 8f * (1f - progress),
        center = Offset(centerX, centerY),
        blendMode = BlendMode.Plus,
      )
    }
  }
}

@Composable
fun NewYearPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0A0A1A),
            Color(0xFF1A1A3A),
            Color(0xFF0A0A1A),
          ),
        ),
      ),
    contentAlignment = Alignment.Center,
  ) {
    // Animated background with fireworks, confetti, and bubbles
    NewYearBackground()

    // Decorations
    FireworkBurstDecoration(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 20.dp, top = 80.dp),
    )
    FireworkBurstDecoration(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(end = 20.dp, top = 100.dp),
    )

    // Main content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      // Stars decoration
      AnimatedStarsRow()

      Spacer(modifier = Modifier.height(8.dp))

      // Happy New Year text
      Text(
        text = "HAPPY NEW YEAR",
        style = TextStyle(
          color = Color(0xFFC0C0C0),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Animated year display
      AnimatedYearDisplay()

      Spacer(modifier = Modifier.height(8.dp))

      // Champagne glasses
      AnimatedChampagneGlass()

      Spacer(modifier = Modifier.height(24.dp))

      // Features card
      GlowingNewYearCard(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(
            text = "New Year Premium",
            style = TextStyle(
              color = Color(0xFFFFD700),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
            ),
          )

          Spacer(modifier = Modifier.height(16.dp))

          NewYearFeatureItem(
            emoji = "🎯",
            title = "Unlimited Goals & Resolutions",
          )
          NewYearFeatureItem(
            emoji = "📊",
            title = "Advanced Progress Analytics",
          )
          NewYearFeatureItem(
            emoji = "🏆",
            title = "Achievement Badges & Rewards",
          )
          NewYearFeatureItem(
            emoji = "🔔",
            title = "Smart Reminder System",
          )
          NewYearFeatureItem(
            emoji = "☁️",
            title = "Cloud Sync Across Devices",
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Special offer badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(text = "🎉", fontSize = 24.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "NEW YEAR SPECIAL",
            style = TextStyle(
              color = Color(0xFFFFD700),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp,
            ),
          )
          Text(
            text = "60% OFF",
            style = TextStyle(
              color = Color(0xFF00FF7F),
              fontSize = 28.sp,
              fontWeight = FontWeight.ExtraBold,
            ),
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "🎉", fontSize = 24.sp)
      }

      Text(
        text = "Start 2026 with Premium!",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 12.sp,
        ),
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Yearly subscription button
      Box(modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = { paywallState.purchase(PackageType.ANNUAL) },
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700),
          ),
          shape = RoundedCornerShape(16.dp),
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "🎆 Yearly - $23.99/year",
              style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Save 60% • Only $1.99/month",
              style = TextStyle(
                color = Color.Black.copy(alpha = 0.7f),
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
              color = Color(0xFF00FF7F),
              shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
          Text(
            text = "BEST DEAL",
            style = TextStyle(
              color = Color.Black,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
            ),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Monthly subscription button
      Button(
        onClick = { paywallState.purchase(PackageType.MONTHLY) },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF9370DB),
        ),
        shape = RoundedCornerShape(16.dp),
      ) {
        Text(
          text = "🌟 Monthly - $4.99/month",
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

    // Bottom decoration
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text("🎊", fontSize = 24.sp)
      Text("✨", fontSize = 24.sp)
      Text("🎇", fontSize = 24.sp)
      Text("✨", fontSize = 24.sp)
      Text("🎊", fontSize = 24.sp)
    }
  }
}
