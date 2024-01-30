package vn.com.fpt.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.utils.ClassUtils;

import java.util.Set;

@SpringBootApplication
@ComponentScan("vn.com.fpt.jobservice")
public class JobServiceApplication {
    public static void main(String[] args) {
        String packageName = "vn.com.fpt.jobservice.jobs";
        Set<Class> classes = ClassUtils.findAllClassesUsingClassLoader(packageName);
//        try {
//            for (Class clazz : classes) {
//                Class<?> jobClass = Class.forName(packageName + clazz.getName());
//                if (BaseJob.class.isAssignableFrom(jobClass)) {
//                    (? extends BaseJob) jobClazz = (Class<? extends BaseJob>) jobClass;
//                    jobClazz.get
//                }
//            }
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        SpringApplication.run(JobServiceApplication.class, args);
    }
}