package com.yandex.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    // Тест: два эпика равны если у них равны id
    @Test
    void epicsAreEqualIfIdEquals() {
        Epic e1 = new Epic("Epic1", "Desc");
        Epic e2 = new Epic("Epic2", "Desc other");
        e1.setId(10);
        e2.setId(10);

        assertEquals(e1, e2);
    }

    // Тест: объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    void cannotAddEpicAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        epic.setId(1);

        // Попытка добавить id самого эпика в список подзадач
        assertThrows(IllegalArgumentException.class, () -> epic.addSubtask(1), "Epic не должен содержать себя как подзадачу");
    }
}