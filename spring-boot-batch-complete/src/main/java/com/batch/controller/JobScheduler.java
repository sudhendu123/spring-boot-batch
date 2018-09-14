package com.batch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {
	
	private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job processJob;

	//@Scheduled(cron = "2 * * * * ?")
	//@Scheduled(fixedRate = 60000)
	public String jobRunner() {
		logger.info("jobRunner() : scheduled");
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		JobExecution execution = null;
		try {
			execution = jobLauncher.run(processJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		//BatchStatus status = execution.getStatus();
		String status = execution.getStatus().getBatchStatus().toString();
		
		return status;
	}

}
