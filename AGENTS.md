# Repository Memory

- Coupon face values like `SAVE10` must remain fixed nominal amounts in the shopper's selected currency in both cart preview and checkout; do not convert them from USD.
- Coupon behavior is implemented in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` and `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`.
