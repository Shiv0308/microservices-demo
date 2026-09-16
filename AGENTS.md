# Repository Memory

- Checkout coupon application lives in `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`.
- `PlaceOrderRequest.coupon_index` is presence-sensitive: checkout should only apply a discount when the field is explicitly set and the index is within bounds.
- `src/checkoutservice` has no local Gradle wrapper; to verify compilation in this workspace, use the existing wrapper from `src/adservice` with `sh ./gradlew -p ../checkoutservice compileJava` while in `src/adservice`.
