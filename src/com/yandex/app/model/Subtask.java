package com.yandex.app.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public void setId(int id) {
        if (id == epicId) {
            throw new IllegalArgumentException("Subtask не может быть эпиком самого себя");
        }
        super.setId(id);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == getId()) {
            throw new IllegalArgumentException("Subtask не может быть эпиком самого себя");
        }
        this.epicId = epicId;
    }
}
