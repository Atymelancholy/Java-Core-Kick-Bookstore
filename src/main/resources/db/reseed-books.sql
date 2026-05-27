-- Одноразовая перезаливка каталога книгами (PostgreSQL).
-- Выполните в pgAdmin / psql, если в products ещё «ноутбук», «кофе» и т.п. со старого seed.
-- Заказы и позиции заказов очищаются, пользователи не трогаются.

TRUNCATE order_items, orders, products RESTART IDENTITY CASCADE;

INSERT INTO products (name, description, price_cents, stock) VALUES
('Чистая архитектура', 'Роберт Мартин. Перевод с англ. Учебник по проектированию.', 89000, 6),
('Designing Data-Intensive Applications', 'Martin Kleppmann. Distributed systems and data engineering.', 459900, 3),
('Кнігі і людзі', 'Зборнік эсэ і артыкулаў, беларуская мова.', 2499, 25),
('Der Vorleser', 'Bernhard Schlink. Roman.', 1299, 40),
('Кніга «Java»', 'Падручнік для курса (кіраўніцтва па мове).', 4599, 12);
