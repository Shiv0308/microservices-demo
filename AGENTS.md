# AGENTS.md

## Repository notes
- Frontend code lives under `src/frontend` and uses Go handlers plus HTML templates in `src/frontend/templates`.
- The cart page renders the header currency selector with `show_currency: true`, including when the cart is empty.
- Successful checkout empties the cart before the confirmation page is shown.
- Keep an explicit navigation path from the order confirmation page back to `/cart` for flows that need to validate the now-empty cart after checkout.
