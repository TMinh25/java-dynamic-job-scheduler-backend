package vn.com.fpt.jobservice.configuration.multitenant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TenantDataSource implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -2418234625461365801L;
	// private static final String DEFAULT_TENANT_ID = "default";
	private Map<String, DataSource> dataSources = new HashMap<>();

	// @Value("${driver.class}")
	// public String driverClass;
	// @Value("${db.url}")
	// public String url;
	// @Value("${db.username}")
	// public String username;
	// @Value("${db.password}")
	// public String password;
	//
	// @Value("${pool.min.size}")
	// public int minSize;
	// @Value("${pool.max.size}")
	// public int maxSize;
	// @Value("${pool.max.idle}")
	// public int maxIdle;
	// @Value("${db.connection.timeout}")
	// public int connectionTimeout;
	// @Value("${db.idle.timeout}")
	// public int idleTimeout;
	// @Value("${db.max.life.timeout}")
	// public int maxLifetime;

	@Autowired
	private TenantRepository configRepo;

	@PostConstruct
	public Map<String, DataSource> getAllTenantDS() {
		List<TenantEntity> dbList = configRepo.findAll();

		Map<String, DataSource> result = new HashMap<>();

		for (TenantEntity dBDetail : dbList) {
			if (dBDetail != null) {
				DataSourceBuilder<?> factory = DataSourceBuilder.create().driverClassName(dBDetail.getDriverClass())
						.username(dBDetail.getDbUser()).password(dBDetail.getDbPassword()).url(dBDetail.getDbUrl());
				DataSource ds = (DataSource) factory.build();
				result.put(dBDetail.getDbId(), ds);
			}
		}
		// result.put(DEFAULT_TENANT_ID, defaultDataSource());
		dataSources = result;
		return result;
	}

	public DataSource getDataSource(String dbId) {
		if (dataSources.get(dbId) != null) {
			return dataSources.get(dbId);
		}
		DataSource dataSource = createDataSource(dbId);
		if (dataSource != null) {
			dataSources.put(dbId, dataSource);
		}
		return dataSource;
	}

	private DataSource createDataSource(String dbId) {
		Optional<TenantEntity> db = configRepo.findById(dbId);
		if (db != null) {
			DataSourceBuilder<?> factory = DataSourceBuilder.create().driverClassName(db.get().getDriverClass())
					.username(db.get().getDbUser()).password(db.get().getDbPassword()).url(db.get().getDbUrl());
			DataSource ds = (DataSource) factory.build();
			return ds;
		}
		return null;
	}

	// public DataSource defaultDataSource() {
	// HikariConfig config = new HikariConfig();
	// config.setDriverClassName(driverClass);
	// config.setJdbcUrl(url);
	// config.setUsername(username);
	// config.setPassword(password);
	// config.setMinimumIdle(minSize);
	// config.setMaximumPoolSize(maxSize);
	// config.addDataSourceProperty("cachePrepStmts", "true");
	// config.addDataSourceProperty("prepStmtCacheSize", "250");
	// config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
	// config.setConnectionTimeout(connectionTimeout);
	// config.setIdleTimeout(idleTimeout);
	// config.setMaxLifetime(maxLifetime);
	// HikariDataSource ds = new HikariDataSource(config);
	//
	// return ds;
	// }
}
