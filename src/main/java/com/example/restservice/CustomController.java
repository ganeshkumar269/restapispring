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
import java.util.ArrayList;


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
  public ResponseEntity<HashMap<String,CustomMetadata>> mysqlmetadata(@RequestParam(value="replace",defaultValue="false") boolean replace,
  				@RequestParam(value="update",defaultValue="false") boolean update){

	//if update is true existing columndata doesnt get affected only new columnData is added
	//if replace is true existing columnData is erased and new columnData is added

	HashMap<String,CustomMetadata> refHm = new HashMap<String,CustomMetadata>();
	HashMap<String,CustomMetadata> resHm = new HashMap<String,CustomMetadata>();

	if(update && replace) return new ResponseEntity<HashMap<String,CustomMetadata>>(new HashMap<String,CustomMetadata>(),HttpStatus.BAD_REQUEST);

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
			
			if(update || replace){
				List<CustomMetadata> metadataList = metadataRepository.findAll();
				
				for(CustomMetadata i : metadataList){
					refHm.put(i.tableName,i);
				}
			}
			while (result.next()){

				String tableName = result.getString("table_name");
				String columnName = result.getString("column_name");
				String dataType = result.getString("data_type");
				

				if(!resHm.containsKey(tableName))
					resHm.put(tableName, new CustomMetadata(tableName));

				resHm.get(tableName).addColumnData(columnName, dataType);

				String output = "Record #%d: %s - %s - %s \n";
				
				System.out.println(String.format(output, ++count, tableName, columnName, dataType));
			}

			//remove keys that are not required
			List<String> toBeRemoved = new ArrayList<String>();
			for(String i : refHm.keySet())
				if(!resHm.containsKey(i))
					toBeRemoved.add(i);
			for(String i : toBeRemoved) refHm.remove(i);

			if(update){
				for(String i : resHm.keySet()){
					if(refHm.containsKey(i)){
						for(HashMap<String,String> col : resHm.get(i).columnData){
							if(!refHm.get(i).columnData.contains(col))
								refHm.get(i).columnData.add(col);
						}
					} 
					else 
						refHm.put(i, resHm.get(i));
				}
			}
			else if(replace){

				for(String i : resHm.keySet()){
					if(refHm.containsKey(i))
						refHm.get(i).columnData = resHm.get(i).columnData;
					else
						refHm.put(i, resHm.get(i));
				}

			}
			else{
				refHm = resHm;
			}
			for(CustomMetadata i : refHm.values())
				metadataRepository.save(i);
		}
	}catch(SQLException err){
		err.printStackTrace();
		return new ResponseEntity<HashMap<String,CustomMetadata>> ( refHm , HttpStatus.INTERNAL_SERVER_ERROR);	
	}

	return new ResponseEntity<HashMap<String,CustomMetadata>>( refHm, HttpStatus.OK);
  }


}