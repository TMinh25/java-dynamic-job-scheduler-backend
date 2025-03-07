package vn.com.fpt.jobservice.configuration;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import vn.com.fpt.jobservice.service.AppJobsListener;
import vn.com.fpt.jobservice.service.AppTriggerListener;
import vn.com.fpt.jobservice.service.JobService;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzSchedulerConfig {

    @Autowired
    DataSource dataSource;
    @Autowired
    JobService jobService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    @Lazy
    private AppTriggerListener triggerListener;
    @Autowired
    @Lazy
    private AppJobsListener jobsListener;

    /**
     * create scheduler
     *
     * @throws SchedulerException
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException, SchedulerException {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());

        // Register listeners to get notification on Trigger misfire etc
        factory.setGlobalTriggerListeners(triggerListener);
        factory.setGlobalJobListeners(jobsListener);

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);

        return factory;
    }

    /**
     * Configure quartz using properties file
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("application.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}