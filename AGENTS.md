# AGENTS.md

## Repository notes

- This repo is the Google Cloud Online Boutique / Hipster Shop microservices app.
- Frontend is in `src/frontend` and checkout logic that computes charged totals is in `src/checkoutservice`.
- `PlaceOrderRequest.coupon_index` is an optional proto field. Absence must mean no coupon. Do not default a missing `coupon_index` to `0`, because index `0` is `SAVE10` and silently discounts orders such as Bamboo Glass Jar ($2.01) + shipping ($8.99), changing the expected $11.00 total.
- For investigation-only tasks in this repo, prefer source inspection with `grep`/`find`; `rg` may not be installed in the environment.
