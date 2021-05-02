package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.hibernate.boot.Metadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.List;


@RestController
public class CustomController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

//   @GetMapping("/")
//   public String defaultMessage() {
//     return "Welcome to the Spring Boot Application Starter!";
//   }
	private RestTemplate restTemplate; 

	@Autowired
	private CustomMetadataRepository metadataRepository;

	@Autowired
	private DataSource datasource;
	// @Autowired
	// private TableMetadataRepository tableMetadataRepository;

    @CrossOrigin(origins = "*")
	@GetMapping("/")
	public String defaultMessage() {
		return "Welcome to the Spring Boot API!";
	}

  @CrossOrigin(origins = "*")
  @GetMapping("/transfermetadata")
  public ResponseEntity<String> mysqlmetadata() {
	String resp = "";
	String url2 = "jdbc:mysql://remotemysql.com:3306/P5PFV6W5fo?user=P5PFV6W5fo&password=9Lz27mwpuN";
	try{
		Connection conn;
		// conn = DriverManager.getConnection(url2);
		conn = datasource.getConnection();
		if (conn != null) {
			System.out.println("MySql database connection exists");
			String reqQuery = "SELECT table_name, column_name, data_type FROM information_schema.columns WHERE table_schema = 'P5PFV6W5fo'";
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(reqQuery);
			
			int count = 0;
			
			HashMap<String,CustomMetadata> hm = new HashMap<String,CustomMetadata>();
			while (result.next()){

				String tableName = result.getString("table_name");
				String columnName = result.getString("column_name");
				String dataType = result.getString("data_type");
				
				if(!hm.containsKey(tableName))
					hm.put(tableName, new CustomMetadata(tableName));

				hm.get(tableName).addColumnData(columnName, dataType);

				String output = "Record #%d: %s - %s - %s \n";
				resp += String.format(output, ++count, tableName, columnName, dataType);
				// System.out.println();
			}
			for(CustomMetadata i : hm.values())
				metadataRepository.save(i);
		}
	}catch(SQLException err){
		err.printStackTrace();
		return new ResponseEntity<String>( "Request Failed" , HttpStatus.INTERNAL_SERVER_ERROR);	
	}

	return new ResponseEntity<String>( "Request Success", HttpStatus.OK);
  }


}