package vn.com.fpt.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.utils.TaskTypeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
    Optional<TaskType> findByName(String name);

    List<TaskType> findByType(TaskTypeType type);
}