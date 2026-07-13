# Repository memory

- The storefront frontend is server-rendered from Go templates under `src/frontend/templates`.
- Some automated UI checks use brittle absolute XPath selectors against direct child `<div>` positions inside summary sections.
- On `src/frontend/templates/order.html`, prefer non-`div` wrappers for optional grouped content placed before shipping/coupon/total rows, so existing direct-child `div[n]` locators remain stable.
