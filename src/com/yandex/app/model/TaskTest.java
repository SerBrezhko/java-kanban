package com.yandex.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    // Тест: задачи равны, если равен id
    @Test
    void tasksAreEqualIfIdEquals() {
        Task t1 = new Task("Name", "Desc", Status.NEW);
        Task t2 = new Task("Other name", "Other desc", Status.DONE);
        t1.setId(5);
        t2.setId(5);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    // Тест: задачи не равны если id разный
    @Test
    void tasksNotEqualIfIdDifferent() {
        Task t1 = new Task("Name", "Desc", Status.NEW);
        Task t2 = new Task("Name", "Desc", Status.NEW);
        t1.setId(1);
        t2.setId(2);

        assertNotEquals(t1, t2);
    }
}