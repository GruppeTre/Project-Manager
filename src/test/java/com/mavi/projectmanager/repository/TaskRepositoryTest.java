package com.mavi.projectmanager.repository;

import com.mavi.projectmanager.model.SubProject;
import com.mavi.projectmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setup(){}

    //Jens Gotfredsen
    @Test
    void shouldInsertIntoTask() {

        Task dbTaskToInsert = new Task();
        dbTaskToInsert.setName("Task C");
        dbTaskToInsert.setDescription("Test beskrivelse");
        dbTaskToInsert.setStartDate(LocalDate.parse("2025-12-15"));
        dbTaskToInsert.setEndDate(LocalDate.parse("2025-12-30"));
        dbTaskToInsert.setEstimatedDuration(8);

        SubProject subProject = new SubProject();
        subProject.setId(1);

        int expectedId = 3;

        assertDoesNotThrow(() -> taskRepository.createTask(dbTaskToInsert, subProject));

        dbTaskToInsert.setId(expectedId);

        assertEquals(taskRepository.getTaskById(expectedId), dbTaskToInsert);
    }

    //Jens Gotfredsen
    @Test
    void shouldGetTaskById() {
        Task retrievedTask = taskRepository.getTaskById(2);

        assertEquals(2, retrievedTask.getId());
        assertEquals("Task B", retrievedTask.getName());
        assertEquals("Test beskrivelse", retrievedTask.getDescription());
        assertEquals(LocalDate.parse("2025-12-13"), retrievedTask.getStartDate());
        assertEquals(LocalDate.parse("2025-12-15"), retrievedTask.getEndDate());
        assertEquals(24, retrievedTask.getEstimatedDuration());
    }
}
