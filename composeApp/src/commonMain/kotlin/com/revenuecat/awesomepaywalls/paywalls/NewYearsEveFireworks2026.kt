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

data class FireworkParticle2026(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var life: Float,
  var maxLife: Float,
  var color: Color,
  var size: Float,
  var type: FireworkParticleType,
)

enum class FireworkParticleType {
  TRAIL,
  EXPLOSION,
}

data class Firework2026(
  var x: Float,
  var y: Float,
  var vy: Float,
  var targetY: Float,
  var color: Color,
  var exploded: Boolean = false,
  var particles: MutableList<FireworkParticle2026> = mutableListOf(),
  var trailParticles: MutableList<FireworkParticle2026> = mutableListOf(),
)

class FireworksSystem2026 {
  private val fireworks = mutableListOf<Firework2026>()
  private val random = Random

  private val colors = listOf(
    Color(0xFFFF7E79),
    Color(0xFFFFD479),
    Color(0xFFD4FB79),
    Color(0xFF49FA79),
    Color(0xFF49FCD6),
    Color(0xFF4AD6FF),
    Color(0xFF7A81FF),
    Color(0xFFD883FF),
    Color.White,
  )

  fun update(width: Float, height: Float, deltaTime: Float) {
    if (random.nextFloat() < 0.04f) {
      spawnFirework(width, height)
    }

    val iterator = fireworks.iterator()
    while (iterator.hasNext()) {
      val firework = iterator.next()

      if (!firework.exploded) {
        firework.y += firework.vy * deltaTime
        firework.vy += 50f * deltaTime

        if (random.nextFloat() < 0.8f) {
          firework.trailParticles.add(
            FireworkParticle2026(
              x = firework.x + random.nextFloat() * 4f - 2f,
              y = firework.y,
              vx = random.nextFloat() * 20f - 10f,
              vy = random.nextFloat() * 50f + 20f,
              life = 0.5f,
              maxLife = 0.5f,
              color = firework.color.copy(alpha = 0.8f),
              size = random.nextFloat() * 3f + 1f,
              type = FireworkParticleType.TRAIL,
            ),
          )
        }

        if (firework.y <= firework.targetY || firework.vy >= 0) {
          explode(firework)
        }
      }

      updateParticles(firework.trailParticles, deltaTime, gravity = 80f)
      updateParticles(firework.particles, deltaTime, gravity = 60f)

      if (firework.exploded &&
        firework.particles.isEmpty() &&
        firework.trailParticles.isEmpty()
      ) {
        iterator.remove()
      }
    }
  }

  private fun spawnFirework(width: Float, height: Float) {
    val color = colors[random.nextInt(colors.size)]
    fireworks.add(
      Firework2026(
        x = random.nextFloat() * width * 0.8f + width * 0.1f,
        y = height,
        vy = -(random.nextFloat() * 200f + 400f),
        targetY = random.nextFloat() * height * 0.4f + height * 0.1f,
        color = color,
      ),
    )
  }

  private fun explode(firework: Firework2026) {
    firework.exploded = true
    val particleCount = random.nextInt(80) + 120

    for (i in 0 until particleCount) {
      val angle = random.nextFloat() * 2f * PI.toFloat()
      val speed = random.nextFloat() * 200f + 50f
      val life = random.nextFloat() * 1.5f + 1f

      firework.particles.add(
        FireworkParticle2026(
          x = firework.x,
          y = firework.y,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          life = life,
          maxLife = life,
          color = firework.color,
          size = random.nextFloat() * 4f + 2f,
          type = FireworkParticleType.EXPLOSION,
        ),
      )
    }
  }

  private fun updateParticles(
    particles: MutableList<FireworkParticle2026>,
    deltaTime: Float,
    gravity: Float,
  ) {
    val iterator = particles.iterator()
    while (iterator.hasNext()) {
      val p = iterator.next()
      p.x += p.vx * deltaTime
      p.y += p.vy * deltaTime
      p.vy += gravity * deltaTime
      p.vx *= 0.99f
      p.life -= deltaTime

      if (p.life <= 0) {
        iterator.remove()
      }
    }
  }

  fun draw(drawScope: DrawScope) {
    for (firework in fireworks) {
      for (p in firework.trailParticles) {
        val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
        drawScope.drawCircle(
          color = p.color.copy(alpha = alpha * 0.7f),
          radius = p.size,
          center = Offset(p.x, p.y),
          blendMode = BlendMode.Plus,
        )
      }

      if (!firework.exploded) {
        drawScope.drawCircle(
          color = firework.color,
          radius = 4f,
          center = Offset(firework.x, firework.y),
          blendMode = BlendMode.Plus,
        )
      }

      for (p in firework.particles) {
        val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
        val size = p.size * (0.3f + alpha * 0.7f)

        drawScope.drawCircle(
          color = p.color.copy(alpha = alpha * 0.3f),
          radius = size * 2f,
          center = Offset(p.x, p.y),
          blendMode = BlendMode.Plus,
        )

        drawScope.drawCircle(
          color = p.color.copy(alpha = alpha),
          radius = size,
          center = Offset(p.x, p.y),
          blendMode = BlendMode.Plus,
        )
      }
    }
  }
}

@Composable
fun FireworksBackground2026(modifier: Modifier = Modifier) {
  val fireworksSystem = remember { FireworksSystem2026() }
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
          fireworksSystem.update(canvasSize.width, canvasSize.height, deltaTime)
        }
      }
    }
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    canvasSize = size
    fireworksSystem.draw(this)
  }
}

@Composable
fun CountdownDisplay(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "countdown")

  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "scale",
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "🎆 NEW YEAR'S EVE 🎆",
      style = TextStyle(
        color = Color(0xFFFFD700),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
      ),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "2026",
      style = TextStyle(
        fontSize = 80.sp,
        fontWeight = FontWeight.ExtraBold,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFF6B6B),
            Color(0xFF4ECDC4),
            Color(0xFFFFD700),
          ),
        ),
      ),
      modifier = Modifier.scale(scale),
    )
  }
}

@Composable
fun AnimatedPartyPopper(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "popper")

  val rotation by infiniteTransition.animateFloat(
    initialValue = -15f,
    targetValue = 15f,
    animationSpec = infiniteRepeatable(
      animation = tween(300, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "rotation",
  )

  Text(
    text = "🎉",
    fontSize = 40.sp,
    modifier = modifier.rotate(rotation),
  )
}

@Composable
fun GlowingEveCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "glowAlpha",
  )

  Box(modifier = modifier) {
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFFFF6B6B).copy(alpha = glowAlpha * 0.3f),
              Color(0xFF4ECDC4).copy(alpha = glowAlpha * 0.3f),
            ),
          ),
          shape = RoundedCornerShape(24.dp),
        ),
    )

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
              Color(0xFFFF6B6B).copy(alpha = 0.6f),
              Color(0xFF4ECDC4).copy(alpha = 0.6f),
              Color(0xFFFFD700).copy(alpha = 0.6f),
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
fun EveFeatureItem(emoji: String, title: String, modifier: Modifier = Modifier) {
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
fun AnimatedSparkleRow(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "sparkles")

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    repeat(5) { index ->
      val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
          animation = tween(400, easing = FastOutSlowInEasing, delayMillis = index * 80),
          repeatMode = RepeatMode.Reverse,
        ),
        label = "sparkle_$index",
      )

      Text(
        text = "✨",
        fontSize = 18.sp,
        modifier = Modifier.scale(scale),
      )
    }
  }
}

@Composable
fun NewYearsEveFireworksPaywall(onDismiss: () -> Unit = {}) {
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
    FireworksBackground2026()

    AnimatedPartyPopper(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 24.dp, top = 80.dp),
    )
    AnimatedPartyPopper(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(end = 24.dp, top = 100.dp)
        .scale(scaleX = -1f, scaleY = 1f),
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      AnimatedSparkleRow()

      Spacer(modifier = Modifier.height(16.dp))

      CountdownDisplay()

      Spacer(modifier = Modifier.height(24.dp))

      GlowingEveCard(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(
            text = "Celebrate with Premium",
            style = TextStyle(
              color = Color(0xFFFFD700),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
            ),
          )

          Spacer(modifier = Modifier.height(16.dp))

          EveFeatureItem(
            emoji = "🎆",
            title = "Exclusive Fireworks Themes",
          )
          EveFeatureItem(
            emoji = "🎊",
            title = "Party Mode & Celebrations",
          )
          EveFeatureItem(
            emoji = "📅",
            title = "2026 Goal Planner",
          )
          EveFeatureItem(
            emoji = "🌟",
            title = "VIP Early Access",
          )
          EveFeatureItem(
            emoji = "🎁",
            title = "New Year Bonus Rewards",
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Text(text = "🎇", fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "COUNTDOWN SPECIAL",
            style = TextStyle(
              color = Color(0xFFFF6B6B),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp,
            ),
          )
          Text(
            text = "70% OFF",
            style = TextStyle(
              color = Color(0xFF4ECDC4),
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
            ),
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "🎇", fontSize = 28.sp)
      }

      Text(
        text = "Ring in 2026 with Premium!",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 12.sp,
        ),
      )

      Spacer(modifier = Modifier.height(24.dp))

      Box(modifier = Modifier.fillMaxWidth()) {
        Button(
          onClick = { paywallState.purchase(PackageType.ANNUAL) },
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B6B),
          ),
          shape = RoundedCornerShape(16.dp),
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "🎆 Yearly - $17.99/year",
              style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
              ),
            )
            Text(
              text = "Save 70% • Only $1.49/month",
              style = TextStyle(
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
              ),
            )
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 8.dp, y = (-8).dp)
            .background(
              color = Color(0xFF4ECDC4),
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

      Button(
        onClick = { paywallState.purchase(PackageType.MONTHLY) },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF7A81FF),
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

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 14.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Cancel anytime. Terms apply.",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.4f),
          fontSize = 12.sp,
        ),
        textAlign = TextAlign.Center,
      )
    }

    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text("🎊", fontSize = 24.sp)
      Text("🎆", fontSize = 24.sp)
      Text("🥳", fontSize = 24.sp)
      Text("🎆", fontSize = 24.sp)
      Text("🎊", fontSize = 24.sp)
    }
  }
}
