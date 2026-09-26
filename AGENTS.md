# Repository Memory

- `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`: only apply a coupon discount when `PlaceOrderRequest.hasCouponIndex()` is true. Treating a missing `coupon_index` as index `0` silently applies `SAVE10` and makes the order total lower than the cart total.
