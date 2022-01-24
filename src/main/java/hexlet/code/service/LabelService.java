package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

public interface LabelService {

    Label createLabel(LabelDto labelData);

    Label updateLabel(Long id, LabelDto labelData);
}
