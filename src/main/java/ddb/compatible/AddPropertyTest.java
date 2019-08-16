package ddb.compatible;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.lambda.Unchecked;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.ConsistentReads;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.google.common.collect.ImmutableMap;


public class AddPropertyTest {

    AmazonDynamoDB client;
    DynamoDBMapper mapper;
    DynamoDB db;


    public AddPropertyTest() {
        client = DynamoDBEmbedded.create().amazonDynamoDB();

        DynamoDBMapperConfig config = new DynamoDBMapperConfig.Builder().withConsistentReads(ConsistentReads.EVENTUAL).build();
        mapper = new DynamoDBMapper(client, config);

        db = new DynamoDB(client);

    }

    @Before
    public void setUp() {
        //  no sqlite4java-osx-x86_64-1.0.392 in java.library.path
        //
        createTables(Arrays.asList(V1.class));
    }

    @After
    public void tearDown() {
        deleteTables(Arrays.asList(V1.class));
    }

    @Test
    public void test_writeWithV2_readWithV1() {
        String id = "my id";

        V2 v2 = V2.builder()
                .id(id)
                .map(ImmutableMap.<String, String>builder()
                        .put("one", "1")
                        .build())
                .build();

        mapper.save(v2);
        V1 v1 = mapper.load(V1.class, id);

        assertEquals(id, v1.getId());

        V2 vtwo = mapper.load(V2.class, id);
        assertEquals(v2, vtwo);

        V3 v3 = mapper.load(V3.class, id);
        assertEquals(id, v3.getId());
    }

    private void createTables(List<Class<?>> domainClazz) {
        domainClazz.stream()
            .map(mapper::generateCreateTableRequest)
            .map(request -> request.withProvisionedThroughput(
                                    new ProvisionedThroughput()
                                        .withReadCapacityUnits(5L)
                                        .withWriteCapacityUnits(5L)))
            .forEach(client::createTable);
    }

    public void deleteTables(List<Class<?>> domainClazz) {
        List<Table> tables = domainClazz.stream()
            .map(mapper::generateDeleteTableRequest)
            .map(DeleteTableRequest::getTableName)
            .filter(this::isTableExists)
            .map(db::getTable)
            .collect(Collectors.toList());

        tables.forEach(Table::delete);
        tables.forEach(Unchecked.consumer(Table::waitForDelete));
    }

    private boolean isTableExists(String tableName) {
        try {
            client.describeTable(tableName);
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }
}
