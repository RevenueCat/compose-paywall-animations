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
import androidx.compose.ui.geometry.Size
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

private const val TAU = 2f * PI.toFloat()

// 3D Hexagonal atom structure
data class HexAtom(
  val x: Float,
  val y: Float,
  val z: Float, // For depth sorting and 3D effect
  val size: Float,
  val rotationSpeed: Float,
  val rotationPhase: Float,
  val pulsePhase: Float,
  val color: Color,
  val hasElectrons: Boolean,
  val electronCount: Int,
)

// Hexagonal grid node for molecular bonds
data class HexNode(
  val x: Float,
  val y: Float,
  val connections: List<Int>, // Indices of connected nodes
)

// Floating hexagonal particle
data class HexParticle(
  var x: Float,
  var y: Float,
  var z: Float,
  var rotationAngle: Float,
  val size: Float,
  val rotationSpeed: Float,
  var vx: Float,
  var vy: Float,
  var life: Float,
  val color: Color,
)

// Energy beam between atoms
data class EnergyBeam(
  val fromIndex: Int,
  val toIndex: Int,
  val pulsePhase: Float,
  val color: Color,
)

class HexAtomicSystem {
  private val hexAtoms = mutableListOf<HexAtom>()
  private val hexParticles = mutableListOf<HexParticle>()
  private val energyBeams = mutableListOf<EnergyBeam>()
  private val random = Random
  private var initialized = false

  private val atomColors = listOf(
    Color(0xFF00FFFF), // Cyan
    Color(0xFF00D4FF), // Light blue
    Color(0xFF7B68EE), // Purple
    Color(0xFFFF6B9D), // Pink
    Color(0xFF00FF88), // Green
  )

  fun initialize(width: Float, height: Float) {
    if (initialized) return
    initialized = true

    // Main central hexagonal atom (large)
    hexAtoms.add(
      HexAtom(
        x = width * 0.5f,
        y = height * 0.26f,
        z = 0f,
        size = 55f,
        rotationSpeed = 0.3f,
        rotationPhase = 0f,
        pulsePhase = 0f,
        color = atomColors[0],
        hasElectrons = true,
        electronCount = 6,
      ),
    )

    // Surrounding hexagonal atoms in 3D arrangement
    hexAtoms.add(
      HexAtom(
        x = width * 0.15f,
        y = height * 0.15f,
        z = -30f,
        size = 28f,
        rotationSpeed = -0.4f,
        rotationPhase = 0.5f,
        pulsePhase = 1f,
        color = atomColors[1],
        hasElectrons = true,
        electronCount = 3,
      ),
    )

    hexAtoms.add(
      HexAtom(
        x = width * 0.85f,
        y = height * 0.12f,
        z = -20f,
        size = 32f,
        rotationSpeed = 0.35f,
        rotationPhase = 1f,
        pulsePhase = 2f,
        color = atomColors[2],
        hasElectrons = true,
        electronCount = 4,
      ),
    )

    hexAtoms.add(
      HexAtom(
        x = width * 0.12f,
        y = height * 0.45f,
        z = -25f,
        size = 24f,
        rotationSpeed = -0.5f,
        rotationPhase = 2f,
        pulsePhase = 0.5f,
        color = atomColors[3],
        hasElectrons = true,
        electronCount = 2,
      ),
    )

    hexAtoms.add(
      HexAtom(
        x = width * 0.9f,
        y = height * 0.38f,
        z = -15f,
        size = 26f,
        rotationSpeed = 0.45f,
        rotationPhase = 1.5f,
        pulsePhase = 1.5f,
        color = atomColors[4],
        hasElectrons = true,
        electronCount = 3,
      ),
    )

    hexAtoms.add(
      HexAtom(
        x = width * 0.3f,
        y = height * 0.08f,
        z = -40f,
        size = 20f,
        rotationSpeed = 0.6f,
        rotationPhase = 0.3f,
        pulsePhase = 2.5f,
        color = atomColors[0],
        hasElectrons = false,
        electronCount = 0,
      ),
    )

    hexAtoms.add(
      HexAtom(
        x = width * 0.7f,
        y = height * 0.48f,
        z = -35f,
        size = 22f,
        rotationSpeed = -0.55f,
        rotationPhase = 2.5f,
        pulsePhase = 0.8f,
        color = atomColors[1],
        hasElectrons = false,
        electronCount = 0,
      ),
    )

    // Energy beams connecting atoms
    energyBeams.add(EnergyBeam(0, 1, 0f, Color(0xFF00FFFF)))
    energyBeams.add(EnergyBeam(0, 2, 0.5f, Color(0xFF7B68EE)))
    energyBeams.add(EnergyBeam(0, 3, 1f, Color(0xFFFF6B9D)))
    energyBeams.add(EnergyBeam(0, 4, 1.5f, Color(0xFF00FF88)))
    energyBeams.add(EnergyBeam(1, 5, 2f, Color(0xFF00D4FF)))
    energyBeams.add(EnergyBeam(2, 6, 2.5f, Color(0xFF7B68EE)))
  }

  fun update(deltaTime: Float, width: Float, height: Float) {
    // Spawn hexagonal particles
    if (random.nextFloat() < 0.12f && hexParticles.size < 25) {
      val atom = hexAtoms[random.nextInt(hexAtoms.size)]
      val angle = random.nextFloat() * TAU
      val speed = random.nextFloat() * 40f + 20f

      hexParticles.add(
        HexParticle(
          x = atom.x,
          y = atom.y,
          z = atom.z + random.nextFloat() * 20f - 10f,
          rotationAngle = random.nextFloat() * TAU,
          size = random.nextFloat() * 8f + 4f,
          rotationSpeed = (random.nextFloat() - 0.5f) * 4f,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          life = 1f,
          color = atomColors[random.nextInt(atomColors.size)],
        ),
      )
    }

    // Update particles
    val iterator = hexParticles.iterator()
    while (iterator.hasNext()) {
      val particle = iterator.next()
      particle.x += particle.vx * deltaTime
      particle.y += particle.vy * deltaTime
      particle.rotationAngle += particle.rotationSpeed * deltaTime
      particle.life -= deltaTime * 0.35f

      if (particle.life <= 0f) {
        iterator.remove()
      }
    }
  }

  fun draw(drawScope: DrawScope, totalTime: Float) {
    // Sort atoms by z for proper depth rendering
    val sortedAtoms = hexAtoms.sortedBy { it.z }

    // Draw background hex grid
    drawHexGrid(drawScope, totalTime)

    // Draw energy beams between atoms
    for (beam in energyBeams) {
      if (beam.fromIndex < hexAtoms.size && beam.toIndex < hexAtoms.size) {
        drawEnergyBeam(
          drawScope,
          hexAtoms[beam.fromIndex],
          hexAtoms[beam.toIndex],
          beam,
          totalTime,
        )
      }
    }

    // Draw hex particles
    for (particle in hexParticles) {
      draw3DHexParticle(drawScope, particle, totalTime)
    }

    // Draw atoms back to front
    for (atom in sortedAtoms) {
      draw3DHexAtom(drawScope, atom, totalTime)
    }
  }

  private fun drawHexGrid(drawScope: DrawScope, time: Float) {
    val gridSize = 60f
    val rows = 12
    val cols = 8

    for (row in 0 until rows) {
      for (col in 0 until cols) {
        val offsetX = if (row % 2 == 0) 0f else gridSize * 0.5f
        val x = col * gridSize + offsetX
        val y = row * gridSize * 0.866f

        val distanceFromCenter = sqrt((x - 200f).pow(2) + (y - 200f).pow(2))
        val pulse = sin(time * 1.5f - distanceFromCenter * 0.01f) * 0.5f + 0.5f
        val alpha = 0.03f * pulse

        drawScope.drawHexagonOutline(
          cx = x,
          cy = y,
          size = 15f,
          rotation = time * 0.1f,
          color = Color(0xFF00D4FF).copy(alpha = alpha),
          strokeWidth = 0.5f,
        )
      }
    }
  }

  private fun drawEnergyBeam(
    drawScope: DrawScope,
    from: HexAtom,
    to: HexAtom,
    beam: EnergyBeam,
    time: Float,
  ) {
    val pulsePosition = (time * 0.5f + beam.pulsePhase) % 1f

    // Draw beam line
    val beamAlpha = 0.15f + sin(time * 3f + beam.pulsePhase) * 0.1f
    drawScope.drawLine(
      color = beam.color.copy(alpha = beamAlpha),
      start = Offset(from.x, from.y),
      end = Offset(to.x, to.y),
      strokeWidth = 1.5f,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), time * 30f),
    )

    // Draw energy pulse traveling along beam
    val pulseX = from.x + (to.x - from.x) * pulsePosition
    val pulseY = from.y + (to.y - from.y) * pulsePosition

    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          beam.color.copy(alpha = 0.8f),
          beam.color.copy(alpha = 0.3f),
          Color.Transparent,
        ),
        center = Offset(pulseX, pulseY),
        radius = 8f,
      ),
      radius = 8f,
      center = Offset(pulseX, pulseY),
    )
  }

  private fun draw3DHexParticle(drawScope: DrawScope, particle: HexParticle, time: Float) {
    val depthScale = 1f - (particle.z / -100f) * 0.3f
    val size = particle.size * depthScale * particle.life

    drawScope.draw3DHexagon(
      cx = particle.x,
      cy = particle.y,
      size = size,
      rotation = particle.rotationAngle,
      depth = 3f * particle.life,
      color = particle.color.copy(alpha = particle.life * 0.7f),
      time = time,
    )
  }

  private fun draw3DHexAtom(drawScope: DrawScope, atom: HexAtom, time: Float) {
    val rotation = time * atom.rotationSpeed + atom.rotationPhase
    val pulse = 1f + sin(time * 2f + atom.pulsePhase) * 0.08f
    val depthScale = 1f - (atom.z / -100f) * 0.2f
    val size = atom.size * pulse * depthScale

    // Draw outer glow
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          atom.color.copy(alpha = 0.3f * depthScale),
          atom.color.copy(alpha = 0.1f * depthScale),
          Color.Transparent,
        ),
        center = Offset(atom.x, atom.y),
        radius = size * 1.8f,
      ),
      radius = size * 1.8f,
      center = Offset(atom.x, atom.y),
    )

    // Draw electron orbits as hexagonal paths
    if (atom.hasElectrons) {
      for (orbitIndex in 0 until minOf(atom.electronCount, 3)) {
        val orbitSize = size * (1.3f + orbitIndex * 0.35f)
        val orbitRotation = rotation * (1f - orbitIndex * 0.2f) + orbitIndex * 0.5f

        drawScope.drawHexagonOutline(
          cx = atom.x,
          cy = atom.y,
          size = orbitSize,
          rotation = orbitRotation,
          color = atom.color.copy(alpha = 0.2f * depthScale),
          strokeWidth = 1.5f * depthScale,
        )

        // Draw electrons on orbit
        val electronsOnOrbit = if (orbitIndex ==
          0
        ) {
          minOf(2, atom.electronCount)
        } else {
          minOf(3, atom.electronCount - 2)
        }
        for (e in 0 until electronsOnOrbit) {
          val electronAngle = orbitRotation + (e.toFloat() / electronsOnOrbit) * TAU
          val ex = atom.x + cos(electronAngle) * orbitSize * 0.9f
          val ey = atom.y + sin(electronAngle) * orbitSize * 0.9f

          // Electron trail
          for (trail in 1..4) {
            val trailAngle = electronAngle - trail * 0.12f
            val tx = atom.x + cos(trailAngle) * orbitSize * 0.9f
            val ty = atom.y + sin(trailAngle) * orbitSize * 0.9f
            val trailAlpha = (1f - trail * 0.22f) * 0.5f * depthScale

            drawScope.drawCircle(
              color = atom.color.copy(alpha = trailAlpha),
              radius = (3f - trail * 0.4f) * depthScale,
              center = Offset(tx, ty),
            )
          }

          // Electron
          drawScope.drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                Color.White,
                atom.color,
              ),
              center = Offset(ex - 1f, ey - 1f),
              radius = 4f * depthScale,
            ),
            radius = 4f * depthScale,
            center = Offset(ex, ey),
          )
        }
      }
    }

    // Draw main 3D hexagon
    drawScope.draw3DHexagon(
      cx = atom.x,
      cy = atom.y,
      size = size,
      rotation = rotation,
      depth = 8f * depthScale,
      color = atom.color,
      time = time,
    )

    // Draw inner hexagon core
    drawScope.draw3DHexagon(
      cx = atom.x,
      cy = atom.y,
      size = size * 0.5f,
      rotation = -rotation * 1.5f,
      depth = 4f * depthScale,
      color = Color.White.copy(alpha = 0.8f),
      time = time,
    )

    // Draw nucleus indicator
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White,
          atom.color,
          atom.color.copy(alpha = 0.5f),
        ),
        center = Offset(atom.x - 2f, atom.y - 2f),
        radius = size * 0.2f,
      ),
      radius = size * 0.2f,
      center = Offset(atom.x, atom.y),
    )
  }
}

// Extension function to draw a hexagon outline
private fun DrawScope.drawHexagonOutline(
  cx: Float,
  cy: Float,
  size: Float,
  rotation: Float,
  color: Color,
  strokeWidth: Float,
) {
  val path = Path()
  for (i in 0 until 6) {
    val angle = rotation + i * TAU / 6 - TAU / 12
    val x = cx + cos(angle) * size
    val y = cy + sin(angle) * size
    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
  }
  path.close()
  drawPath(path, color, style = Stroke(width = strokeWidth))
}

// Extension function to draw a 3D hexagon with shading
private fun DrawScope.draw3DHexagon(
  cx: Float,
  cy: Float,
  size: Float,
  rotation: Float,
  depth: Float,
  color: Color,
  time: Float,
) {
  // Calculate hexagon points
  val topPoints = mutableListOf<Offset>()
  val bottomPoints = mutableListOf<Offset>()

  for (i in 0 until 6) {
    val angle = rotation + i * TAU / 6 - TAU / 12
    topPoints.add(Offset(cx + cos(angle) * size, cy + sin(angle) * size))
    bottomPoints.add(Offset(cx + cos(angle) * size, cy + sin(angle) * size + depth))
  }

  // Draw side faces (3D depth effect)
  for (i in 0 until 6) {
    val next = (i + 1) % 6
    val sidePath = Path().apply {
      moveTo(topPoints[i].x, topPoints[i].y)
      lineTo(topPoints[next].x, topPoints[next].y)
      lineTo(bottomPoints[next].x, bottomPoints[next].y)
      lineTo(bottomPoints[i].x, bottomPoints[i].y)
      close()
    }

    // Shade based on face orientation
    val faceAngle = rotation + i * TAU / 6
    val shadeFactor = (cos(faceAngle) * 0.3f + 0.7f).coerceIn(0.4f, 1f)
    val sideColor = color.copy(alpha = 0.5f * shadeFactor)

    drawPath(sidePath, sideColor)
    drawPath(sidePath, color.copy(alpha = 0.3f), style = Stroke(width = 1f))
  }

  // Draw top face with gradient
  val topPath = Path().apply {
    for (i in 0 until 6) {
      if (i == 0) {
        moveTo(topPoints[i].x, topPoints[i].y)
      } else {
        lineTo(topPoints[i].x, topPoints[i].y)
      }
    }
    close()
  }

  // Top face gradient (3D lighting effect)
  drawPath(
    topPath,
    brush = Brush.radialGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.4f),
        color.copy(alpha = 0.8f),
        color.copy(alpha = 0.6f),
      ),
      center = Offset(cx - size * 0.2f, cy - size * 0.2f),
      radius = size * 1.2f,
    ),
  )

  // Top face outline
  drawPath(
    topPath,
    color = color,
    style = Stroke(width = 2f),
  )

  // Animated inner glow
  val glowPulse = sin(time * 3f) * 0.2f + 0.8f
  val innerPath = Path().apply {
    for (i in 0 until 6) {
      val angle = rotation + i * TAU / 6 - TAU / 12
      val x = cx + cos(angle) * size * 0.6f
      val y = cy + sin(angle) * size * 0.6f
      if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
  }

  drawPath(
    innerPath,
    color = Color.White.copy(alpha = 0.2f * glowPulse),
    style = Stroke(width = 1f),
  )
}

@Composable
fun AtomicBackground(modifier: Modifier = Modifier) {
  val system = remember { HexAtomicSystem() }
  var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
  var totalTime by remember { mutableFloatStateOf(0f) }
  var canvasSize by remember { mutableStateOf(Size.Zero) }

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
fun AtomicFeatureItem(title: String, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Electron-like indicator
    Box(
      modifier = Modifier.size(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0xFF00FFFF).copy(alpha = 0.2f),
                Color.Transparent,
              ),
            ),
          ),
      )
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                Color.White,
                Color(0xFF00FFFF),
              ),
            ),
          ),
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      style = TextStyle(
        color = Color(0xFFE0F7FA),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}

@Composable
fun AtomicPaywallScreen(onDismiss: () -> Unit = {}) {
  val paywallState = rememberPaywallState(onPurchaseSuccess = onDismiss)

  val features = listOf(
    "Quantum State Analysis",
    "Molecular Bonding Simulation",
    "Nuclear Reaction Modeling",
    "Wave Function Visualization",
    "Spectral Line Calculation",
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF050510),
            Color(0xFF0A0A20),
            Color(0xFF0D1025),
            Color(0xFF050510),
          ),
        ),
      ),
  ) {
    AtomicBackground()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
        .padding(top = 48.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(100.dp))

      Text(
        text = "QUANTUM MECHANICS",
        style = TextStyle(
          color = Color(0xFF00FFFF).copy(alpha = 0.8f),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 4.sp,
        ),
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Atomic Pro",
        style = TextStyle(
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFF00FFFF),
              Color(0xFF00D4FF),
              Color(0xFF7B68EE),
            ),
          ),
        ),
      )

      Text(
        text = "Explore the Quantum Realm",
        style = TextStyle(
          color = Color(0xFFE0F7FA).copy(alpha = 0.6f),
          fontSize = 13.sp,
        ),
      )

      Spacer(modifier = Modifier.weight(0.3f))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0A1525).copy(alpha = 0.8f))
          .padding(16.dp),
      ) {
        features.forEach { feature ->
          AtomicFeatureItem(title = feature)
        }
      }

      Spacer(modifier = Modifier.weight(0.5f))

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "$59.99/year",
          style = TextStyle(
            color = Color(0xFFE0F7FA),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
          ),
        )
        Text(
          text = "Unlimited quantum simulations",
          style = TextStyle(
            color = Color(0xFF00FFFF),
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
              brush = Brush.linearGradient(
                colors = listOf(
                  Color(0xFF00FFFF),
                  Color(0xFF00D4FF),
                  Color(0xFF7B68EE),
                ),
              ),
              shape = RoundedCornerShape(14.dp),
            ),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "Initialize Quantum State",
            style = TextStyle(
              color = Color(0xFF050510),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Restore Purchases",
        style = TextStyle(
          color = Color(0xFFE0F7FA).copy(alpha = 0.5f),
          fontSize = 13.sp,
        ),
        modifier = Modifier.clickable { paywallState.restorePurchases() },
      )
    }
  }
}
