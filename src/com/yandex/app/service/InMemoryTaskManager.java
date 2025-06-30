package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int anotherId = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int newId() {
        return anotherId++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        int id = task.getId();
        if (id <= 0) {
            id = newId();
            task.setId(id);
        } else if (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
            throw new IllegalArgumentException("ID " + id + " уже используется");
        } else if (id >= anotherId) {
            anotherId = id + 1;
        }
        tasks.put(id, task);
    }


    @Override
    public void addEpic(Epic epic) {
        int id = epic.getId();
        if (id <= 0) {
            id = newId();
            epic.setId(id);
        } else if (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
            throw new IllegalArgumentException("ID " + id + " уже используется");
        } else if (id >= anotherId) {
            anotherId = id + 1;
        }
        epics.put(id, epic);
    }


    @Override
    public void addSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (id <= 0) {
            id = newId();
            subtask.setId(id);
        } else if (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
            throw new IllegalArgumentException("ID " + id + " уже используется");
        } else if (id >= anotherId) {
            anotherId = id + 1;
        }

        if (subtask.getEpicId() == subtask.getId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться сама на себя как на эпик");
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic с id " + subtask.getEpicId() + " не существует");
        }
        subtasks.put(id, subtask);
        epic.addSubtask(id);
        updateEpicStatus(epic);
    }



    @Override
    public void updateTask(Task task) {
        tasks.computeIfPresent(task.getId(), (id, oldTask) -> {
            oldTask.setName(task.getName());
            oldTask.setDescription(task.getDescription());
            oldTask.setStatus(task.getStatus());
            return oldTask;
        });
    }


    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) continue;
            Status status = subtask.getStatus();

            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }

        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().removeIf(subtaskId -> subtaskId == id);
                updateEpicStatus(epic);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                result.add(subtasks.get(subId));
            }
        }
        return result;
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.clearSubtasks();
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.clearSubtasks();
                updateEpicStatus(epic); // Новое: обновление статуса эпика
            }
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
