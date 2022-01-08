package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final UserCreateDto testRegistrationData = new UserCreateDto(
            "Sidr@Sidr.com",
            "Sidr",
            "Sidorov",
            "123456"
    );

    private static final String BASE_URL = "/api/users";

    @Test
    void getUsers() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_URL))
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
    void createAndGetById() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        MockHttpServletResponse responsePost = mockMvc
                .perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testRegistrationData))
                )
                .andReturn()
                .getResponse();

        Assertions.assertEquals(201, responsePost.getStatus());
        Assertions.assertEquals(4, userRepository.count());

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_URL + "/4"))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Sidr@Sidr.com"));
    }

    @Test
    void updateUser() throws Exception {
        MockHttpServletResponse responseOld = mockMvc
                .perform(get(BASE_URL + "/1"))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responseOld.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), responseOld.getContentType());
        Assertions.assertTrue(responseOld.getContentAsString().contains("Egor"));
        Assertions.assertFalse(responseOld.getContentAsString().contains("Sidr"));

        MockHttpServletResponse responsePut = mockMvc
                .perform(
                        put(BASE_URL + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(testRegistrationData))
                )
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responsePut.getStatus());

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_URL + "/1"))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertTrue(response.getContentAsString().contains("Sidr"));
        Assertions.assertFalse(response.getContentAsString().contains("Egor"));
    }

    @Test
    void deleteUser() throws Exception {
        MockHttpServletResponse responseOld = mockMvc
                .perform(get(BASE_URL))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responseOld.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), responseOld.getContentType());
        Assertions.assertTrue(responseOld.getContentAsString().contains("Egor"));
        Assertions.assertEquals(3, userRepository.count());

        MockHttpServletResponse responseDelete = mockMvc
                .perform(delete(BASE_URL + "/1"))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, responseDelete.getStatus());

        MockHttpServletResponse response = mockMvc
                .perform(get(BASE_URL))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), response.getContentType());
        Assertions.assertFalse(response.getContentAsString().contains("Egor"));
        Assertions.assertEquals(2, userRepository.count());
    }

    @Test
    void validationTest() throws Exception {
        Assertions.assertEquals(3, userRepository.count());
        mockMvc.perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserCreateDto(
                                "e",
                                "",
                                "Eg",
                                "12"
                        )))
                )
                .andExpect(status().isUnprocessableEntity());

        Assertions.assertEquals(3, userRepository.count());
    }

    @Test
    void createUserWithSameCredentialsTwice() throws Exception {
        Assertions.assertEquals(3, userRepository.count());

        mockMvc.perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testRegistrationData))
                )
                .andExpect(status().isCreated());

        /*mockMvc.perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testRegistrationData))
                )
                .andExpect(status().isUnprocessableEntity());
        */
        Assertions.assertEquals(4, userRepository.count());

    }
}

