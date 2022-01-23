package hexlet.code.app.repository;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {

    Optional<Task> findFirst1ByAuthorIdOrExecutorId(Long authorId, Long executorId);

    Optional<Task> findFirst1ByTaskStatusId(Long taskStatusId);

    List<Task> findByLabels(Label label);
}
