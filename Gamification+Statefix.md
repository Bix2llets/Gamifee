# State & Lifecycle Fixes Plan

## Bug 1 — Fix `isChosen` serialization
**File:** `data/CartItem.kt`

Add `isChosen` to `serializeCartItem`:
```kotlin
obj.put("isChosen", item.isChosen)
```

Add `isChosen` to `deserializeCartItem`:
```kotlin
isChosen = obj.optBoolean("isChosen", false)
```

## Bug 2 — Persist `isChosen` toggle
**File:** `ui/screen/CartScreen.kt` (inside `onToggleChosen` lambda, ~line 112)
Add `saveCartItem(cartFileName, cartList)` after the map operation so the file reflects the toggled selection state immediately.

## Bug 3 — Survive rotation (transient UI state)
Replace `remember` with `rememberSaveable` in these locations:

| File | State variables |
|---|---|
| `OrdersScreen.kt` | `selectedTabIndex` |
| `ProductDetail.kt` | `selectedShot`, `selectedTemperature`, `selectedSize`, `selectedIce`, `countSelectAmount` |
| `CartScreen.kt` | `showOrderConfirm`, `address` (in CheckoutPanel) |
| `CartPreviewButton.kt` | `showSheet` |
| `OrderCard.kt` | `expanded`, `showCompleteDialog`, `offsetX`, `showDeleteDialog` |

## Bug 4 (Optional) — Memoize derived state
**File:** `CartScreen.kt` line 68
Wrap `selectedTotal` in `remember(cartList) { ... }` to avoid recomputation on unrelated recompositions.

---

# Gamification Plan — Points + Discounts + Coupons

## New Data Models

### `data/PointsTransaction.kt`
```kotlin
package com.example.midtermproject_24125072.data

data class PointsTransaction(
    val id: Int,
    val amount: Int,
    val reason: String,
    val timestamp: ZonedDateTime
)
```

With `loadPointsLedger(fileName)` and `savePointsLedger(fileName, data)` helpers (same JSON pattern as CartItem/OrderItem).

### `data/Coupon.kt`
```kotlin
package com.example.midtermproject_24125072.data

data class Coupon(
    val code: String,
    val discountAmount: Double,
    val minSpend: Double,
    val isUsed: Boolean = false,
    val expiryDate: ZonedDateTime
)
```

With `loadCoupons(fileName)` and `saveCoupons(fileName, data)` helpers. Seed with a few test coupons on first launch.

## UI Changes

### `ui/screen/RewardsScreen.kt` — Rewrite
- Points balance at top (large text)
- "Redeem Points" section: slider to convert N points → dollar discount, confirm button
- Available coupons list (code, discount, expiry, min spend)
- Points history list (recent transactions)
- Already has `NavHostController` parameter — keep that

### `ui/screen/CartScreen.kt` — Expand CheckoutPanel
Add to the bottom (between address field and CheckoutButton):
- "Use Points" toggle + amount display (e.g., "100 pts = $1.00 off")
- `OutlinedTextField` for coupon code + "Apply" button
- Pass discount amount to `onConfirmation` so it's reflected in the order total

### `ui/screen/OrderSuccessScreen.kt` — Update
- Show "You earned X points from this order!" message
- Show applied discount if any

## Business Logic

| Rule | Implementation |
|---|---|
| **Earning** | `floor(orderTotal * 10)` points awarded on order creation |
| **Conversion** | 100 pts = $1.00 off; user picks amount in RewardsScreen |
| **Coupon application** | Validate code against loaded coupons, check `!isUsed && !expired && total >= minSpend` |
| **One-time coupons** | Mark `isUsed = true` and save after successful use |

## Files Summary

| Action | File |
|---|---|
| **Create** | `data/PointsTransaction.kt` |
| **Create** | `data/Coupon.kt` |
| **Rewrite** | `ui/screen/RewardsScreen.kt` |
| **Expand** | `ui/screen/CartScreen.kt` (CheckoutPanel) |
| **Update** | `ui/screen/OrderSuccessScreen.kt` |
