package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public Label createLabel(LabelDto labelData) {
        final Label label = new Label();

        label.setName(labelData.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelData) {
        final Label label = labelRepository.getById(id);

        label.setName(labelData.getName());
        return labelRepository.save(label);
    }
}
