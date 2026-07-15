# Repository memory

- Coupon definitions live in `src/frontend/src/main/java/hipstershop/frontend/config/ShopProperties.java` via `COUPON_DEFS` and `COUPON_ORDER`.
- Cart-page coupon application is handled in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` and rendered in `src/frontend/src/main/resources/templates/cart.html`.
- Coupon eligibility should use the product subtotal before shipping, in the shopper's selected currency.
- `CheckoutController` should keep cart and checkout coupon validation consistent.
- `CheckoutServiceImpl` should only apply a discount when the client explicitly submits a valid coupon code.
