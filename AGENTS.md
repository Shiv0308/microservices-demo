# Repository Memory

- Coupon validation is split between the Spring frontend (`src/frontend/.../CheckoutController.java`) and Java checkoutservice (`src/checkoutservice/.../CheckoutServiceImpl.java`). Keep them aligned.
- Coupon eligibility must be based on product subtotal before shipping, and coupons should be rejected when subtotal is at or below the threshold.
- Do not auto-apply a default coupon when no coupon code was entered.
