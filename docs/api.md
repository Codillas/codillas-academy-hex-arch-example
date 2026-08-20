# HTTP API reference

## Conventions

- Default base URL: `http://localhost:8080`
- Request and success-response media type: `application/json`
- Error media type: `application/problem+json`
- Resource identifiers: UUID strings
- Timestamps: UTC ISO-8601 instants
- Money: JSON decimal values with at most two fractional digits

The API is intentionally unauthenticated for this architecture example.

## Endpoint summary

| Module | Method | Path | Success | Description |
| --- | --- | --- | --- | --- |
| Customers | `POST` | `/api/customers` | `201` | Register a customer |
| Customers | `GET` | `/api/customers/{customerId}` | `200` | Get one customer |
| Customers | `GET` | `/api/customers` | `200` | List customers, newest first |
| Catalog | `POST` | `/api/products` | `201` | Create an active product |
| Catalog | `GET` | `/api/products/{productId}` | `200` | Get one product |
| Catalog | `GET` | `/api/products` | `200` | List products, newest first |
| Catalog | `PUT` | `/api/products/{productId}/deactivation` | `200` | Deactivate a product idempotently |
| Inventory | `POST` | `/api/inventory/{productId}/restocks` | `200` | Add units to inventory |
| Inventory | `GET` | `/api/inventory/{productId}` | `200` | Get available stock |
| Orders | `POST` | `/api/orders` | `201` | Validate references, reserve stock, and place an order |
| Orders | `GET` | `/api/orders/{orderId}` | `200` | Get one order |
| Orders | `GET` | `/api/orders` | `200` | List orders, newest first |
| Orders | `PUT` | `/api/orders/{orderId}/confirmation` | `200` | Confirm an order idempotently |
| Orders | `PUT` | `/api/orders/{orderId}/cancellation` | `200` | Cancel an order and release stock once |

List endpoints are deliberately unpaginated because this is a small teaching application.

## Customers

### Register a customer

```http
POST /api/customers
Content-Type: application/json

{
  "name": "Ada Lovelace",
  "email": "ada@example.com"
}
```

Validation:

- `name` is required and has at most 120 characters;
- `email` is required, must be a valid address, and has at most 320 characters;
- email addresses are normalized to lowercase and must be unique.

Response:

```http
HTTP/1.1 201 Created
Location: /api/customers/1cfbfe5b-6bcb-4332-93d9-30608f946d82

{
  "id": "1cfbfe5b-6bcb-4332-93d9-30608f946d82",
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "createdAt": "2026-08-20T08:00:00Z"
}
```

Registering the same normalized email again returns `409 Conflict`.

### Query customers

```bash
curl http://localhost:8080/api/customers/{customerId}
curl http://localhost:8080/api/customers
```

An unknown customer identifier returns `404 Not Found`.

## Catalog

### Create a product

```http
POST /api/products
Content-Type: application/json

{
  "name": "Mechanical keyboard",
  "price": 129.90
}
```

Validation:

- `name` is required and has at most 200 characters;
- `price` must be at least `0.01`, with at most 17 integer digits and two fractional digits.

Response:

```http
HTTP/1.1 201 Created
Location: /api/products/7c340d44-9904-4bf2-8106-6b21ac7f6f08

{
  "id": "7c340d44-9904-4bf2-8106-6b21ac7f6f08",
  "name": "Mechanical keyboard",
  "price": 129.90,
  "active": true,
  "createdAt": "2026-08-20T08:01:00Z",
  "updatedAt": "2026-08-20T08:01:00Z"
}
```

### Query and deactivate products

```bash
curl http://localhost:8080/api/products/{productId}
curl http://localhost:8080/api/products
curl -X PUT http://localhost:8080/api/products/{productId}/deactivation
```

Deactivation is idempotent. An inactive product remains queryable but cannot be restocked or used for
a new order. An unknown product identifier returns `404 Not Found`.

## Inventory

### Restock a product

Restocking adds to the current quantity; it does not replace it.

```http
POST /api/inventory/7c340d44-9904-4bf2-8106-6b21ac7f6f08/restocks
Content-Type: application/json

{
  "quantity": 5
}
```

Response:

```json
{
  "productId": "7c340d44-9904-4bf2-8106-6b21ac7f6f08",
  "availableQuantity": 5,
  "updatedAt": "2026-08-20T08:02:00Z"
}
```

`quantity` must be positive. Restocking an unknown or inactive product returns
`422 Unprocessable Content`.

### Query inventory

```bash
curl http://localhost:8080/api/inventory/{productId}
```

Inventory is created by the first restock. Querying a product with no inventory row returns
`404 Not Found`.

## Orders

### Place an order

The customer and product must exist, the product must be active, and sufficient stock must be
available.

```http
POST /api/orders
Content-Type: application/json

{
  "customerId": "1cfbfe5b-6bcb-4332-93d9-30608f946d82",
  "productId": "7c340d44-9904-4bf2-8106-6b21ac7f6f08",
  "quantity": 2
}
```

Response:

```http
HTTP/1.1 201 Created
Location: /api/orders/704b090f-b705-4022-8181-85829fa6ea17

{
  "id": "704b090f-b705-4022-8181-85829fa6ea17",
  "customerId": "1cfbfe5b-6bcb-4332-93d9-30608f946d82",
  "productId": "7c340d44-9904-4bf2-8106-6b21ac7f6f08",
  "productName": "Mechanical keyboard",
  "unitPrice": 129.90,
  "quantity": 2,
  "totalPrice": 259.80,
  "status": "PLACED",
  "createdAt": "2026-08-20T08:03:00Z",
  "updatedAt": "2026-08-20T08:03:00Z"
}
```

The product name and unit price are snapshots taken at placement time. A successful placement
decrements available inventory in the same database transaction.

### Query orders

```bash
curl http://localhost:8080/api/orders/{orderId}
curl http://localhost:8080/api/orders
```

An unknown order identifier returns `404 Not Found`.

### Confirm an order

```bash
curl -X PUT http://localhost:8080/api/orders/{orderId}/confirmation
```

`PLACED → CONFIRMED` is valid. Confirming an already confirmed order is idempotent. Confirming a
cancelled order returns `409 Conflict`.

### Cancel an order

```bash
curl -X PUT http://localhost:8080/api/orders/{orderId}/cancellation
```

Both `PLACED → CANCELLED` and `CONFIRMED → CANCELLED` are valid. The first cancellation returns the
reserved quantity to inventory. Repeating the command returns the unchanged order and does not release
stock again.

## Error responses

Business and lookup failures use RFC 9457 problem details. A stock conflict looks like:

```http
HTTP/1.1 409 Conflict
Content-Type: application/problem+json

{
  "type": "about:blank",
  "title": "Insufficient stock",
  "status": 409,
  "detail": "Product 7c340d44-9904-4bf2-8106-6b21ac7f6f08 has 5 units available; 99 were requested",
  "instance": "/api/orders",
  "productId": "7c340d44-9904-4bf2-8106-6b21ac7f6f08",
  "requestedQuantity": 99,
  "availableQuantity": 5
}
```

| Status | Meaning |
| --- | --- |
| `400 Bad Request` | Malformed JSON, bean-validation failure, or invalid command value |
| `404 Not Found` | Requested customer, product, inventory, or order does not exist |
| `409 Conflict` | Duplicate email, insufficient stock, unavailable inventory, or invalid order transition |
| `422 Unprocessable Content` | A referenced customer/product is unavailable for the requested workflow |

Error handlers are scoped to each module's web adapter so HTTP concerns remain outside the domain and
application ports.
