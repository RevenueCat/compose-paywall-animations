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
package com.revenuecat.awesomepaywalls.purchase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PaywallState(
  private val scope: CoroutineScope,
  private val onPurchaseSuccess: () -> Unit = {},
  private val onPurchaseError: (String) -> Unit = {},
  private val onPurchaseCancelled: () -> Unit = {},
) {
  var offering by mutableStateOf<OfferingInfo?>(null)
    private set

  var isLoading by mutableStateOf(false)
    private set

  var errorMessage by mutableStateOf<String?>(null)
    private set

  var selectedPackage by mutableStateOf(PackageType.ANNUAL)

  init {
    loadOfferings()
  }

  fun loadOfferings() {
    scope.launch {
      isLoading = true
      errorMessage = null

      PurchaseHelper.fetchOfferings()
        .onSuccess { offering = it }
        .onFailure { errorMessage = it.message }

      isLoading = false
    }
  }

  fun purchase(packageType: PackageType = selectedPackage) {
    scope.launch {
      isLoading = true
      errorMessage = null

      when (val result = PurchaseHelper.purchase(packageType)) {
        is PurchaseResult.Success -> onPurchaseSuccess()
        is PurchaseResult.Error -> {
          errorMessage = result.message
          onPurchaseError(result.message)
        }
        is PurchaseResult.Cancelled -> onPurchaseCancelled()
      }

      isLoading = false
    }
  }

  fun restorePurchases() {
    scope.launch {
      isLoading = true
      errorMessage = null

      when (val result = PurchaseHelper.restorePurchases()) {
        is PurchaseResult.Success -> onPurchaseSuccess()
        is PurchaseResult.Error -> {
          errorMessage = result.message
          onPurchaseError(result.message)
        }
        is PurchaseResult.Cancelled -> { /* Not applicable for restore */ }
      }

      isLoading = false
    }
  }

  fun clearError() {
    errorMessage = null
  }
}

@Composable
fun rememberPaywallState(
  onPurchaseSuccess: () -> Unit = {},
  onPurchaseError: (String) -> Unit = {},
  onPurchaseCancelled: () -> Unit = {},
): PaywallState {
  val scope = rememberCoroutineScope()
  return remember {
    PaywallState(
      scope = scope,
      onPurchaseSuccess = onPurchaseSuccess,
      onPurchaseError = onPurchaseError,
      onPurchaseCancelled = onPurchaseCancelled,
    )
  }
}
