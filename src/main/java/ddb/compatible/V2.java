package ddb.compatible;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@DynamoDBTable(tableName = V1.TABLE_NAME)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class V2 {

    @NonNull
    @DynamoDBHashKey(attributeName = "id")
    private String id;

    @NonNull
    @DynamoDBAttribute(attributeName = "map")
    private Map<String, String> map;

}