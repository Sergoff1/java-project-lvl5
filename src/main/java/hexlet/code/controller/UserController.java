package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserServiceImpl userService;

    private final UserRepository userRepository;

    @Operation(summary = "Get list of All Users")
    @ApiResponse(responseCode = "200", description = "List of all Users", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get specific User by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No Users with such id")
    })
    @GetMapping("/{id}")
    public User getUserById(
            @Parameter(description = "Id of User to be found", required = true)
            @PathVariable final Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No users with such id"));
    }

    @Operation(summary = "Create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed", content =
                @Content(mediaType = "application/json"))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(
            @Parameter(description = "Data of User to be created", required = true)
            @RequestBody @Valid UserDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateKeyException("User with such email already exist");
        }

        return userService.createUser(dto);
    }

    @Operation(summary = "Update User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed"),
            @ApiResponse(responseCode = "404", description = "No Users with such id")
    })
    @PutMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User update(
            @Parameter(description = "Id of User to be updated", required = true)
            @PathVariable final Long id,
            @Parameter(description = "User data to save", required = true)
            @RequestBody @Valid final UserDto dto) {

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No users with such id"));

        return userService.updateUser(id, dto);
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "No Users with such id")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(
            @Parameter(description = "Id of User to be deleted", required = true)
            @PathVariable final Long id) {

        userService.deleteUser(id);
    }
}
