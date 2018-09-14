package com.batch.reader;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.batch.model.Person;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public class CsvFileReader implements ItemReader<Person>{

	private final Logger logger = LoggerFactory.getLogger(CsvFileReader.class);
	private int nextPersonIndex;
	List<Person> personList = null;
	public CsvFileReader(){
		readCsv();
		// The value should be initialized before reading it in post-costruct or constructor or InitializingBean-afterPropertiesSet 
	}
	
	private List<Person> readCsv(){
		
		try {
			Resource res = new ClassPathResource("sample-data.csv");
			MappingIterator<Person> personIter = new CsvMapper().readerWithTypedSchemaFor(Person.class).readValues(res.getFile());
			personList = personIter.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		nextPersonIndex=0;
		return personList;
	}
	
	@Override
	public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		/*ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
				BatchConfiguration.class);
		Object obj=applicationContext.getBean(H2ItemWriter.class);*/
		logger.info("Perosn reader:");
		Person person=null;
		if(nextPersonIndex<personList.size()) {
			person=personList.get(nextPersonIndex);
			nextPersonIndex=nextPersonIndex+1;
		}
		
		return person;
	}

}
