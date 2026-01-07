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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.purchase.PackageType
import com.revenuecat.awesomepaywalls.purchase.rememberPaywallState
import kotlin.math.*
import kotlin.random.Random

enum class TreeGrowthStage(val progress: Float) {
  SEED(0f),
  SPROUT(0.25f),
  SAPLING(0.5f),
  YOUNG_TREE(0.75f),
  FULL_TREE(1f),
}

data class FallingLeaf(
  var x: Float,
  var y: Float,
  var rotation: Float,
  var rotationSpeed: Float,
  var size: Float,
  var alpha: Float,
  var swayPhase: Float,
  var fallSpeed: Float,
  var color: Color,
)

class TreeParticleSystem {
  private val leaves = mutableListOf<FallingLeaf>()
  private val random = Random

  private val leafColors = listOf(
    Color(0xFF228B22),
    Color(0xFF32CD32),
    Color(0xFF90EE90),
    Color(0xFF006400),
  )

  fun spawnLeaves(centerX: Float, topY: Float, count: Int = 8) {
    repeat(count) {
      leaves.add(
        FallingLeaf(
          x = centerX + (random.nextFloat() - 0.5f) * 120f,
          y = topY + random.nextFloat() * 60f,
          rotation = random.nextFloat() * 360f,
          rotationSpeed = (random.nextFloat() - 0.5f) * 180f,
          size = random.nextFloat() * 8f + 6f,
          alpha = 1f,
          swayPhase = random.nextFloat() * PI.toFloat() * 2f,
          fallSpeed = random.nextFloat() * 40f + 30f,
          color = leafColors[random.nextInt(leafColors.size)],
        ),
      )
    }
  }

  fun update(deltaTime: Float, totalTime: Float, maxY: Float) {
    val iterator = leaves.iterator()
    while (iterator.hasNext()) {
      val leaf = iterator.next()
      leaf.y += leaf.fallSpeed * deltaTime
      leaf.x += sin(totalTime * 2f + leaf.swayPhase) * 40f * deltaTime
      leaf.rotation += leaf.rotationSpeed * deltaTime

      if (leaf.y > maxY - 50f) {
        leaf.alpha -= deltaTime * 2f
      }

      if (leaf.alpha <= 0f || leaf.y > maxY) {
        iterator.remove()
      }
    }
  }

  fun draw(drawScope: DrawScope) {
    for (leaf in leaves) {
      drawScope.withTransform({
        translate(leaf.x, leaf.y)
        rotate(leaf.rotation)
      }) {
        val path = Path().apply {
          moveTo(0f, -leaf.size)
          quadraticTo(leaf.size * 0.6f, -leaf.size * 0.3f, 0f, leaf.size)
          quadraticTo(-leaf.size * 0.6f, -leaf.size * 0.3f, 0f, -leaf.size)
          close()
        }
        drawPath(path, leaf.color.copy(alpha = leaf.alpha))
      }
    }
  }
}

@Composable
fun TreeCanvas(stage: TreeGrowthStage, modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "tree")

  val sway by infiniteTransition.animateFloat(
    initialValue = -2f,
    targetValue = 2f,
    animationSpec = infiniteRepeatable(
      animation = tween(3000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "sway",
  )

  val growthAnim by animateFloatAsState(
    targetValue = stage.progress,
    animationSpec = tween(800, easing = FastOutSlowInEasing),
    label = "growth",
  )

  Canvas(modifier = modifier) {
    val centerX = size.width / 2
    val groundY = size.height - 20f
    val maxTrunkHeight = size.height * 0.6f
    val trunkHeight = maxTrunkHeight * growthAnim

    // Ground
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFF5D4037),
          Color(0xFF3E2723).copy(alpha = 0.5f),
          Color.Transparent,
        ),
        center = Offset(centerX, groundY),
        radius = 60f,
      ),
      topLeft = Offset(centerX - 60f, groundY - 10f),
      size = androidx.compose.ui.geometry.Size(120f, 20f),
    )

    if (stage == TreeGrowthStage.SEED) {
      // Seed
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF8B4513), Color(0xFF5D4037)),
        ),
        topLeft = Offset(centerX - 8f, groundY - 12f),
        size = androidx.compose.ui.geometry.Size(16f, 10f),
      )
      return@Canvas
    }

    // Trunk
    val trunkWidth = 8f + growthAnim * 12f
    val trunkPath = Path().apply {
      moveTo(centerX - trunkWidth / 2, groundY)
      lineTo(centerX - trunkWidth / 3 + sway * 0.3f, groundY - trunkHeight)
      lineTo(centerX + trunkWidth / 3 + sway * 0.3f, groundY - trunkHeight)
      lineTo(centerX + trunkWidth / 2, groundY)
      close()
    }
    drawPath(
      path = trunkPath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF5D4037), Color(0xFF8B4513)),
      ),
    )

    if (stage.ordinal < TreeGrowthStage.SAPLING.ordinal) return@Canvas

    // Branches and leaves
    val branchLevels = when {
      stage.ordinal >= TreeGrowthStage.FULL_TREE.ordinal -> 4
      stage.ordinal >= TreeGrowthStage.YOUNG_TREE.ordinal -> 3
      else -> 2
    }

    val leafClusters = mutableListOf<Pair<Offset, Float>>()

    for (level in 0 until branchLevels) {
      val branchY = groundY - trunkHeight * (0.4f + level * 0.18f)
      val branchLength = (40f + level * 15f) * growthAnim
      val branchAngle = 35f + level * 5f

      for (side in listOf(-1, 1)) {
        val angleRad = (branchAngle * side + sway * 2f) * PI.toFloat() / 180f
        val endX = centerX + sway * 0.5f + cos(angleRad) * branchLength * side
        val endY = branchY - sin(branchAngle * PI.toFloat() / 180f) * branchLength * 0.5f

        // Branch
        drawLine(
          color = Color(0xFF6D4C41),
          start = Offset(centerX + sway * 0.3f, branchY),
          end = Offset(endX, endY),
          strokeWidth = 4f - level * 0.5f,
          cap = StrokeCap.Round,
        )

        leafClusters.add(Offset(endX, endY) to (25f - level * 3f))
      }
    }

    // Top cluster
    leafClusters.add(Offset(centerX + sway, groundY - trunkHeight - 20f) to 35f)

    // Draw leaf clusters
    for ((pos, radius) in leafClusters) {
      // Outer glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF228B22).copy(alpha = 0.3f),
            Color.Transparent,
          ),
          center = pos,
          radius = radius * 1.5f,
        ),
        radius = radius * 1.5f,
        center = pos,
      )
      // Main foliage
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF32CD32),
            Color(0xFF228B22),
            Color(0xFF006400),
          ),
          center = pos,
          radius = radius,
        ),
        radius = radius,
        center = pos,
      )
    }
  }
}

@Composable
fun TreeAnimationArea(
  stage: TreeGrowthStage,
  particleSystem: TreeParticleSystem,
  modifier: Modifier = Modifier,
) {
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

  LaunchedEffect(stage) {
    if (stage == TreeGrowthStage.FULL_TREE) {
      particleSystem.spawnLeaves(200f, 50f, 12)
    }
  }

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
        if (canvasSize.height > 0) {
          particleSystem.update(deltaTime, totalTime, canvasSize.height)
        }
      }
    }
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    TreeCanvas(
      stage = stage,
      modifier = Modifier.fillMaxSize(),
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
      canvasSize = size
      particleSystem.draw(this)
    }

    Column(
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stage.name.replace("_", " "),
        style = TextStyle(
          color = Color(0xFF81C784),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
        ),
      )
    }
  }
}

@Composable
fun GrowthStageIndicator(currentStage: TreeGrowthStage, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TreeGrowthStage.entries.forEach { stage ->
      val isActive = stage.ordinal <= currentStage.ordinal
      Box(
        modifier = Modifier
          .size(if (stage == currentStage) 12.dp else 8.dp)
          .clip(CircleShape)
          .background(
            if (isActive) Color(0xFF4CAF50) else Color(0xFF2E2E2E),
          )
          .then(
            if (stage == currentStage) {
              Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            } else {
              Modifier
            },
          ),
      )
    }
  }
}

@Composable
fun TreeFeatureRow(title: String, unlocked: Boolean, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(if (unlocked) Color(0xFF4CAF50) else Color(0xFF333333)),
      contentAlignment = Alignment.Center,
    ) {
      if (unlocked) {
        Canvas(modifier = Modifier.size(10.dp)) {
          drawPath(
            path = Path().apply {
              moveTo(size.width * 0.15f, size.height * 0.5f)
              lineTo(size.width * 0.4f, size.height * 0.75f)
              lineTo(size.width * 0.85f, size.height * 0.25f)
            },
            color = Color.White,
            style = Stroke(2f, cap = StrokeCap.Round, join = StrokeJoin.Round),
          )
        }
      }
    }
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = title,
      style = TextStyle(
        color = if (unlocked) Color.White else Color(0xFF666666),
        fontSize = 14.sp,
        fontWeight = if (unlocked) FontWeight.Medium else FontWeight.Normal,
      ),
    )
  }
}

@Composable
fun GrowingTreePaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)
  var stage by remember { mutableStateOf(TreeGrowthStage.SEED) }
  val particleSystem = remember { TreeParticleSystem() }

  val features = listOf(
    "Unlimited Access" to TreeGrowthStage.SPROUT,
    "Offline Mode" to TreeGrowthStage.SPROUT,
    "Advanced Analytics" to TreeGrowthStage.SAPLING,
    "Priority Support" to TreeGrowthStage.YOUNG_TREE,
    "Exclusive Content" to TreeGrowthStage.FULL_TREE,
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF1B2631),
            Color(0xFF17202A),
            Color(0xFF0D1117),
          ),
        ),
      ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "GROW WITH US",
        style = TextStyle(
          color = Color(0xFF81C784),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 3.sp,
        ),
      )

      Text(
        text = "Unlock Premium",
        style = TextStyle(
          color = Color.White,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
        ),
      )

      Spacer(modifier = Modifier.height(12.dp))

      GrowthStageIndicator(currentStage = stage)

      Spacer(modifier = Modifier.height(16.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF1E272E)),
      ) {
        TreeAnimationArea(
          stage = stage,
          particleSystem = particleSystem,
          modifier = Modifier.fillMaxSize().padding(16.dp),
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1E1E1E))
          .padding(16.dp),
      ) {
        features.forEach { (title, unlockStage) ->
          TreeFeatureRow(
            title = title,
            unlocked = stage.ordinal >= unlockStage.ordinal,
          )
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "\$49.99/year",
          style = TextStyle(
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "7-day free trial",
          style = TextStyle(
            color = Color(0xFF81C784),
            fontSize = 14.sp,
          ),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          if (stage == TreeGrowthStage.FULL_TREE) {
            paywallState.purchase(PackageType.ANNUAL)
          } else if (stage.ordinal < TreeGrowthStage.entries.lastIndex) {
            stage = TreeGrowthStage.entries[stage.ordinal + 1]
          }
        },
        modifier = Modifier.fillMaxWidth().height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
        shape = RoundedCornerShape(12.dp),
      ) {
        Text(
          text = if (stage == TreeGrowthStage.FULL_TREE) "Subscribe Now" else "Grow Tree",
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
