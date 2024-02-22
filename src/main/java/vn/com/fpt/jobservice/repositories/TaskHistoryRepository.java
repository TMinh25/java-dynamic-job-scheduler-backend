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
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    Page<TaskHistory> findByTaskIdOrderByStartedAtDesc(Pageable pageable, String taskId);

    List<TaskHistory> findByTaskIdOrderByStartedAtDesc(String taskId);

    Optional<TaskHistory> findFirstByTaskIdAndStatus(String taskId, TaskStatus status);


    Page<TaskHistory> findAll(Specification<TaskHistory> specification, Pageable pageable);

    default Page<TaskHistory> searchByString(Pageable pageable, String searchQuery) {
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "startedAt"));

        return findAll((Specification<TaskHistory>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!searchQuery.isEmpty()) {
                Predicate ticketIdPredicate = criteriaBuilder.isTrue(criteriaBuilder.literal(false));
//                Predicate phaseIdPredicate = criteriaBuilder.isTrue(criteriaBuilder.literal(false));

//                try {
//                    Long longValue = Long.parseLong(searchQuery);
//                    ticketIdPredicate = criteriaBuilder.equal(root.get("ticketId"), longValue);
//                    phaseIdPredicate = criteriaBuilder.equal(root.get("phaseId"), longValue);
//                } catch (Exception e) {
//                }

                predicate = criteriaBuilder.and(criteriaBuilder.or(criteriaBuilder.like(root.get("taskId"), "%" + searchQuery + "%"), ticketIdPredicate));
            }
            return predicate;
        }, pageableWithSort);
    }
}