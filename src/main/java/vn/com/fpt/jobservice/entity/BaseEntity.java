package vn.com.fpt.jobservice.entity;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

	@CreatedDate
	@Column(name = "created_at")
	protected Instant  createdAt;

	@CreatedBy
	@Column(name = "created_by")
	protected String createdBy;

	@LastModifiedDate
	@Column(name = "modified_at")
	protected Instant  modifiedAt;

	@LastModifiedBy
	@Column(name = "modified_by")
	protected String modifiedBy;
}