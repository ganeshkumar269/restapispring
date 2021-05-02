package com.example.restservice;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomMetadata {

  @Id
  public String id;

  @Getter
  @Setter
  public String tableName;
  
  @Getter
  @Setter
  public ArrayList<HashMap<String,String>> columnData;

  public CustomMetadata(){}
  public CustomMetadata(String a){
	  tableName = a; columnData = new ArrayList<>();
  }
  
  public void addColumnData(String col,String type){
	  HashMap<String,String> t = new HashMap<String,String>();
	  t.put("column_name",col);
	  t.put("data_type",type);
	  columnData.add(t);
  }

//   public String toString(){
// 	  return String.format("username: %s, password: %s",username,password);
//   }
}