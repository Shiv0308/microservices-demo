# Repository Memory

- Checkout coupon application lives in `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`.
- `PlaceOrderRequest.coupon_index` is presence-sensitive: only apply a discount when the field is explicitly set and in range.
- For the Bamboo Glass Jar flow, the expected cart total is `$11.00`; an unexpected `$1.00` total indicates an unintended `SAVE10` discount was applied.
- `src/checkoutservice` builds with Gradle; `compileJava` succeeds with JDK 21 using Gradle 8.10.2.
