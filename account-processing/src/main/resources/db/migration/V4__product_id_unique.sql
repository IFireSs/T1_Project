WITH d AS (
    SELECT product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY product_id
    HAVING COUNT(*) > 1
),
     u AS (
         SELECT product_id, keep_id, unnest(ids) AS dup_id
         FROM d
     )
UPDATE cards c
SET account_id = u.keep_id
    FROM u
WHERE c.account_id = u.dup_id
  AND u.dup_id <> u.keep_id;

WITH d AS (
    SELECT product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY product_id
    HAVING COUNT(*) > 1
),
u AS (
    SELECT product_id, keep_id, unnest(ids) AS dup_id
    FROM d
)
UPDATE transactions t
SET account_id = u.keep_id
FROM u
WHERE t.account_id = u.dup_id
  AND u.dup_id <> u.keep_id;

WITH d AS (
    SELECT product_id, MIN(id) AS keep_id, ARRAY_AGG(id) AS ids
    FROM accounts
    GROUP BY product_id
    HAVING COUNT(*) > 1
),
     u AS (
         SELECT product_id, keep_id, unnest(ids) AS dup_id
         FROM d
     )
DELETE FROM accounts a
    USING u
WHERE a.id = u.dup_id
  AND u.dup_id <> u.keep_id;

ALTER TABLE accounts
    ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE accounts
DROP CONSTRAINT IF EXISTS uq_accounts_client_product;

ALTER TABLE accounts
    ADD CONSTRAINT uq_accounts_product UNIQUE (product_id);