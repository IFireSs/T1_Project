DO $$
DECLARE
seq_name text;
    max_id   bigint;
BEGIN

SELECT pg_get_serial_sequence('accounts', 'id') INTO seq_name;
IF seq_name IS NOT NULL THEN
        EXECUTE format('SELECT COALESCE(MAX(id),0) FROM accounts') INTO max_id;
EXECUTE format('SELECT setval(''%s'', %s, true)', seq_name, max_id);
END IF;

SELECT pg_get_serial_sequence('cards', 'id') INTO seq_name;
IF seq_name IS NOT NULL THEN
        EXECUTE format('SELECT COALESCE(MAX(id),0) FROM cards') INTO max_id;
EXECUTE format('SELECT setval(''%s'', %s, true)', seq_name, max_id);
END IF;

SELECT pg_get_serial_sequence('transactions', 'id') INTO seq_name;
IF seq_name IS NOT NULL THEN
        EXECUTE format('SELECT COALESCE(MAX(id),0) FROM transactions') INTO max_id;
EXECUTE format('SELECT setval(''%s'', %s, true)', seq_name, max_id);
END IF;
END $$;