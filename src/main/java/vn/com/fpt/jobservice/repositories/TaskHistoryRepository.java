package vn.com.fpt.jobservice.repositories;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    Page<TaskHistory> findByTaskIdOrderByStartedAtDesc(Pageable pageable, String taskId);

    List<TaskHistory> findByTaskIdOrderByStartedAtDesc(String taskId);

    Optional<TaskHistory> findFirstByTaskIdAndStatus(String taskId, TaskStatus status);

    Optional<TaskHistory> findFirstByTaskIdOrderByStartedAtDesc(String taskId);

    Page<TaskHistory> findAll(Specification<TaskHistory> specification, Pageable pageable);

    default Page<TaskHistory> searchByString(Pageable pageable, String tenantId, String searchQuery) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "startedAt"));

        return findAll((Specification<TaskHistory>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("tenantId"), tenantId);
            if (!searchQuery.isEmpty()) {

                predicate = criteriaBuilder.and(
                        criteriaBuilder.like(root.get("taskId"), "%" + searchQuery + "%"),
                        criteriaBuilder.equal(root.get("tenantId"), tenantId)
                );
            }
            return predicate;
        }, pageableWithSort);
    }
}