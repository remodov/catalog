# Catalog Service

## Спецификация

`docs/spec/catalog-spec.md` — источник правды по сервису (формат — Use Case спецификация Bounded Context; Tier B, один концепт-агрегат Product → **один файл**, секции контекста 1–11 + агрегата 12–18). **Читай в начале сессии.** Техника (схема БД, стек) — в разделе «Техническая реализация»; домен — в остальных; ссылки между разделами — по якорям.

## Кодогенерация и ревью — через скиллы

Любая работа над UCP-артефактом — через `/ucp-*` скилл, не от руки.

| Тип работы | Скилл |
|---|---|
| Спека / ревью спеки | `/ucp-spec-design` · `/ucp-spec-review` |
| UseCase + Handler + Controller + маппер | `/ucp-pattern-design` |
| OpenAPI + DTO | `/ucp-api-design` |
| Bootstrap (профили, jOOQ, Liquibase) | `/ucp-bootstrap-design` |
| Тесты по разделу «Критерии приёмки» | `/ucp-test-design` |

Catalog — **Tier B** (UseCase Pattern без DDD-агрегатов/событий/саг). Persistence — только jOOQ на сгенерированных классах (`BS-17`). **Комментариев в коде нет** (`JS-7.1`).
