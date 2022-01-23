package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;

public interface LabelService {

    Label createLabel(LabelDto labelData);

    Label updateLabel(Long id, LabelDto labelData);
}
