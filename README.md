# Task 4 — Online Bookstore (Servlet + Layered MVC)

Learning project: **online bookstore** — book catalog (UTF-8, including Cyrillic in the DB), **PostgreSQL**, registration/sign-in, orders, profile, add/edit catalog items, REST `GET /api/v1/products` and `GET /api/v1/books` (same catalog), PRG after POST, i18n, filters, Thymeleaf, JDBC + HikariCP, SLF4J + Log4j2.

The assignment does not mandate a specific shop theme; this project uses a bookstore in UI text and demo data. The DB table is still named `products` (catalog line items).

## PostgreSQL

1. Create a database and user (example):

```sql
CREATE DATABASE shop OWNER shop;
```

Or with Docker:

```bash
docker run --name shop-pg -e POSTGRES_USER=shop -e POSTGRES_PASSWORD=shop -e POSTGRES_DB=shop -p 5432:5432 -d postgres:16
```

2. Connection settings (priority order):

- environment variables `SHOP_JDBC_URL`, `SHOP_JDBC_USER`, `SHOP_JDBC_PASSWORD`;
- or file `src/main/resources/shop.db.properties` (copy from `shop.db.properties.example` and adjust).

If neither file nor variables are set, defaults apply: `jdbc:postgresql://localhost:5432/shop`, user `shop`, password `shop`.

On startup, `db/schema.sql` runs (`IF NOT EXISTS`), then `db/data.sql` **only when** the `products` table is empty.

### Old seed data (laptop, coffee, single item, etc.)

`data.sql` does not re-run when the catalog is non-empty. To replace the catalog with **books only**, open Query Tool in pgAdmin against database `shop` and run **`src/main/resources/db/reseed-books.sql`** (or paste its contents). It clears orders and the catalog, then inserts demo books.

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

## GitHub

Publish the repository to GitHub yourself (`git remote add` / push).
