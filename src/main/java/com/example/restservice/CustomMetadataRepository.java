package com.example.restservice;


import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomMetadataRepository extends MongoRepository<CustomMetadata, ArrayList> {
}