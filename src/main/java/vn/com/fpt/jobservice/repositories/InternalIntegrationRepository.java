package vn.com.fpt.jobservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.com.fpt.jobservice.entity.InternalIntegration;

@Repository
public interface InternalIntegrationRepository extends JpaRepository<InternalIntegration, Long> {
}