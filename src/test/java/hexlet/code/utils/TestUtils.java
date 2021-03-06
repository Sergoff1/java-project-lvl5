package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@Component
public class TestUtils {

    public static final String TEST_USERNAME = "Shrek@Shrek.com";

    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "Shrek",
            "SBolot",
            "123456"
    );

    @Autowired(required = false)
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post("/api/users")
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = tokenService.getToken(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
