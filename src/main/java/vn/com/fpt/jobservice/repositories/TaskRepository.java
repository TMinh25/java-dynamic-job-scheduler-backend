package vn.com.fpt.jobservice.repositories;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByStatusAndNextInvocationBefore(TaskStatus status, Date nextInvocation);

    Optional<Task> findByTicketIdAndPhaseId(Long ticketId, Long phaseId);

    Optional<Task> findByJobUUID(String jobUUID);

    Page<Task> findAll(Specification<Task> specification, Pageable pageable);

    default Page<Task> searchByString(Pageable pageable, String searchQuery) {
        // Add sorting by createdAt in descending order
        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return findAll((Specification<Task>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (!searchQuery.isEmpty()) {
                Predicate ticketIdPredicate = criteriaBuilder.isTrue(criteriaBuilder.literal(false));
                Predicate phaseIdPredicate = criteriaBuilder.isTrue(criteriaBuilder.literal(false));

                try {
                    Long longValue = Long.parseLong(searchQuery);
                    ticketIdPredicate = criteriaBuilder.equal(root.get("ticketId"), longValue);
                    phaseIdPredicate = criteriaBuilder.equal(root.get("phaseId"), longValue);
                } catch (Exception e) {
                }

                predicate = criteriaBuilder.and(
                        criteriaBuilder.or(
                                // criteriaBuilder.like(root.get("id"), "%" + searchQuery + "%"),
                                criteriaBuilder.like(root.get("name"), "%" + searchQuery + "%"),
                                ticketIdPredicate,
                                phaseIdPredicate));
            }
            return predicate;
        }, pageableWithSort);
    }
}