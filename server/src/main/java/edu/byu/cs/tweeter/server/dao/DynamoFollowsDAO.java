package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.bean.FollowsBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoFollowsDAO extends DynamoDAO implements FollowsDAO {

    private static final String TableName = "follows";

    private static final DynamoDbTable<FollowsBean> followsTable = enhancedClient.table(TableName, TableSchema.fromBean(FollowsBean.class));

    @Override
    public List<User> getFollowees(String followerHandle, int pageSize, String lastFollowee) {
        List<FollowsBean> followees = new ArrayList<>();

        try {
            Key key = Key.builder()
                    .partitionValue(followerHandle)
                    .build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

            if (isNonEmptyString(lastFollowee)) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(followerHandle).build());
                startKey.put("followee_handle", AttributeValue.builder().s(lastFollowee).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            QueryEnhancedRequest request = requestBuilder.build();

            followsTable.query(request).items().stream().limit(pageSize).forEach(f -> followees.add(f));

            // how do I check if there's more pages?
        }
        catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null;
    }

    @Override
    public List<User> getFollowers(String followeeHandle, int pageSize, String lastFollower) {
        return null;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

}
