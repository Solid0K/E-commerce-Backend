# AeroCart Backend

A full-featured e-commerce backend built with **Java, Spring Boot, and MongoDB** — covering authentication, product management, shopping carts, checkout, and payment gateway integration (currently mocked pending live payment gateway access).

Built as a learning project to understand logic-heavy applications with complex, interrelated data models and real-world integration patterns.

---

## Features

- **Authentication** — JWT-based signup/login, role-based access control (`CUSTOMER` / `ADMIN`)
- **Product catalog** — full CRUD for admins, public browsing/search for everyone, soft delete, stock management
- **Search** — Atlas Search-powered full-text search across product name, description, and category
- **Shopping cart** — add/update/remove items with live server-side stock and price validation
- **Checkout** — converts a cart into an immutable order snapshot, re-validates stock at checkout time
- **Payment integration** — a mock payment gateway that mirrors a real Stripe/Razorpay integration architecturally (payment intent creation → async webhook-style confirmation → conditional stock deduction)
- **Admin order management** — view all orders across all customers, update order status, paginated
- **Consistent error handling** — a unified JSON error shape across the whole API, with a custom exception mapped to the correct HTTP status for every failure case

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 17+, Spring Boot 4.x |
| Web | Spring Web (REST) |
| Database | MongoDB (Atlas), Spring Data MongoDB |
| Search | MongoDB Atlas Search |
| Auth | Spring Security + JWT (jjwt) |
| Validation | Jakarta Bean Validation |
| Build tool | Maven |

---

## Architecture Notes

A few design decisions worth calling out, since they weren't arbitrary:

**Cart vs. Order — mutable vs. immutable.**
A cart is live, working state: quantities can change, and prices are always resolved fresh from the product catalog when the cart is viewed. An order, once created at checkout, is a **frozen snapshot** — item names and prices are copied in permanently, so a price change or product edit after checkout never retroactively changes what a customer already agreed to pay.

**Stock is validated at checkout, but only decremented on confirmed payment.**
Checkout re-validates stock against live inventory (never trusting whatever was true when an item was added to the cart). But the actual stock decrement only happens once payment is confirmed — via the payment gateway's webhook (or, currently, the mock equivalent). This avoids an abandoned, never-paid order silently reducing real inventory.

**The payment gateway is intentionally mocked.**
Live payment gateway access (Stripe, Razorpay) involves account approval processes — Stripe is currently invite-only in India, and Razorpay requires KYC/PAN verification — that were out of scope for a learning project's timeline. Instead, a `MockPaymentGateway` was built that mirrors the real architecture exactly: a payment intent gets created at checkout, and a separate "confirmation" step (standing in for a real webhook) updates order status and triggers stock deduction. The intentional gap: this mock has **no signature verification**, unlike a real webhook, which would cryptographically confirm the request genuinely came from the payment provider. Swapping in a real gateway later is a scoped, contained change — the rest of the architecture (order status flow, stock timing) doesn't need to change.

**"Not found" vs. "forbidden" for access control.**
When a user requests a resource that exists but belongs to someone else (e.g., another customer's order), the API returns `404 Not Found` rather than `403 Forbidden`. This avoids leaking information about whether a given ID exists at all to someone who isn't authorized to see it.

---

## API Overview

### Auth
```
POST   /auth/signup
POST   /auth/login
```

### Products
```
GET    /products                       — public, paginated, supports ?search=
GET    /products/{id}                  — public
POST   /admin/products                 — admin only
PUT    /admin/products/{id}            — admin only
PATCH  /admin/products/{id}/stock      — admin only, additive stock adjustment
DELETE /admin/products/{id}/softDelete — admin only
```

### Cart (authenticated)
```
GET    /cart
POST   /cart/items
PUT    /cart/items/{productId}
DELETE /cart/items/{productId}
DELETE /cart
```

### Orders
```
POST   /orders/checkOut
GET    /orders                         — current user's own orders, paginated
GET    /orders/{id}                    — own orders only
GET    /admin/orders                   — admin only, all orders, paginated
GET    /admin/orders/{id}              — admin only, any order
PATCH  /admin/orders/{id}/status       — admin only
```

### Payments (mocked)
```
POST   /mock-payment/{paymentIntentId}/simulate?success=true|false
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven
- A MongoDB instance (local or Atlas)

### Environment Variables

This project reads all secrets from environment variables — nothing sensitive is committed to source control.

```
JWT_SECRET=<a long, random, base64-encoded secret — generate with: openssl rand -base64 32>
MONGO_CONNECTION=<your MongoDB connection string>
DATABASE=<database name>
```

### Running locally

```bash
export JWT_SECRET=$(openssl rand -base64 32)
export MONGO_CONNECTION="mongodb+srv://<user>:<password>@<cluster-host>/?appName=<app>"
export DATABASE=ecommerce

./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

An admin account is seeded automatically on first startup (see `AdminSeeder` / `CommandLineRunner`) — check the console log or the `users` collection for the seeded admin email, and change the seeded password before using this anywhere beyond local development.

### Testing

No frontend is built yet — the API is fully testable via **Postman** (or any HTTP client). A typical flow:

1. `POST /auth/signup` → get a JWT
2. `POST /admin/products` (using the seeded admin's token) → create some products
3. `POST /cart/items` (as a regular customer) → build a cart
4. `POST /orders/checkOut` → get a mock `paymentIntentId`
5. `POST /mock-payment/{paymentIntentId}/simulate?success=true` → confirms the order, decrements stock

---

## What's Not Included

- **A frontend.** This is an API-first build, intended to be consumed via Postman or a separate client.
- **A real payment gateway.** See the architecture note above — the integration pattern is fully built, but wired to a mock rather than live Stripe/Razorpay credentials.
- **Refresh tokens.** Auth uses a single 24-hour access token; no refresh-token rotation was built, as a deliberate scope decision for a project this size.

---

## Status

Core backend complete: authentication, product catalog with search, cart, checkout, mocked payment flow, and admin order management are all built and tested end-to-end via Postman.
