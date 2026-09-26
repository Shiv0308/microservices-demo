# AGENTS.md

## Repository notes
- This repository is a microservices demo with a Java frontend and a Java checkoutservice.
- Coupon application during checkout is controlled by `PlaceOrderRequest.coupon_index` presence, not by `coupon_code` alone.
- `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java` must only apply a discount when `req.hasCouponIndex()` is true; otherwise orders without an entered coupon can be undercharged by the first configured coupon amount.
