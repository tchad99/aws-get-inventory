package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;



public class InventoryGetFunctionHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context)   {
		// TODO Auto-generated method stub
		 JSONParser parser = new JSONParser();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		    JSONObject responseJson = new JSONObject();
		    ArrayList resultList = new ArrayList();
		//dynamo
		    AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
		    String tableName = "Inventory";
		    
		  
		    try {
		        JSONObject event =  (JSONObject) parser.parse(reader);
		        JSONObject responseBody = new JSONObject();
		 
		    
		    
		    if (event.get("pathParameters") != null) {
	            JSONObject pps = (JSONObject) event.get("pathParameters");
	            if (pps.get("id") != null) {
	                int id = Integer.parseInt((String) pps.get("id"));
	              //  result = dynamoDb.getTable(DYNAMODB_TABLE_NAME).getItem("id", id);
	            }
	        }
		    else
		    {
		    	
		    	 Table table = new DynamoDB(dynamoDB).getTable(tableName);
		            Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		            expressionAttributeValues.put(":actualPrice", 0);
		            
		            Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		            expressionAttributeNames.put("#actualPrice", "actualPrice");
		            expressionAttributeNames.put("#category", "category");
		            expressionAttributeNames.put("#description", "description");
		            expressionAttributeNames.put("#ID", "ID");
		            expressionAttributeNames.put("#listings", "listings");
		            expressionAttributeNames.put("#productCode", "productCode");
		            expressionAttributeNames.put("#subcategory", "subcategory");
		    	
		            ItemCollection<ScanOutcome> items = table.scan("#actualPrice <> :actualPrice",
		            		"#actualPrice,#subcategory,#productCode,#ID,#category,#description,#listings",// ProjectionExpression
		            		expressionAttributeNames, // ExpressionAttributeNames - not used in this example
		                    expressionAttributeValues);
		            //System.out.println("Scan of " + tableName + " for items with a price less than 100.");
		            Iterator<com.amazonaws.services.dynamodbv2.document.Item> iterator = items.iterator();
		            while (iterator.hasNext()) {
		            	resultList.add(iterator.next().toJSONPretty());
		            
		    	
		            }
		    	
		    	/*HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		         Condition condition = new Condition()
		             .withComparisonOperator(ComparisonOperator.GT.toString())
		             .withAttributeValueList(new AttributeValue().withN("1985"));
		         scanFilter.put("year", condition);
		         ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		         ScanResult scanResult = dynamoDB.scan(scanRequest);
		         System.out.println("Result: " + scanResult);
		        // responseJson.putAll(scanResult);
		         for (Map<String, AttributeValue> item : scanResult.getItems()){
		        	 resultList.add(item);
		        	
		         }*/
		         
		        
		         //responseJson.put("resultset",resultList);
		       //  responseJson.put("statusCode", 200);
		    }
		    
		//return null;
		
	}
	catch(Exception e)
		    {
		 responseJson.put("statusCode", e);
		 e.printStackTrace();
		    }
		    OutputStreamWriter writer;
			try {
				writer = new OutputStreamWriter(output, "UTF-8");
				 writer.write(    resultList.toString());
				    writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
  }
	
	
	
  /*  public  ArrayList handleRequest(InputStream input,OutputStream output, Context context) {
        context.getLogger().log("Input: " + input);
        // Create a connection to DynamoDB
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
       
         String tableName = "Inventory";
         
         
      //   Table table = dynamoDB.getTable(tableName);
         // Scan items for movies with a year attribute greater than 1985

         // Scan items for movies with a year attribute greater than 1985
         HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
         Condition condition = new Condition()
             .withComparisonOperator(ComparisonOperator.GT.toString())
             .withAttributeValueList(new AttributeValue().withN("1985"));
         scanFilter.put("year", condition);
         ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
         ScanResult scanResult = dynamoDB.scan(scanRequest);
         System.out.println("Result: " + scanResult);
         ArrayList resultList = new ArrayList();
         for (Map<String, AttributeValue> item : scanResult.getItems()){
        	 resultList .add(item);
        	}
         
         
         // TODO: implement your handler
        return  resultList; //"Hello from Lambda!";
    }*/




}
