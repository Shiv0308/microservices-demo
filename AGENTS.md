# AGENTS.md

## Repository notes
- Main UI checkout flow lives in `src/frontend`.
- Order pricing and coupon application during checkout live in `src/checkoutservice`.
- When analyzing automation failures in this repo, compare cart totals from the frontend with charged totals from checkoutservice before assuming a locator issue.

## Known gotcha
- `PlaceOrderRequest.coupon_code` must be treated as opt-in. If checkoutservice defaults an empty coupon to a real code like `SAVE10`, the order total becomes lower than the cart total even though the shopper never entered a coupon.
