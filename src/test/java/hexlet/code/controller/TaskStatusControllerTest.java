package hexlet.code.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.asJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DBRider
@DBUnit(alwaysCleanBefore = true)
@DataSet("taskStatuses.yml")
public class TaskStatusControllerTest {

    private static final String CONTROLLER_PATH = "/api/statuses";

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskStatusRepository statusRepository;

    @BeforeEach
    void before() throws Exception {
        utils.regDefaultUser();
    }

    @Test
    void createStatus() throws Exception {
        Assertions.assertEquals(3, statusRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson("archived"));

        utils.perform(request, TEST_USERNAME).andExpect(status().isCreated());

        Assertions.assertEquals(4, statusRepository.count());
    }

    @Test
    void getStatusById() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH + "/1"), TEST_USERNAME)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("new"));
    }

    @Test
    void getAllStatuses() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_USERNAME)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("new"));
        Assertions.assertTrue(response.getContentAsString().contains("finished"));
        Assertions.assertTrue(response.getContentAsString().contains("at work"));
    }

    @Test
    void updateStatus() throws Exception {
        MockHttpServletRequestBuilder request = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson("start"));

        utils.perform(request, TEST_USERNAME).andExpect(status().isOk());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_USERNAME)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().contains("new"));
        Assertions.assertTrue(response.getContentAsString().contains("start"));
    }

    @Test
    void deleteStatus() throws Exception {
        Assertions.assertEquals(3, statusRepository.count());

        utils.perform(delete(CONTROLLER_PATH + "/1"), TEST_USERNAME).andExpect(status().isOk());

        Assertions.assertEquals(2, statusRepository.count());
    }

    @Test
    void securityTest() throws Exception {
        utils.perform(get(CONTROLLER_PATH)).andExpect(status().isUnauthorized());
        utils.perform(get(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());
        utils.perform(delete(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson("stat"));

        utils.perform(request).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder requestPut = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson("stat"));

        utils.perform(requestPut).andExpect(status().isUnauthorized());
    }

    @Test
    void validationTest() throws  Exception {
        Assertions.assertEquals(3, statusRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson(new TaskStatusDto("")));

        utils.perform(request, TEST_USERNAME).andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(3, statusRepository.count());
    }
}
