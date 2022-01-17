package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("users.yml")
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    private static final String CONTROLLER_PATH = "/api/users";

    private final UserDto testRegistrationData = new UserDto(
            "Sidr@Sidr.com",
            "Sidr",
            "Sidorov",
            "123456"
    );

    @Test
    void getUsers() throws Exception {
        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Egor@Egor.com"));
        Assertions.assertTrue(response.getContentAsString().contains("Petr@Petr.com"));
        Assertions.assertTrue(response.getContentAsString().contains("Ivan@Ivan.com"));
        Assertions.assertFalse(response.getContentAsString().contains("password"));
    }

    @Test
    void create() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        utils.regUser(testRegistrationData).andExpect(status().isCreated());

        Assertions.assertEquals(4, userRepository.count());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Sidr@Sidr.com"));
    }

    @Test
    void updateUser() throws Exception {
        MockHttpServletResponse responseOld = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responseOld.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), responseOld.getContentType());
        Assertions.assertTrue(responseOld.getContentAsString().contains("Egor"));
        Assertions.assertFalse(responseOld.getContentAsString().contains("Sidr"));


        final MockHttpServletRequestBuilder request = put(CONTROLLER_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRegistrationData));

        utils.perform(request, userRepository.findById(1L).get().getEmail()).andExpect(status().isOk());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Sidr"));
        Assertions.assertFalse(response.getContentAsString().contains("Egor"));
    }

    @Test
    void deleteUser() throws Exception {
        utils.regUser(testRegistrationData);

        MockHttpServletResponse responseOld = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responseOld.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), responseOld.getContentType());
        Assertions.assertTrue(responseOld.getContentAsString().contains("Sidr"));
        Assertions.assertEquals(4, userRepository.count());

        Long userId = userRepository.findByEmail(testRegistrationData.getEmail()).get().getId();
        utils.perform(delete(CONTROLLER_PATH + "/" + userId), testRegistrationData.getEmail())
                        .andExpect(status().isOk());

        MockHttpServletResponse response = utils.perform(get(CONTROLLER_PATH))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertFalse(response.getContentAsString().contains("Sidr"));
        Assertions.assertEquals(3, userRepository.count());
    }

    @Test
    void validationTest() throws Exception {
        Assertions.assertEquals(3, userRepository.count());

        utils.regUser(new UserDto("e", "", "eg", "12"))
                .andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(3, userRepository.count());
    }

    @Test
    void createUserWithSameCredentialsTwice() throws Exception {
        Assertions.assertEquals(3, userRepository.count());

        utils.regUser(testRegistrationData)
                .andExpect(status().isCreated());

        utils.regUser(testRegistrationData)
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(4, userRepository.count());

    }
}

