package com.yandex.app.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    // Тест: Managers возвращает не null и готовые к работе экземпляры менеджеров
    @Test
    void managersReturnInitializedInstances() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = Managers.getDefaultHistory();
        assertNotNull(tm);
        assertNotNull(hm);
    }
}