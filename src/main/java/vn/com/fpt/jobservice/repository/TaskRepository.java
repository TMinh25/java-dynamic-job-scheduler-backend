package vn.com.fpt.jobservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.Predicate;

import vn.com.fpt.jobservice.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
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
  };
}