package vn.com.fpt.jobservice.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Value("${driver.class}")
	public String driverClass;
	@Value("${spring.datasource.url}")
	public String url;
	@Value("${spring.datasource.username}")
	public String username;
	@Value("${spring.datasource.password}")
	public String password;

	@Value("${pool.min.size}")
	public int minSize;
	@Value("${pool.max.size}")
	public int maxSize;
	@Value("${pool.max.idle}")
	public int maxIdle;
	@Value("${db.connection.timeout}")
	public int connectionTimeout;
	@Value("${db.idle.timeout}")
	public int idleTimeout;
	@Value("${db.max.life.timeout}")
	public int maxLifetime;

	
	@Bean
    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        dataSourceBuilder.driverClassName(driverClass);
        return dataSourceBuilder.build();
    }
	
}