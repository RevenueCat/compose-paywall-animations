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
package com.revenuecat.awesomepaywalls

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revenuecat.awesomepaywalls.paywalls.AtomicPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.BiblePaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.ChristmasPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.DayNightPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.FirefliesPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.GeometryPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.GrowingTreePaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.NewYearPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.NewYearsEveFireworksPaywall
import com.revenuecat.awesomepaywalls.paywalls.PremiumPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.SakuraPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.SummerPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.SynthwavePaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.UnderwaterPaywallScreen
import com.revenuecat.awesomepaywalls.paywalls.UniversePaywallScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  var selectedPaywall by remember { mutableStateOf<PaywallDemo?>(null) }

  MaterialTheme {
    BackHandler(enabled = selectedPaywall != null) {
      selectedPaywall = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedContent(
        targetState = selectedPaywall,
        transitionSpec = {
          if (targetState != null) {
            slideInHorizontally { it } + fadeIn() togetherWith
              slideOutHorizontally { -it } + fadeOut()
          } else {
            slideInHorizontally { -it } + fadeIn() togetherWith
              slideOutHorizontally { it } + fadeOut()
          }
        },
        label = "navigation",
      ) { paywall ->
        when (paywall) {
          null -> MainScreen(
            onPaywallSelected = { selectedPaywall = it },
          )
          PaywallDemo.CHRISTMAS -> ChristmasPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.NEW_YEAR -> NewYearPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.NEW_YEARS_EVE_FIREWORKS -> NewYearsEveFireworksPaywall(onDismiss = {
            selectedPaywall =
              null
          })
          PaywallDemo.SUMMER -> SummerPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.PREMIUM -> PremiumPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.GROWING_TREE -> GrowingTreePaywallScreen(onDismiss = {
            selectedPaywall = null
          })
          PaywallDemo.UNIVERSE -> UniversePaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.DAY_NIGHT -> DayNightPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.UNDERWATER -> UnderwaterPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.SYNTHWAVE -> SynthwavePaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.SAKURA -> SakuraPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.FIREFLIES -> FirefliesPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.GEOMETRY -> GeometryPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.ATOMIC -> AtomicPaywallScreen(onDismiss = { selectedPaywall = null })
          PaywallDemo.BIBLE -> BiblePaywallScreen(onDismiss = { selectedPaywall = null })
        }
      }

      if (selectedPaywall != null) {
        Box(
          modifier = Modifier
            .padding(16.dp)
            .padding(top = 24.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { selectedPaywall = null }
            .align(Alignment.TopStart),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "<",
            style = TextStyle(
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
            ),
          )
        }
      }
    }
  }
}
