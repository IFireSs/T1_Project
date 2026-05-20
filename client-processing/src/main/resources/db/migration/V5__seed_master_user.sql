INSERT INTO users (login, password, email)
VALUES ('master', '{noop}master', 'master@example.com')
ON CONFLICT (login) DO UPDATE
SET password = EXCLUDED.password,
    email = EXCLUDED.email;

INSERT INTO clients (client_id, user_id, first_name, last_name, date_of_birth, document_type, document_id)
SELECT '770000000099', u.id, 'Master', 'User', DATE '1990-01-01', 'PASSPORT', 'MASTER-DOC-1'
FROM users u
WHERE u.login = 'master'
ON CONFLICT (client_id) DO UPDATE
SET user_id = EXCLUDED.user_id,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    date_of_birth = EXCLUDED.date_of_birth,
    document_type = EXCLUDED.document_type,
    document_id = EXCLUDED.document_id;
