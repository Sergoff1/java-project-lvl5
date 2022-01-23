package hexlet.code.app.controller;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.app.utils.TestUtils.asJson;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@DBRider
@DBUnit(alwaysCleanBefore = true)
@DataSet("tasks.yml")
public class LabelControllerTest {

    private static final String TEST_EMAIL = "Egor@Egor.com";

    private static final String CONTROLLER_PATH = "/api/labels";

    @Autowired
    private TestUtils utils;

    @Autowired
    private LabelRepository labelRepository;

    @Test
    void createLabel() throws Exception {
        Assertions.assertEquals(3, labelRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson("content"));

        utils.perform(request, TEST_EMAIL).andExpect(status().isCreated());

        Assertions.assertEquals(4, labelRepository.count());
    }

    @Test
    void getLabelById() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH + "/1"), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("feature"));
    }

    @Test
    void getAllLabels() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsString().contains("feature"));
        Assertions.assertTrue(response.getContentAsString().contains("bug"));
        Assertions.assertTrue(response.getContentAsString().contains("urgent"));
    }

    @Test
    void updateLabel() throws Exception {
        MockHttpServletRequestBuilder request = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson("updated"));

        utils.perform(request, TEST_EMAIL).andExpect(status().isOk());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH), TEST_EMAIL)
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertFalse(response.getContentAsString().contains("feature"));
        Assertions.assertTrue(response.getContentAsString().contains("updated"));
    }

    @Test
    void deleteLabel() throws Exception {
        Assertions.assertEquals(3, labelRepository.count());

        utils.perform(delete(CONTROLLER_PATH + "/1"), TEST_EMAIL).andExpect(status().isOk());

        Assertions.assertEquals(2, labelRepository.count());
    }

    @Test
    void securityTest() throws Exception {
        utils.perform(get(CONTROLLER_PATH)).andExpect(status().isUnauthorized());
        utils.perform(get(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());
        utils.perform(delete(CONTROLLER_PATH + "/1")).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson("label"));

        utils.perform(request).andExpect(status().isUnauthorized());

        MockHttpServletRequestBuilder requestPut = put(CONTROLLER_PATH + "/1")
                .contentType(APPLICATION_JSON)
                .content(asJson("label"));

        utils.perform(requestPut).andExpect(status().isUnauthorized());
    }

    @Test
    void validationTest() throws Exception {
        Assertions.assertEquals(3, labelRepository.count());

        MockHttpServletRequestBuilder request = post(CONTROLLER_PATH)
                .contentType(APPLICATION_JSON)
                .content(asJson(new TaskStatusDto("")));

        utils.perform(request, TEST_EMAIL).andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(3, labelRepository.count());
    }
}
