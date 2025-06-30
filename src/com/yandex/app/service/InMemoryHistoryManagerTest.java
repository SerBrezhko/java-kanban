package com.yandex.app.service;

import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    // Тест: задачи, добавляемые в историю, сохраняют предыдущую версию задачи и её данные
    @Test
    void historyKeepsPreviousVersions() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        task1.setId(1);
        historyManager.add(task1);

        // Модифицируем задачу, меняем статус, но добавляем новую версию в историю
        Task task1Modified = new Task("Task1", "Desc1", Status.DONE);
        task1Modified.setId(1);
        historyManager.add(task1Modified);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        // Первая версия
        assertEquals(Status.NEW, history.get(0).getStatus());
        // Вторая версия
        assertEquals(Status.DONE, history.get(1).getStatus());
    }

    // Тест: история не превышает максимальный размер
    @Test
    void historyDoesNotExceedMaxSize() {
        for (int i = 0; i < 15; i++) {
            Task t = new Task("Task" + i, "Desc" + i, Status.NEW);
            t.setId(i);
            historyManager.add(t);
        }
        List<Task> history = historyManager.getHistory();
        assertTrue(history.size() <= 10);
    }


}