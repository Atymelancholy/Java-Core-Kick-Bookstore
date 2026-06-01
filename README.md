# Task 4 — Online Bookstore

Learning project: **online bookstore** — book catalog (UTF-8, including Cyrillic in the DB), **PostgreSQL**, registration/sign-in, orders, profile, add/edit catalog items, REST `GET /api/v1/products` and `GET /api/v1/books` (same catalog), PRG after POST, i18n, filters, Thymeleaf, JDBC + HikariCP, SLF4J + Log4j2.

The assignment does not mandate a specific shop theme; this project uses a bookstore in UI text and demo data. The DB table is still named `products` (catalog line items).

<img width="2524" height="1248" alt="image" src="https://github.com/user-attachments/assets/febeb6c2-09bd-4b4a-aec1-b78653f66e1d" />

## PostgreSQL

1. Create a database and user (example):

```sql
CREATE DATABASE shop OWNER shop;
```

Or with Docker:

```bash
docker run --name shop-pg -e POSTGRES_USER=shop -e POSTGRES_PASSWORD=shop -e POSTGRES_DB=shop -p 5432:5432 -d postgres:16
```

2. Connection settings:

Defaults apply: `jdbc:postgresql://localhost:5432/shop`, user `shop`, password `shop`.

## Administrator (demo)

On first startup an admin account is created if missing:

| Login | Password |
|-------|----------|
| `admin` | `admin` |

Admins can add, edit, and delete books. Registered customers can only place orders in the catalog.

## Run

```bash
mvn jetty:run
```

Open http://localhost:8080/ — redirects to the book catalog.

## Tests

```bash
mvn test
```

