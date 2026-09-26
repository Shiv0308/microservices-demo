# Repository Memory

- Coupon amounts are nominal in the shopper's selected currency. `SAVE10` must display and charge as 10 units of the active currency, not as a USD amount converted through `currencyservice`.
- The coupon rule is enforced in both `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` for cart preview and `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java` for the actual order charge.
