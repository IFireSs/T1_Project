# T1 Project

Мультимодульный Spring Boot проект с тремя банковскими микросервисами:

- `client-processing` - регистрация клиентов, каталог продуктов, привязка продуктов к клиентам и отправка заявок на выпуск карт.
- `account-processing` - учет счетов, выпуск карт, обработка платежей и транзакций.
- `credit-processing` - обработка кредитных продуктов, принятие кредитного решения и формирование графика платежей.

Сервисы используют PostgreSQL для хранения данных, Flyway для миграций, Kafka для обмена событиями, JWT для авторизации и Spring AOP/Actuator для логирования, метрик и технических аспектов.

## Технологии

- Java 21
- Spring Boot 3.5.5
- Maven Wrapper
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL 16
- Flyway
- Apache Kafka
- MapStruct, Lombok
- JUnit/Spring Boot Test, H2 для тестов
- Micrometer/Prometheus в `client-processing`

## Структура проекта

```text
.
├── pom.xml                  # parent Maven project
├── client-processing/       # сервис клиентов и клиентских продуктов
├── account-processing/      # сервис счетов, карт, платежей и транзакций
├── credit-processing/       # сервис кредитного реестра
├── .mvn/, mvnw, mvnw.cmd    # Maven Wrapper
└── src/                     # остаточный корневой Spring Boot шаблон, не входит в modules
```

## Сервисы и порты

| Сервис | Порт | База данных | PostgreSQL порт |
| --- | ---: | --- | ---: |
| `client-processing` | `8081` | `clientdb`, schema `client` | `5461` |
| `account-processing` | `8082` | `accountdb`, schema `account` | `5460` |
| `credit-processing` | `8083` | `creditdb`, schema `credit` | `5462` |

Kafka доступна на `localhost:9091`, Kafka UI - на `http://localhost:8088`.

## Kafka-топики

| Топик | Кто пишет | Кто читает | Назначение |
| --- | --- | --- | --- |
| `client_products` | `client-processing` | `account-processing` | открытие, обновление и закрытие некредитных клиентских продуктов |
| `client_credit_products` | `client-processing` | `credit-processing` | заявки на кредитные продукты |
| `client_cards` | `client-processing` | `account-processing` | заявки на выпуск карт |
| `client_transactions` | внешняя система | `account-processing` | операции по счетам и картам |
| `client_payments` | внешняя система | `account-processing` | клиентские платежи |
| `service_logs` | аспекты сервисов | Kafka/log consumers | технические логи HTTP, метрик и ошибок datasource |

Кредитными считаются продукты с ключами `IPO`, `PC`, `AC`; остальные клиентские продукты отправляются в `client_products`.

## Быстрый запуск

### 1. Требования

Установите:

- JDK 21
- Docker и Docker Compose

Maven устанавливать отдельно не обязательно, в проекте есть `mvnw.cmd`.

### 2. Запуск инфраструктуры

В отдельных compose-файлах описаны PostgreSQL для каждого сервиса. Kafka и Kafka UI находятся в compose-файле `client-processing`.

```powershell
docker compose -f client-processing/docker-compose.yml up -d
docker compose -f account-processing/docker-compose.yml up -d
docker compose -f credit-processing/docker-compose.yml up -d
```

### 3. Сборка

```powershell
.\mvnw.cmd clean package
```

Запуск тестов:

```powershell
.\mvnw.cmd test
```

### 4. Запуск сервисов

Откройте три терминала и запустите модули:

```powershell
.\mvnw.cmd -pl client-processing spring-boot:run
```

```powershell
.\mvnw.cmd -pl account-processing spring-boot:run
```

```powershell
.\mvnw.cmd -pl credit-processing spring-boot:run
```

По умолчанию используется профиль `postgres`, параметры подключения заданы в `application.yml` каждого модуля.

## Авторизация

Большинство endpoint'ов защищены JWT. Исключения:

- `POST /api/clients/register`
- `POST /api/auth/login`
- `/actuator/**`
- Swagger/OpenAPI пути, если будут подключены

Регистрация клиента возвращает JWT, который нужно передавать в заголовке:

```http
Authorization: Bearer <token>
```

Сервисы также умеют генерировать service-token для межсервисных REST-вызовов через настроенный `RestTemplate`.

Для повторного получения JWT используйте login:

```powershell
curl -Method POST http://localhost:8081/api/auth/login `
  -ContentType "application/json" `
  -Body '{
    "login": "master",
    "password": "master"
  }'
```

Тестовый пользователь `master/master` создается миграцией и получает роль `MASTER`, поэтому им можно создавать, изменять и удалять продукты.

Переменные окружения:

| Переменная | Назначение | Значение по умолчанию |
| --- | --- | --- |
| `JWT_SECRET` | секрет подписи JWT | `change-me-super-secret-256bit-change-this` |
| `SERVICE_ID` | идентификатор сервиса в service-token | `${spring.application.name}` |

## Основные REST endpoint'ы

### client-processing, `http://localhost:8081`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `POST` | `/api/auth/login` | получение JWT по логину и паролю |
| `POST` | `/api/clients/register` | регистрация клиента и получение JWT |
| `GET` | `/api/clients/{clientId}/brief` | краткая информация о клиенте |
| `POST` | `/api/products` | создание продукта, роль `MASTER` |
| `PUT` | `/api/products/{productId}` | обновление продукта, роли `MASTER`, `GRAND_EMPLOYEE` |
| `GET` | `/api/products` | список продуктов |
| `GET` | `/api/products/{productId}` | получение продукта |
| `DELETE` | `/api/products/{productId}` | удаление продукта, роли `MASTER`, `GRAND_EMPLOYEE` |
| `POST` | `/api/client-products` | привязка продукта к клиенту и публикация события |
| `PUT` | `/api/client-products/{productId}` | обновление клиентского продукта |
| `GET` | `/api/client-products` | список клиентских продуктов |
| `GET` | `/api/client-products/{productId}` | получение клиентского продукта |
| `DELETE` | `/api/client-products` | закрытие/удаление клиентского продукта |
| `POST` | `/api/cards/create` | отправка заявки на выпуск карты |

Пример регистрации:

```powershell
curl -Method POST http://localhost:8081/api/clients/register `
  -ContentType "application/json" `
  -Body '{
    "clientCode": "C001",
    "login": "ivanov",
    "password": "pass",
    "email": "ivanov@example.com",
    "firstName": "Ivan",
    "middleName": "Ivanovich",
    "lastName": "Ivanov",
    "dateOfBirth": "1990-01-01",
    "documentType": "PASSPORT",
    "documentId": "123456",
    "documentPrefix": "1234",
    "documentSuffix": "567890"
  }'
```

### account-processing, `http://localhost:8082`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `GET` | `/api/products/by-client/{clientId}` | счета клиента |
| `GET` | `/api/products/{clientId}/{productId}` | счет по клиенту и продукту |

Сервис также слушает Kafka-топики `client_products`, `client_cards`, `client_transactions`, `client_payments`.

### credit-processing, `http://localhost:8083`

| Метод | Endpoint | Описание |
| --- | --- | --- |
| `GET` | `/cred` | список записей кредитного реестра |

Сервис слушает `client_credit_products`. При создании кредитного продукта он:

1. Запрашивает краткую информацию о клиенте в `client-processing`.
2. Проверяет кредитный лимит `credit.decision.limit`.
3. Проверяет наличие просроченных платежей.
4. Создает запись в `product_registry`.
5. Формирует график платежей в `payment_registry`.

## Метрики и healthcheck

Actuator endpoints доступны без авторизации:

```text
GET /actuator/health
GET /actuator/info
GET /actuator/prometheus
```

Prometheus endpoint явно включен в `client-processing`. Для остальных сервисов набор endpoint'ов зависит от их зависимостей и конфигурации.

## Тесты

В проекте есть web- и service-тесты для всех трех модулей:

- `client-processing/src/test`
- `account-processing/src/test`
- `credit-processing/src/test`

Запуск всех тестов:

```powershell
.\mvnw.cmd test
```

Запуск тестов одного модуля:

```powershell
.\mvnw.cmd -pl client-processing test
.\mvnw.cmd -pl account-processing test
.\mvnw.cmd -pl credit-processing test
```

## Настройки

Ключевые параметры находятся в `src/main/resources/application.yml` каждого модуля:

- `server.port` - порт сервиса.
- `spring.datasource.*` - подключение к PostgreSQL.
- `spring.flyway.*` - схема и миграции.
- `spring.kafka.bootstrap-servers` - Kafka broker.
- `topics.*` - имена Kafka-топиков.
- `security.jwt.*` - настройки JWT.
- `app.metric.threshold-ms` - порог медленных методов.
- `app.cache.ttl-ms` - TTL кеша.
- `processing.fraud.*` - параметры fraud-проверки в `account-processing`.
- `credit.*` - параметры кредитного решения в `credit-processing`.
