package vn.com.fpt.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByStartedAtDesc(String taskId);

    Optional<TaskHistory> findFirstByTaskIdAndStatus(String taskId, TaskStatus status);
}