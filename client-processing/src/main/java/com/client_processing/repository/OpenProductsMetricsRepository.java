package com.client_processing.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OpenProductsMetricsRepository extends org.springframework.data.repository.Repository<Object, Long> {

    interface Row {
        String getKey();
        long getCnt();
    }

    @Query(value = """
        SELECT p.key AS key, COUNT(*) AS cnt
        FROM client_products cp
        JOIN products p ON p.product_id = cp.product_id
        WHERE cp.status IN ('OPENED','ACTIVE')
        GROUP BY p.key
    """, nativeQuery = true)
    List<Row> countOpenByKey();
}
