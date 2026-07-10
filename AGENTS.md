# Repository Memory

## Coupon and currency behavior
- Coupon amounts in `src/checkoutservice/main.go` are fixed amounts in the shopper's selected currency, not USD amounts that should be converted through `currencyservice`.
- Minimum coupon thresholds are also enforced as fixed amounts in the shopper's selected currency.
- `fixedAmount(currencyCode, units)` is the helper used to build coupon discount and threshold values without currency conversion.

## Frontend order confirmation accessibility
- Order confirmation amount cells in `src/frontend/templates/order.html` should expose amount-specific `aria-label` values. Some UI checks assert on attributes rather than visible text, so totals/discounts/shipping should remain discoverable in attributes as well as page text.
