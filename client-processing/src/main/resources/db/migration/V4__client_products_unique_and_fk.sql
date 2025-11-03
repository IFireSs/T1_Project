WITH dups AS (
    SELECT product_id, MIN(id) AS keep_id
    FROM client_products
    GROUP BY product_id
    HAVING COUNT(*) > 1
)
DELETE FROM client_products cp
    USING dups
WHERE cp.product_id = dups.product_id
  AND cp.id <> dups.keep_id;

DROP INDEX IF EXISTS ix_client_products_product_id;

ALTER TABLE client_products
    ADD CONSTRAINT ux_client_products_product_id UNIQUE (product_id);

ALTER TABLE client_products
    ADD CONSTRAINT fk_client_products_product
        FOREIGN KEY (product_id)
            REFERENCES products(product_id)
            ON DELETE CASCADE;

ALTER TABLE client_products
    ADD CONSTRAINT fk_client_products_client
        FOREIGN KEY (client_id)
            REFERENCES clients(client_id)
            ON DELETE CASCADE;