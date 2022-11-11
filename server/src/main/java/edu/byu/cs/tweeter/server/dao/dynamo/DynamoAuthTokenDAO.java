package edu.byu.cs.tweeter.server.dao.dynamo;

import java.sql.Timestamp;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.bean.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO {

    private static final String TableName = "authtoken";

    private static final DynamoDbTable<AuthTokenBean> authtokenTable = enhancedClient.table(TableName, TableSchema.fromBean(AuthTokenBean.class));

    @Override
    public AuthToken addToken() {
        String token = UUID.randomUUID().toString();
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();

        try {
            AuthTokenBean authtoken = new AuthTokenBean();
            authtoken.setAuthtoken(token);
            authtoken.setTimestamp(currentTime);

            authtokenTable.putItem(authtoken);
        }
        catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return new AuthToken(token);
    }

    @Override
    public void deleteToken(String token) {
        try {
            Key key = Key.builder()
                    .partitionValue(token)
                    .build();

            authtokenTable.deleteItem(key);
        }
        catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public AuthToken tokenFound(String token) {
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        AuthTokenBean foundToken = authtokenTable.getItem(key);

        // The token is not found in the table
        if (foundToken == null) {
            return null;
        }

        return new AuthToken(foundToken.getAuthtoken(), String.valueOf(foundToken.getTimestamp()));
    }

}
