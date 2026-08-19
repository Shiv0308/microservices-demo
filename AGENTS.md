# Repository Memory

- Coupon tier values in this storefront are expected to behave as fixed face values in the shopper's selected currency. Example: `SAVE100` should display and charge `100` EUR for EUR orders, not a USD amount converted into EUR.
- The cart coupon preview in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` and the real order discount in `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java` must stay aligned, or cart and order-confirmation totals will diverge.
