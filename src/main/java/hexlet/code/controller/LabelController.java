package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    private final LabelService labelService;

    private final LabelRepository labelRepository;

    @Operation(summary = "Get list of All Labels")
    @ApiResponse(responseCode = "200", description = "List of all Labels", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class)))
    @GetMapping
    public List<Label> getAll() {
       return labelRepository.findAll();
    }

    @Operation(summary = "Get specific Label by it id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "404", description = "No Labels with such id")
    })
    @GetMapping(path = "/{id}")
    public Label getLabelById(
            @Parameter(description = "Id of Label to be found", required = true)
            @PathVariable final Long id) {

        return labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No labels with such id"));
    }

    @Operation(summary = "Create new Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed", content =
                @Content(mediaType = "application/json"))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(
            @Parameter(description = "Data of Label to be created", required = true)
            @RequestBody @Valid LabelDto labelData) {

        return labelService.createLabel(labelData);
    }

    @Operation(summary = "Update Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "422", description = "Data validation failed"),
            @ApiResponse(responseCode = "404", description = "No Labels with such id")
    })
    @PutMapping(path = "/{id}")
    public Label updateLabel(
            @Parameter(description = "Id of Label to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Label data to save", required = true)
            @RequestBody @Valid LabelDto labelData) {

        return labelService.updateLabel(id, labelData);
    }

    @Operation(summary = "Delete Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "No Labels with such id")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteLabel(
            @Parameter(description = "Id of Label to be deleted", required = true)
            @PathVariable Long id) {

        labelService.deleteLabel(id);
    }
}
