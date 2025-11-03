WITH d AS (
    SELECT client_id, product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY client_id, product_id
    HAVING COUNT(*) > 1
),
     u AS (
         SELECT client_id, product_id, keep_id, unnest(ids) AS dup_id
         FROM d
     )
UPDATE cards c
SET account_id = u.keep_id
    FROM u
WHERE c.account_id = u.dup_id
  AND u.dup_id <> u.keep_id;

WITH d AS (
    SELECT client_id, product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY client_id, product_id
    HAVING COUNT(*) > 1
),
     u AS (
         SELECT client_id, product_id, keep_id, unnest(ids) AS dup_id
         FROM d
     )
UPDATE transactions t
SET account_id = u.keep_id
    FROM u
WHERE t.account_id = u.dup_id
  AND u.dup_id <> u.keep_id;

WITH d AS (
    SELECT client_id, product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY client_id, product_id
    HAVING COUNT(*) > 1
),
     u AS (
         SELECT client_id, product_id, keep_id, unnest(ids) AS dup_id
         FROM d
     )
DELETE FROM accounts a
    USING u
WHERE a.id = u.dup_id
  AND u.dup_id <> u.keep_id;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_accounts_client_product'
          AND conrelid = 'accounts'::regclass
    ) THEN
ALTER TABLE accounts
    ADD CONSTRAINT uq_accounts_client_product UNIQUE (client_id, product_id);
END IF;
END$$;