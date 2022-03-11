package com.demo.springbootbatch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class BatchJobLauncherController {

    @Value(("${spring.application.name}"))
    private String appName;

    @Autowired
    @Qualifier("asyncJobLauncher")
    JobLauncher jobLauncher;

    @Autowired
    Job simpleJob;

    @Autowired
    JobRepository jobRepository;

    /*
     * @Autowired SimpleJobExplorer simpleJobExplorer;
     */

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobOperator jobOperator;

    /**
     * Method to launch the job
     *
     * @return String
     * @throws Exception
     */
    @GetMapping("/launch/job")
    public JobInstance jobLauncher() throws Exception {
        StringBuilder sb = new StringBuilder();
        Logger logger = LoggerFactory.getLogger(this.getClass());
        JobExecution jobExecution = null;
        try {
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            // job launcher is an interface for running the jobs
            jobExecution = jobLauncher.run(simpleJob, jobParameters);

            Long jobId = jobExecution.getJobId();
            sb.append("JobId: ").append(jobId).append(System.lineSeparator());

            /*JobInstance jobInstance = jobExecution.getJobInstance();
            Long instanceId = jobInstance.getInstanceId();
            sb.append("JobInstanceId: ").append(instanceId).append(System.lineSeparator());

            BatchStatus status = jobExecution.getStatus();
            sb.append("BatchStatus: ").append(status).append(System.lineSeparator());

            ExitStatus exitStatus = jobExecution.getExitStatus();
            sb.append("ExitStatus: ").append(exitStatus).append(System.lineSeparator());

            sb.append("JobName: ").append(simpleJob.getName()).append(System.lineSeparator());*/

			/*ExecutionContext executionContext = jobExecution.getExecutionContext();
			JobExecution execution = jobRepository.getLastJobExecution(simpleJob.getName(),
					new JobParametersBuilder().toJobParameters());*/
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return jobExecution.getJobInstance();
    }

    @GetMapping("/jobname/{jobName}")
    public String get(@PathVariable String jobName) {
        StringBuilder sb = new StringBuilder();
        JobInstance lastJobInstance =
                jobExplorer.getLastJobInstance(jobName);
        JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);

        sb.append(lastJobInstance).append(lastJobExecution);

        return sb.toString();
    }

    @GetMapping("/jobInstanceId/{jobInstanceId}")
    public JobExecution get(@PathVariable Long jobInstanceId) {
        StringBuilder sb = new StringBuilder();
        JobInstance lastJobInstance =
                jobExplorer.getJobInstance(jobInstanceId);
        JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
        sb.append(lastJobInstance).append(lastJobExecution);

        return lastJobExecution;
    }

    @DeleteMapping("/jobExecutionId/{jobExecutionId}")
    public boolean stop(@PathVariable Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        return jobOperator.stop(jobExecutionId);
    }

    @GetMapping("/jobExecutionId/{jobExecutionId}")
    public String getExecutionSummary(@PathVariable Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        return jobOperator.getSummary(jobExecutionId);
    }

    @PostMapping("/jobExecutionId/restart/{jobExecutionId}")
    public long restart(@PathVariable Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobInstanceAlreadyCompleteException, NoSuchJobException, JobParametersInvalidException, JobRestartException {
        return jobOperator.restart(jobExecutionId);
    }

    @PostMapping("/jobExecutionId/abandon/{jobExecutionId}")
    public JobExecution abandon(@PathVariable Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobInstanceAlreadyCompleteException, NoSuchJobException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException {
        return jobOperator.abandon(jobExecutionId);
    }

    /*
     * @GetMapping("/{jobInstanceId}") public JobInstance get(Long jobInstanceId) {
     * JobInstance lastJobInstance = simpleJobExplorer.get return lastJobInstance; }
     */
}