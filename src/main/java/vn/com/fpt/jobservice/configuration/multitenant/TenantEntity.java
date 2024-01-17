package vn.com.fpt.jobservice.configuration.multitenant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "tenant_db")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TenantEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6364902041896237890L;
    @Id
    @Column(name = "db_id")
    private String dbId;
    @Column(name = "db_name")
    private String dbName;
    @Column(name = "db_url")
    private String dbUrl;
    @Column(name = "db_password")
    private String dbPassword;
    @Column(name = "db_user")
    private String dbUser;
    @Column(name = "driver_class")
    private String driverClass;
}
