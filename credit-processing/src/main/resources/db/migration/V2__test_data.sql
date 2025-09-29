INSERT INTO product_registry (id, client_id, account_id, product_id, interest_rate, open_date) VALUES
    (1, 1001, 5001, 20001, 0.120000, DATE '2025-01-15'),
    (2, 1001, 5002, 20002, 0.250000, DATE '2025-03-01'),
    (3, 1002, 5003, 20003, 0.180000, DATE '2025-06-10');

INSERT INTO payment_registry (id, product_registry_id, payment_date, amount, interest_rate_amount, debt_amount, expired, payment_expiration_date) VALUES
    (1, 1, DATE '2025-02-15',  5000.00, 1200.00, 38000.00, FALSE, DATE '2025-02-20'),
    (2, 1, DATE '2025-03-15',  5000.00, 1100.00, 33000.00, TRUE,  DATE '2025-03-20'),
    (3, 2, DATE '2025-04-01', 12000.00, 2500.00, 88000.00, FALSE, DATE '2025-04-05'),
    (4, 3, DATE '2025-07-10',  8000.00, 1800.00, 52000.00, FALSE, DATE '2025-07-15');

SELECT setval('product_registry_id_seq', (SELECT COALESCE(MAX(id),0) FROM product_registry), true);
SELECT setval('payment_registry_id_seq', (SELECT COALESCE(MAX(id),0) FROM payment_registry), true);