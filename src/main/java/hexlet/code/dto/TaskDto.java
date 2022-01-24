package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    @NotNull
    @NotBlank
    private String name;

    private String description;

    private Long executorId;

    @NotNull
    private Long taskStatusId;

    private Set<Long> labelIds;
}
