package hexlet.code.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.TaskDto;
import hexlet.code.repository.TaskRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Set;
import static hexlet.code.utils.TestUtils.asJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@DBRider
@DBUnit(alwaysCleanBefore = true)
@DataSet("tasks.yml")
public class TaskControllerTest {

    private static final String CONTROLLER_PATH = "/api/tasks";

    private static final TaskDto TASK_DATA = new TaskDto(
            "myTask",
            "test task",
            1L,
            1L,
            Set.of(1L)
    );

    private static final String TEST_EMAIL = "Egor@Egor.com";

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void createTask() throws Exception {
        Assertions.assertEquals(3, taskRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson(TASK_DATA));

        utils.perform(request, TEST_EMAIL).andExpect(status().isCreated());

        Assertions.assertEquals(4, taskRepository.count());
    }

    @Test
    void getTaskById() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH + "/1"), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("taskOne"));
    }

    @Test
    void getAllTasks() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("taskOne"));
        Assertions.assertTrue(response.getContentAsString().contains("taskTwo"));
    }

    @Test
    void getAllByFilter() throws Exception {
        final String authorFilter = "?authorId=3";
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH + authorFilter), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("taskFilter"));
        Assertions.assertFalse(response.getContentAsString().contains("taskOne"));
        Assertions.assertFalse(response.getContentAsString().contains("taskTwo"));

        final String labelAndExecutorFilter = "?labels=1&executorId=1";
        MockHttpServletResponse labelResponse = utils.perform(
                get(CONTROLLER_PATH + labelAndExecutorFilter), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, labelResponse.getStatus());
        Assertions.assertTrue(labelResponse.getContentAsString().contains("taskTwo"));
        Assertions.assertFalse(labelResponse.getContentAsString().contains("taskOne"));
        Assertions.assertFalse(labelResponse.getContentAsString().contains("taskFilter"));
    }

    @Test
    void updateTask() throws Exception {
        MockHttpServletRequestBuilder request = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson(TASK_DATA));

        utils.perform(request, TEST_EMAIL).andExpect(status().isOk());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("myTask"));
        Assertions.assertFalse(response.getContentAsString().contains("TaskOne"));
    }

    @Test
    void deleteTask() throws Exception {
        Assertions.assertEquals(3, taskRepository.count());

        utils.perform(delete(CONTROLLER_PATH + "/1"), TEST_EMAIL).andExpect(status().isOk());

        Assertions.assertEquals(2, taskRepository.count());
    }

    @Test
    void securityTest() throws Exception {
        utils.perform(get(CONTROLLER_PATH)).andExpect(status().isUnauthorized());
        utils.perform(get(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());
        utils.perform(delete(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson(TASK_DATA));

        utils.perform(request).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder requestPut = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson(TASK_DATA));

        utils.perform(requestPut).andExpect(status().isUnauthorized());
    }

    @Test
    void validationTest() throws  Exception {
        Assertions.assertEquals(3, taskRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson(new TaskDto()));

        utils.perform(request, TEST_EMAIL).andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(3, taskRepository.count());
    }

    @Test
    void deleteUserAndStatusAssociatedWithTask() throws Exception {

        utils.perform(delete("/api/users/1"), TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());

        utils.perform(delete("/api/statuses/1"), TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());
    }
}
