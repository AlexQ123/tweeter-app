package edu.byu.cs.tweeter.server.dao;

import java.sql.Timestamp;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.bean.AuthTokenBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoAuthTokenDAO extends DynamoDAO implements AuthTokenDAO {

    // private static final long EXPIRE_TIME = 86400000;

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
    public boolean tokenFound(String token) {
        // The token is null or empty for some reason
//        if (token.isBlank()) {
//            return false;
//        }

        Key key = Key.builder()
                .partitionValue(token)
                .build();

        AuthTokenBean foundToken = authtokenTable.getItem(key);

        // The token is not found in the table
        if (foundToken == null) {
            return false;
        }

        // The token has expired
//        long currentTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
//        if (currentTimestamp - foundToken.getTimestamp() > EXPIRE_TIME) {
//            return false;
//        }

        return true;
    }

}
