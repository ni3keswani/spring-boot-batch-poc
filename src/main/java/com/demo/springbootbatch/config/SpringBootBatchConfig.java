package com.demo.springbootbatch.config;

import com.demo.springbootbatch.step.MessageProcessor;
import com.demo.springbootbatch.step.MessageReader;
import com.demo.springbootbatch.step.MessageWriter;
import com.demo.springbootbatch.step.SimpleTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
public class SpringBootBatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value(("${spring.application.name}"))
    private String appName;

    @Bean("asyncJobLauncher")
    JobLauncher asyncJobLauncher(JobRepository jobRepository, TaskExecutor taskExecutor) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        jobLauncher.setTaskExecutor(taskExecutor);
        return jobLauncher;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("custom-user-thread");
    }

    @Bean
    JobExplorer jobExplorer() throws Exception {
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setDataSource(this.dataSource);
        return factoryBean.getObject();
    }

    /**
     * A Job is made up of many steps and each step is
     * a READ-PROCESS-WRITE task or a single operation task (tasklet).
     *
     * @return job
     */
    @Bean
    public Job simpleJob(Step step1) {
        return jobBuilderFactory.get(appName + "simpleJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    /**
     * Step consist of an ItemReader, ItemProcessor and an ItemWriter.
     *
     * @return step
     */
    @Bean
    public Step step1(MessageReader messageReader, MessageProcessor messageProcessor, MessageWriter messageWriter) {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(1)
                .reader(messageReader)
                .processor(messageProcessor)
                .writer(messageWriter)
                .build();
    }

    @Bean
    public Job simpleTaskletJob(Step simpleTaskletStep) {
        return jobBuilderFactory
                .get(appName+ "simpleTaskletJob")
                .incrementer(new RunIdIncrementer())
                .start(simpleTaskletStep)
                .build();
    }

    @Bean
    public Step simpleTaskletStep(SimpleTasklet simpleTasklet) {
        return stepBuilderFactory.get("simpleTaskletStep")
                .tasklet(simpleTasklet)
                .build();
    }
}
