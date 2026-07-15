# Repository memory

- Frontend coupon behavior lives in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java`, `CheckoutController.java`, and `src/frontend/src/main/resources/templates/cart.html` / `order.html`.
- Cart-page coupon validation is expected to happen before checkout and uses the product subtotal before shipping as the eligibility threshold.
- Applied coupon details on the cart page are rendered from `coupon_code_used` and `coupon_discount`; the checkout form only submits a validated applied code through a hidden `coupon_code` field.
- Coupon definitions are centralized in `src/frontend/src/main/java/hipstershop/frontend/config/ShopProperties.java`.
