# Repository memory

- This repository is a Java/Spring frontend plus multiple microservices based on the Online Boutique sample.
- Coupon checkout flow:
  - Frontend checkout entrypoint: `src/frontend/src/main/java/hipstershop/frontend/web/CheckoutController.java`
  - Cart page model/setup: `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java`
  - Order confirmation coupon rendering: `src/frontend/src/main/resources/templates/order.html`
  - Coupon charging logic: `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`
- Important coupon/currency rule: the storefront and automated tests treat `SAVE10` / `SAVE50` / `SAVE100` as face-value discounts in the shopper's currently selected currency. Example: when currency is EUR, `SAVE50` should render and apply as `€50.00`, not as a USD amount converted through `currencyservice`.
