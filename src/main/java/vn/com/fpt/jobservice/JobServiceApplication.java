package vn.com.fpt.jobservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;

@SpringBootApplication
@ComponentScan("vn.com.fpt.jobservice")
@EnableAutoConfiguration
public class JobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }
}