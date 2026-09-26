# AGENTS.md

Repository notes for future sessions:

- This repo is a Java/Spring frontend plus Java checkoutservice variant of the Online Boutique demo.
- Coupon amounts are currency-invariant numeric values in the shopper's selected currency. Example: `SAVE10` must always discount `10` in the active currency (`$10`, `€10`, `¥10`, etc.), not a USD amount converted into that currency.
- The cart coupon preview in `src/frontend/src/main/java/hipstershop/frontend/web/CartController.java` must match the real order discount applied in `src/checkoutservice/src/main/java/hipstershop/CheckoutServiceImpl.java`.
