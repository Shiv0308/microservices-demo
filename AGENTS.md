# Repository Memory

- Coupon business rule: SAVE10/SAVE50/SAVE100 are fixed nominal discounts in the shopper's currently selected currency. Do not currency-convert the coupon face value from USD before showing or charging the discount.
- Frontend cart preview and checkoutservice order calculation must stay aligned on coupon discount semantics to avoid mismatched cart vs. order totals.
