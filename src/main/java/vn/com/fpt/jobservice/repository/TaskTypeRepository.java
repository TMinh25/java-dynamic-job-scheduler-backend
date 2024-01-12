package vn.com.fpt.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.com.fpt.jobservice.entity.TaskType;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
}