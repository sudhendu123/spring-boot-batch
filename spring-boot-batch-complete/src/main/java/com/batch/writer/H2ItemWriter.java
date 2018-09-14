package com.batch.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.batch.model.Person;

public class H2ItemWriter implements ItemWriter<Person>{

	private final Logger logger = LoggerFactory.getLogger(H2ItemWriter.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void write(List<? extends Person> user) throws Exception {
		String sql="INSERT INTO people (first_name, last_name) VALUES (?, ?)";
		logger.info("H2ItemWriter");
		Person usr = user.get(0);
		jdbcTemplate.update(sql,new Object[] {usr.getFirstName(),usr.getLastName()});
		
	}
}
