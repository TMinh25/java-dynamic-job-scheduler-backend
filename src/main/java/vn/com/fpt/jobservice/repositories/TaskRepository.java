package vn.com.fpt.jobservice.repositories;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByStatusAndNextInvocationBefore(TaskStatus status, Date nextInvocation);

    Optional<Task> findByJobUUID(String jobUUID);

    Page<Task> findAll(Specification<Task> specification, Pageable pageable);

    default Page<Task> searchByString(Pageable pageable, String searchQuery) {
        return findAll((Specification<Task>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!searchQuery.isBlank()) {
                Predicate tPredicate = criteriaBuilder.isTrue(criteriaBuilder.literal(false));

                try {
                    Long longValue = Long.parseLong(searchQuery);
                    tPredicate = criteriaBuilder.equal(root.get("ticketId"), longValue);
                } catch (Exception e) {
                }

                predicate = criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get("id"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("name"), "%" + searchQuery + "%"),
                                tPredicate));
            }
            return predicate;
        }, pageable);
    }

}