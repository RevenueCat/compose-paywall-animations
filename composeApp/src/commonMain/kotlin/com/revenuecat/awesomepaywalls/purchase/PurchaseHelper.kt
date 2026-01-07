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

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.result.awaitCustomerInfoResult
import com.revenuecat.purchases.kmp.result.awaitOfferingsResult
import com.revenuecat.purchases.kmp.result.awaitPurchaseResult
import com.revenuecat.purchases.kmp.result.awaitRestoreResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PurchaseHelper {

  // Premium entitlement identifier - configure this in RevenueCat dashboard
  private const val PREMIUM_ENTITLEMENT = "premium"

  private val _currentOffering = MutableStateFlow<OfferingInfo?>(null)
  val currentOffering: StateFlow<OfferingInfo?> = _currentOffering.asStateFlow()

  private val _isPremium = MutableStateFlow(false)
  val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private var cachedPackages: Map<PackageType, Package> = emptyMap()

  /**
   * Fetch available offerings from RevenueCat
   */
  suspend fun fetchOfferings(): Result<OfferingInfo> {
    _isLoading.value = true
    return try {
      val offeringsResult = Purchases.sharedInstance.awaitOfferingsResult()

      offeringsResult.fold(
        onSuccess = { offerings ->
          val current = offerings.current

          if (current != null) {
            val packages = mutableListOf<PackageInfo>()
            val packageMap = mutableMapOf<PackageType, Package>()

            // Map RevenueCat packages to our PackageType
            current.lifetime?.let { pkg ->
              packages.add(
                PackageInfo(
                  identifier = pkg.identifier,
                  packageType = PackageType.LIFETIME,
                  localizedPrice = pkg.storeProduct.price.formatted,
                ),
              )
              packageMap[PackageType.LIFETIME] = pkg
            }

            current.annual?.let { pkg ->
              packages.add(
                PackageInfo(
                  identifier = pkg.identifier,
                  packageType = PackageType.ANNUAL,
                  localizedPrice = pkg.storeProduct.price.formatted,
                ),
              )
              packageMap[PackageType.ANNUAL] = pkg
            }

            current.monthly?.let { pkg ->
              packages.add(
                PackageInfo(
                  identifier = pkg.identifier,
                  packageType = PackageType.MONTHLY,
                  localizedPrice = pkg.storeProduct.price.formatted,
                ),
              )
              packageMap[PackageType.MONTHLY] = pkg
            }

            cachedPackages = packageMap

            val offeringInfo = OfferingInfo(
              identifier = current.identifier,
              packages = packages,
            )
            _currentOffering.value = offeringInfo
            Result.success(offeringInfo)
          } else {
            Result.failure(Exception("No current offering available"))
          }
        },
        onFailure = { error ->
          Result.failure(error)
        },
      )
    } finally {
      _isLoading.value = false
    }
  }

  /**
   * Purchase a specific package type
   */
  suspend fun purchase(packageType: PackageType): PurchaseResult {
    val pkg = cachedPackages[packageType]
      ?: return PurchaseResult.Error("Product not found. Please refresh offerings.")

    _isLoading.value = true
    return try {
      val result = Purchases.sharedInstance.awaitPurchaseResult(pkg)

      result.fold(
        onSuccess = { purchaseResult ->
          val customerInfo = purchaseResult.customerInfo
          val isPremiumNow = customerInfo.entitlements.active.containsKey(PREMIUM_ENTITLEMENT)
          _isPremium.value = isPremiumNow

          PurchaseResult.Success(
            CustomerInfoWrapper(
              activeEntitlements = customerInfo.entitlements.active.keys,
              isPremium = isPremiumNow,
            ),
          )
        },
        onFailure = { error ->
          val message = error.message ?: "Purchase failed"
          if (message.contains("cancelled", ignoreCase = true) ||
            message.contains("canceled", ignoreCase = true)
          ) {
            PurchaseResult.Cancelled
          } else {
            PurchaseResult.Error(message = message)
          }
        },
      )
    } finally {
      _isLoading.value = false
    }
  }

  /**
   * Restore previous purchases
   */
  suspend fun restorePurchases(): PurchaseResult {
    _isLoading.value = true
    return try {
      val result = Purchases.sharedInstance.awaitRestoreResult()

      result.fold(
        onSuccess = { customerInfo ->
          val isPremiumNow = customerInfo.entitlements.active.containsKey(PREMIUM_ENTITLEMENT)
          _isPremium.value = isPremiumNow

          PurchaseResult.Success(
            CustomerInfoWrapper(
              activeEntitlements = customerInfo.entitlements.active.keys,
              isPremium = isPremiumNow,
            ),
          )
        },
        onFailure = { error ->
          PurchaseResult.Error(message = error.message ?: "Restore failed")
        },
      )
    } finally {
      _isLoading.value = false
    }
  }

  /**
   * Check current subscription status
   */
  suspend fun checkSubscriptionStatus(): Boolean = try {
    val result = Purchases.sharedInstance.awaitCustomerInfoResult()

    result.fold(
      onSuccess = { customerInfo ->
        val isPremiumNow = customerInfo.entitlements.active.containsKey(PREMIUM_ENTITLEMENT)
        _isPremium.value = isPremiumNow
        isPremiumNow
      },
      onFailure = {
        false
      },
    )
  } catch (e: Exception) {
    false
  }
}
