# Repository Memory

- Checkout coupon behavior: `checkoutservice` must only apply a discount when `PlaceOrderRequest.coupon_index` is explicitly present. Treating proto3's default zero as an implicit coupon applies `SAVE10` to coupon-less orders and can make confirmation totals lower than the cart total.
