package vn.com.fpt.jobservice.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "internal_integrations")
@Data
@EntityListeners(AuditingEntityListener.class)
public class InternalIntegration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "url")
  private String url;

  @Column(name = "method")
  private String method;

  @Column(name = "params")
  private String params;

  @Column(name = "headers")
  private String headers;

  @Column(name = "body")
  private String body;

  @Column(name = "outputConfig")
  private String outputConfig;

  @Column(name = "mappingConfig")
  private String mappingConfig;

  @Column(name = "auth")
  private String auth;
}
