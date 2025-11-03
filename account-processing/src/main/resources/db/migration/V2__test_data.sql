INSERT INTO accounts (id, client_id, product_id, balance, interest_rate, is_recalc, card_exist, status)
VALUES
    (1, '770100000001', 'DC1', 10000.00, 0.050000, TRUE, TRUE,  'ACTIVE'),
    (2, '770100000001', 'CC2',  2500.00, 0.000000, TRUE, FALSE, 'ACTIVE'),
    (3, '770100000001', 'PENS3',  -300.00, 0.250000, FALSE, TRUE, 'FROZEN');

INSERT INTO cards (id, account_id, card_id, payment_system, status)
VALUES
    (1, 1, '5333335333331111', 'MASTERCARD', 'ACTIVE'),
    (2, 3, '4111114111112222', 'VISA',       'FROZEN');


INSERT INTO payments (id, account_id, payment_date, amount, is_credit, payed_at, type)
VALUES
    (1, 1, CURRENT_DATE, 1000.00, TRUE,  NOW(), 'CASH_IN'),
    (2, 1, CURRENT_DATE,  250.00, FALSE, NOW(), 'FEE');

INSERT INTO transactions (id, account_id, card_id, type, amount, status, "timestamp")
VALUES
    (1, 1, 1, 'PURCHASE', 1250.00, 'COMPLETE', NOW()),
    (2, 1, 1, 'REFUND',    100.00, 'COMPLETE', NOW()),
    (3, 3, 2, 'PURCHASE',  300.00, 'BLOCKED',  NOW());