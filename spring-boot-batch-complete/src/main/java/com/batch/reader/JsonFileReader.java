package com.batch.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.batch.model.Person;

public class JsonFileReader implements ItemReader<Person>{
	private static final Logger log = LoggerFactory.getLogger(JsonFileReader.class);
	@Override
	public Person read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		/*ClassPathResource resource = new ClassPathResource("sample-data.csv");
		InputStream stream = resource.getInputStream();
		InputStreamReader streamReader = new InputStreamReader(stream);*/
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		JSONArray  jSONArray=null;
		Person person=new Person();
		Resource res = new ClassPathResource("Person.json");
		InputStream fileInputStream = res.getInputStream();
		Object obj = jsonParser.parse(new InputStreamReader(fileInputStream));
		jsonObject = (JSONObject) obj;
		person.setFirstName(jsonObject.get("firstName").toString());
		person.setLastName(jsonObject.get("lastName").toString());
		log.info("Person :{}",person);
		return person;
	}

}
