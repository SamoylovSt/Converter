# Конвертер валют API
Учебный проект по обмену валюты на java и использованием сервлетов, JDBC и PostgrSQL

### Функции
Добавить валюту<br>
Добавить обменный курс<br>
Конвертация валют<br>
Запросы и ответы в виде JSON<br>
Тестовый интерфейс<br>

### Технологии
Java<br>
JDBC<br>
Сервлеты<br>
PostgreSQL<br>
Tomcat развёртывание<br>

### Эндпоинты
GET /currencies – Получить все валюты<br>
GET /currency – Получить валюту<br>
POST /currencies – Добавить новую валюту<br>

GET /exchangeRates – Получить все обменные курсы<br>
GET /exchangeRate – Получить обменный курс<br>
POST /exchangeRates — Добавьте новый обменный курс<br>
PATCH /exchangeRate — Обновите единый курс обмена<br>

Протестировать можно  локально в IDE, + установить PostgreSQL 17, Tomcat 11<br>
URL: http://localhost:8080/ 


