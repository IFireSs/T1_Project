INSERT INTO users (id, login, password, email) VALUES
    (1,'admin','{noop}admin','admin@example.com'),
    (2,'jdoe','{noop}secret','jdoe@example.com');

INSERT INTO clients (id, client_id, user_id, first_name, last_name, date_of_birth, document_type, document_id)
VALUES
    (1,'770100000001', 1, 'Ivan', 'Ivanov', DATE '1990-01-10', 'PASSPORT', '4500123456'),
    (2,'770200000023', 2, 'Petr', 'Petrov', DATE '1985-05-20', 'INT_PASSPORT', '70N123456');

INSERT INTO products (id, name, "key")
VALUES
    (1,'Debit Card', 'DC'),
    (2,'Credit Card','CC'),
    (3,'Pension','PENS');

INSERT INTO client_products (id, client_id, product_id, open_date, status)
VALUES
    (1, '770100000001', 'DC1', CURRENT_DATE, 'ACTIVE'),
    (2, '770100000001', 'CC2', CURRENT_DATE, 'ACTIVE'),
    (3, '770200000023', 'PENS3', CURRENT_DATE, 'BLOCKED');

SELECT setval('users_id_seq',         (SELECT COALESCE(MAX(id),0) FROM users), true);
SELECT setval('clients_id_seq',       (SELECT COALESCE(MAX(id),0) FROM clients), true);
SELECT setval('products_id_seq',      (SELECT COALESCE(MAX(id),0) FROM products), true);
SELECT setval('client_products_id_seq',(SELECT COALESCE(MAX(id),0) FROM client_products), true);