# AGENTS.md

- Checkout service is a standalone Gradle module at `src/checkoutservice` but does not include its own wrapper script.
- To verify checkoutservice compilation, use the shared wrapper from adservice: `bash src/adservice/gradlew -p src/checkoutservice compileJava`.
- For this coupon-fix workflow, branch and PR work must target `feature/Coupon-v1` rather than the default branch.
