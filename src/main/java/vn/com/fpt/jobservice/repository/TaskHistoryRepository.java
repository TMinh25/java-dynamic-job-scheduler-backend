package vn.com.fpt.jobservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.com.fpt.jobservice.entity.TaskHistory;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
  Page<TaskHistory> findByTaskId(Pageable pageable, String taskId);
}