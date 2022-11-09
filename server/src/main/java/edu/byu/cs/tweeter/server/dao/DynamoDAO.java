package edu.byu.cs.tweeter.server.dao;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDAO {

    private static final Region region = Region.US_WEST_1;

    private static final DynamoDbClient ddb = DynamoDbClient.builder()
            .region(region)
            .build();

    protected static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddb)
            .build();

}
