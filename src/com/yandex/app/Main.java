package com.yandex.app;

import com.yandex.app.model.*;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Купить овощи", "Лук, картофель, помидоры", Status.NEW);
        Task task2 = new Task("Купить фрукты", "Яблоки, груши, апельсины", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Обучение Java", "Изучить спринт номер 4");
        manager.addEpic(epic1);

        Subtask sub1 = new Subtask("Теория", "Изучить теорию", Status.NEW, epic1.getId());
        Subtask sub2 = new Subtask("Практика", "Сделать финальное задание", Status.NEW, epic1.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        Epic epic2 = new Epic("Найти работу", "Поиск вакансий");
        manager.addEpic(epic2);

        Subtask sub3 = new Subtask("Резюме", "Сделать резюме", Status.NEW, epic2.getId());
        manager.addSubtask(sub3);

        printAllTasks(manager);

        // Проверим историю просмотров
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(sub1.getId());

        System.out.println("\nИстория после просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}
