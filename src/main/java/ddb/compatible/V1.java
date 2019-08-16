package ddb.compatible;
 import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

 /**
  * Dynamodb centroid records Item.
  */

 @DynamoDBTable(tableName = V1.TABLE_NAME)
 @Builder
 @Data
 @AllArgsConstructor
 @NoArgsConstructor
 public class V1 {
     public static final String TABLE_NAME = "table";

     @NonNull
     @DynamoDBHashKey(attributeName = "id")
     private String id;

 }