package com.yandex.app.service;

import com.yandex.app.model.*;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();

    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);

    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();
}
