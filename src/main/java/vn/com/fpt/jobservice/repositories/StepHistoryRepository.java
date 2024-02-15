package vn.com.fpt.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.StepHistory;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface StepHistoryRepository extends JpaRepository<StepHistory, Long> {
    List<StepHistory> findByTaskHistoryIdOrderByStepDesc(Long taskHistoryId);

    Optional<StepHistory> findFirstByTaskHistoryIdAndStatus(Long taskHistoryId, TaskStatus status);
}
