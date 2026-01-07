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

sealed class PurchaseResult {
  data class Success(val customerInfo: CustomerInfoWrapper) : PurchaseResult()

  data class Error(val message: String, val code: Int? = null) : PurchaseResult()

  data object Cancelled : PurchaseResult()
}

data class CustomerInfoWrapper(val activeEntitlements: Set<String>, val isPremium: Boolean)

data class PackageInfo(
  val identifier: String,
  val packageType: PackageType,
  val localizedPrice: String,
  val pricePerMonth: String? = null,
)

data class OfferingInfo(val identifier: String, val packages: List<PackageInfo>)
