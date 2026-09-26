# Repository Memory

- Coupon business rule: SAVE10, SAVE50, and SAVE100 are flat nominal discounts in the shopper's selected currency. Example: SAVE10 means €10 in EUR and £10 in GBP.
- Keep cart preview logic in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` aligned with actual order application in `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java` so the cart page and charged order show the same discount.
