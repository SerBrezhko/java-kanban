public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

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

        System.out.println("All Tasks: " + manager.getAllTasks());
        System.out.println("\nAll Epics: " + manager.getAllEpics());
        System.out.println("\nAll Subtasks: " + manager.getAllSubtasks());

        sub1.setStatus(Status.DONE);  // Обновим статус
        sub2.setStatus(Status.IN_PROGRESS);
            manager.updateSubtask(sub1);
            manager.updateSubtask(sub2);

        System.out.println("\nEpic1 после обновления: " + manager.getEpicById(epic1.getId()));

            manager.deleteTaskById(task1.getId());  // Удаление
            manager.deleteEpicById(epic2.getId());

        System.out.println("\nВсе задачи после удаления: " + manager.getAllTasks());
        System.out.println("\nВсе эпики после удаления: " + manager.getAllEpics());
        System.out.println("\nВсе подзадачи после удаления: " + manager.getAllSubtasks());
    }
}

