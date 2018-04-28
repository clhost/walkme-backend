# Backend alpha 2.0
## Основное 
На данный момент проект разбит на 3 модуля:
* Модуль авторизации
* Модуль работы с маршрутами
* Модуль, связывающий оба (проксирующий)

Соответственно, для каждого модуля свои проперти.

* Проперти модуля авторизации лежат в ```auth_module/src/resources```
    * ```auth_hibernate.properties``` для связи с базой
    * ```auth_local.properties``` для настройки OAuth
* Проперти модуля работы с маршрутами лежат в ```route_module/src/resources```
    * ```route_hibernate.properties``` для связи с базой
* Проперти связующего модуля
    * ```core_local.properties``` для настройки сервера

## О режиме stub
Режим **stub** поддерживает следующие инварианты:
    
    server.stub.enable=on/off
    
**Тестируйте только в режиме stub.**

## Запуск: 
* ```./gradlew auth_module:drop```: drop таблиц ```wm_user``` и ```wm_sessions```
* ```./gradlew route_module:load```: загрузка данных в базу
* ```./gradlew route_module:drop```: drop таблиц ```wm_place``` и ```category``` 
* ```./gradlew core_module:run_core```: старт сервера (предварительно требуется выполнить ```load```)