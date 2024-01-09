package vn.com.fpt.jobservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.TaskEntity;


import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}