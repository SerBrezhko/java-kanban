package com.yandex.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    // Тест: два Subtask равны если у них равны id
    @Test
    void subtasksAreEqualIfIdEquals() {
        Subtask s1 = new Subtask("Subtask1", "Desc", Status.NEW, 5);
        Subtask s2 = new Subtask("Subtask2", "Desc", Status.DONE, 7);
        s1.setId(100);
        s2.setId(100);

        assertEquals(s1, s2);
    }

    // Тест: нельзя установить id равный epicId (подзадача не может быть своим эпиком)
    @Test
    void cannotSetIdEqualToEpicId() {
        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, 1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subtask.setId(1);
        });
        assertEquals("Subtask не может быть эпиком самого себя", exception.getMessage());
    }

    // Тест: нельзя установить epicId равный id подзадачи
    @Test
    void cannotSetEpicIdEqualToId() {
        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, 5);
        subtask.setId(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(10);
        });
        assertEquals("Subtask не может быть эпиком самого себя", exception.getMessage());
    }
}
