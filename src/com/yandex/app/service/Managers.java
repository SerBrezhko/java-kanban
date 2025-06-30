package com.yandex.app.service;

public class Managers {

    private Managers() {
        // Добаавил приватный конструктор который запрещает создание объектов этого класса
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
