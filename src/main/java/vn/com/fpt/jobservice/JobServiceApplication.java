package vn.com.fpt.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("vn.com.fpt.jobservice")
public class JobServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(JobServiceApplication.class, args);
	}
}