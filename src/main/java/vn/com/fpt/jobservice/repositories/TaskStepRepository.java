package vn.com.fpt.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.TaskStep;

@Repository
public interface TaskStepRepository extends JpaRepository<TaskStep, Long> {
}