package vn.com.fpt.jobservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.com.fpt.jobservice.entity.TaskType;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
	Optional<TaskType> findByName(String name);
}