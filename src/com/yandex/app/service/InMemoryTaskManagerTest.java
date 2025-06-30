package com.yandex.app.service;

import com.yandex.app.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void addAndGetTasksById() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);

        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        Task fetchedTask = manager.getTaskById(task.getId());
        Epic fetchedEpic = manager.getEpicById(epic.getId());
        Subtask fetchedSubtask = manager.getSubtaskById(subtask.getId());

        assertEquals(task, fetchedTask);
        assertEquals(epic, fetchedEpic);
        assertEquals(subtask, fetchedSubtask);
    }

    @Test
    void tasksWithManualAndGeneratedIdsDoNotConflict() {
        Task taskManual = new Task("Manual", "Desc", Status.NEW);
        taskManual.setId(999);

        Task taskAuto = new Task("Auto", "Desc", Status.NEW);

        manager.addTask(taskManual);
        manager.addTask(taskAuto);

        assertEquals(taskManual, manager.getTaskById(999));
        assertEquals(taskAuto, manager.getTaskById(taskAuto.getId()));

        assertNotEquals(taskManual.getId(), taskAuto.getId());
    }

    @Test
    void taskFieldsRemainCorrectAfterAdding() {
        Task task = new Task("Immutable", "Desc", Status.NEW);
        manager.addTask(task);

        Task fetched = manager.getTaskById(task.getId());

        assertEquals(task.getName(), fetched.getName());
        assertEquals(task.getDescription(), fetched.getDescription());
        assertEquals(task.getStatus(), fetched.getStatus());
        assertEquals(task.getId(), fetched.getId());
    }

    @Test
    void epicCannotHaveItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        // Прямое добавление через Epic не должно происходить, проверяем корректность через менеджер
        assertFalse(epic.getSubtaskIds().contains(epic.getId()), "Epic не должен содержать самого себя как подзадачу");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Self Epic", "Desc", Status.NEW, 0);
        subtask.setId(100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(100); // здесь и будет выброс исключения
        });

        assertEquals("Subtask не может быть эпиком самого себя", exception.getMessage());

    }




    @Test
    void updateTaskSuccessfully() {
        Task task = new Task("Initial", "Desc", Status.NEW);
        manager.addTask(task);

        // Обновляем существующую задачу
        task.setName("Updated Name");
        task.setDescription("Updated Description");
        task.setStatus(Status.IN_PROGRESS);

        manager.updateTask(task);

        Task fetched = manager.getTaskById(task.getId());
        assertEquals("Updated Name", fetched.getName());
        assertEquals("Updated Description", fetched.getDescription());
        assertEquals(Status.IN_PROGRESS, fetched.getStatus());
    }



    @Test
    void updateEpicSuccessfullyAndStatusReflectsSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        // Изначально эпик должен быть NEW, т.к. все подзадачи NEW
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());

        // Обновляем одну подзадачу в DONE, эпик должен стать IN_PROGRESS
        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());

        // Обновляем вторую подзадачу в DONE, эпик должен стать DONE
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void deleteTaskByIdRemovesTaskAndFromHistory() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);
        manager.getTaskById(task.getId());  // Добавляем в историю

        assertFalse(manager.getHistory().isEmpty());

        manager.deleteTaskById(task.getId());
        assertNull(manager.getTaskById(task.getId())); // Уже нет задачи
        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    void deleteEpicRemovesEpicSubtasksAndHistory() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());  // Добавляем в историю

        assertFalse(manager.getHistory().isEmpty());

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(subtask.getId()));
        assertFalse(manager.getHistory().contains(epic));
        assertFalse(manager.getHistory().contains(subtask));
    }

    @Test
    void deleteSubtaskRemovesSubtaskAndUpdatesEpic() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.deleteSubtaskById(subtask1.getId());

        assertNull(manager.getSubtaskById(subtask1.getId()));
        assertEquals(1, manager.getSubtasksOfEpic(epic.getId()).size());
    }

    @Test
    void getSubtasksOfEpicReturnsCorrectSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        var subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void deleteAllTasksClearsTasksAndHistory() {
        Task task1 = new Task("T1", "D1", Status.NEW);
        Task task2 = new Task("T2", "D2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        assertFalse(manager.getAllTasks().isEmpty());
        assertFalse(manager.getHistory().isEmpty());

        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getHistory().stream().noneMatch(t -> t.getId() == task1.getId() || t.getId() == task2.getId()));
    }

    @Test
    void deleteAllEpicsClearsEpicsSubtasksAndHistory() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        manager.deleteAllEpics();

        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().stream().noneMatch(t -> t.getId() == epic.getId() || t.getId() == subtask.getId()));
    }

    @Test
    void deleteAllSubtasksClearsSubtasksAndUpdatesEpics() {
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.deleteAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty());
        assertEquals(0, manager.getSubtasksOfEpic(epic.getId()).size());
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void historyRecordsViewedTasksAndRemovesOnDelete() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);
        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        assertEquals(3, manager.getHistory().size());

        manager.deleteTaskById(task.getId());
        assertEquals(2, manager.getHistory().size());
        assertFalse(manager.getHistory().contains(task));

        manager.deleteEpicById(epic.getId());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void generatedIdsAreUniqueAndIncremental() {
        Task task1 = new Task("T1", "D1", Status.NEW);
        manager.addTask(task1);
        Task task2 = new Task("T2", "D2", Status.NEW);
        manager.addTask(task2);

        assertTrue(task2.getId() > task1.getId());

        Epic epic1 = new Epic("E1", "D1");
        manager.addEpic(epic1);
        Epic epic2 = new Epic("E2", "D2");
        manager.addEpic(epic2);

        assertTrue(epic2.getId() > epic1.getId());
    }

    @Test
    void historyIsEmptyAfterDeletingAllEntities() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.addTask(task);

        Epic epic = new Epic("Epic", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        assertFalse(manager.getHistory().isEmpty());

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        assertTrue(manager.getHistory().isEmpty());
    }
}
