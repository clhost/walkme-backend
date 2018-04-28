# Backend alpha 1.0

И так, кратко:

## Основное (deprecated, позже исправлю, смотри запуск)
1. Всё зависит от файла ```local.properties```, 
сейчас он в корне проекта.
2. В ```local.properties``` есть всё, что нужно для настройки. Чтобы работать 
в режиме **stub** (тестовый), нужно прописать в ```server.stub.enable``` 
значение ```on``` для включения, ```off``` для выключения.

## О режиме stub (deprecated, позже исправлю, смотри запуск)
Режим **stub** поддерживает следующие инварианты:
    
    server.stub.enable=on/off
    server.stub.db_reload=on/off
    server.stub.auth=on/off

* ```server.stub.db_reload``` регулирует загрузку данных в бд. При значении 
```on``` данные перезаливаются с каждым запуском, при ```off``` - 1 раз.
* ```server.stub.auth``` регулирует авторизаци. При значении ```on``` 
авторизация работает, при ```off``` - авторизации нет.

**Тестируйте только в режиме stub.**

## Запуск: 
* ```./gradlew auth_module:drop```: drop таблиц ```wm_user``` и ```wm_sessions```
* ```./gradlew route_module:load```: загрузка данных в базу
* ```./gradlew route_module:drop```: drop таблиц ```wm_place``` и ```category``` 
* ```./gradlew core_module:run_core```: старт сервера (предварительно требуется выполнить ```load```)