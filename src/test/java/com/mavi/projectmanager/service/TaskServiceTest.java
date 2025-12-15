package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Task;
import com.mavi.projectmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {

    @Autowired
    TaskService taskService;

    @MockitoBean
    TaskRepository taskRepository;

    private Task dbTask;

    @BeforeEach
    void setUp() {
        int id = 1;

        dbTask = new Task();
        dbTask.setId(id);
    }

    //Magnus Sørensen
    @Test
    void shouldDeleteTask() {

        when(taskRepository.deleteTask(dbTask)).thenReturn(1);

        assertDoesNotThrow(() -> this.taskService.deleteTask(dbTask));

        verify(taskRepository).deleteTask(dbTask);
    }
}
