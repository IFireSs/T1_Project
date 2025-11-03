create table if not exists error_log
(
    id bigserial primary key,
    timestamp timestamptz not null,
    method_signature varchar(512),
    exception_message varchar(4000),
    stacktrace varchar(8000),
    params_json varchar(4000),
    service_name varchar(128),
    type varchar(32)
    );