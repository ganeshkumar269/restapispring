package com.example.restservice;


import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomMetadataRepository extends MongoRepository<CustomMetadata, ArrayList> {

//   public Logincreds findByUsername(String username);
//   public List<Logincreds> findByLastName(String lastName);

}