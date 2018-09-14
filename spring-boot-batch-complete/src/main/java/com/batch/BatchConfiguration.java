package com.batch;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import com.batch.listner.JobCompletionNotificationListener;
import com.batch.model.Person;
import com.batch.processor.PersonItemProcessor;
import com.batch.reader.CsvFileReader;
import com.batch.writer.H2ItemWriter;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {
	private final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    /*@Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
            .dataSource(dataSource)
            .build();
    }*/

    
   /* @Bean
    public FlatFileItemReader<Person> csvPersonReader(){
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
            	logger.info("csvPersonReader");
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }*/

   /* @Bean
    public JsonFileReader reader() {
    	return new JsonFileReader();
    }*/
    
    public CsvFileReader csvFileReader() {
    	return new CsvFileReader();
    }
    
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }
    
    @Bean
    public H2ItemWriter writer() {
    	return new H2ItemWriter();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("jsonUserJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            // flow is used when step by step is needed ans start when one step to execute
            /*.flow(step1)
            .next(Step2)
            .end()*/
            .start(step1)
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
        		//.tasklet(tasklet) one step with all reader,propcer and writer
        	//this can be tasklet job.start(readLines()).next(processLines()).next(writeLines()) 
        		//readLines(),processLines(), writeLines() are tasklet of step
        		/*While Tasklets feel more natural for ‘one task after the other’ scenarios, 
        		chunks provide a simple solution to deal with paginated reads or 
        		situations where we don’t want to keep a significant amount of data in memory.*/
            .<Person, Person> chunk(1)
            /*Our job will read, process and write one lines at a time.*/
            /*instead of reading, processing and writing all the lines at once, 
            it’ll read, process and write a fixed amount of records (chunk) at a time.*/
            /*if chunk is 1 then read process and write*/
            /*if chunk is 2 then read,read process.process and write*/
            .reader(csvFileReader())
            .processor(processor())
            .writer(writer())
            .build();
    }
    
    @Bean
	public DataSource dataSource() {
		//org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(); // org.apache.tomcat.jdbc.pool.DataSource;
    	DriverManagerDataSource dataSource=new DriverManagerDataSource();
    	dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:tcp://localhost/~/test2");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		/*
		 * dataSource.setTestWhileIdle(testWhileIdle);
		 * dataSource.setTestOnBorrow(testOnBorrow);
		 * dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMills);
		 * dataSource.setValidationQuery(validationQuery);
		 */
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}
	
	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource());
		factory.setTransactionManager(transactionManager());
		factory.setTablePrefix("BATCH_");
		factory.afterPropertiesSet();
		return (JobRepository) factory.getObject();
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}
}
