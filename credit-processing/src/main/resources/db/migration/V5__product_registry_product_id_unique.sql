WITH dups AS (
    SELECT product_id, MIN(id) AS keep_id
    FROM product_registry
    GROUP BY product_id
    HAVING COUNT(*) > 1
)
DELETE FROM product_registry pr
    USING dups
WHERE pr.product_id = dups.product_id
  AND pr.id <> dups.keep_id;

DROP INDEX IF EXISTS ix_pr_product_id;

ALTER TABLE product_registry
    ADD CONSTRAINT ux_product_registry_product_id UNIQUE (product_id);
